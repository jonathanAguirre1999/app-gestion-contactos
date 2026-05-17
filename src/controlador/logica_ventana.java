package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vista.ventana;
import modelo.*;

public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {
	private ventana delegado; 
	private String nombres, email, telefono, categoria=""; 
	private persona persona; 
	private List<persona> contactos; 
	private boolean favorito = false; 

	// sincroniza hilos y evita corrupción de datos.
	private final Object lockManejoDatos = new Object(); 
	// controla el hilo de búsqueda actual y lo cancela si el usuario teclea muy rápido.
	private SwingWorker<RowFilter<Object, Object>, Void> workerBusquedaActual;

	public logica_ventana(ventana delegado, persona persona) {
	    this.delegado = delegado;
	    this.persona = persona;
	    cargarContactosRegistrados(); 
	    
	    this.delegado.btn_add.addActionListener(this);
	    this.delegado.btn_eliminar.addActionListener(this);
	    this.delegado.btn_modificar.addActionListener(this);
	    this.delegado.tbl_contactos.getSelectionModel().addListSelectionListener(this);
	    this.delegado.cmb_categoria.addItemListener(this);
	    this.delegado.chb_favorito.addItemListener(this);
	    
	    inicializarEventos(); 
	}

	private void incializacionCampos() {
		nombres = delegado.txt_nombres.getText();
		email = delegado.txt_email.getText();
		telefono = delegado.txt_telefono.getText();
	}

	private void cargarContactosRegistrados() {
		delegado.barraProgreso.setVisible(true);
	    delegado.barraProgreso.setValue(0);
	    delegado.barraProgreso.setString("Cargando base de datos...");
	    
	    SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
	    	@Override
	    	protected Void doInBackground() throws Exception {
	    		try {
	    			// sincroniza al leer para asegurar integridad inicial
	    			synchronized(lockManejoDatos) {
	    				contactos = new personaDAO(new persona()).leerArchivo();
	    			}
	                for (int i = 0; i <= 100; i += 25) { 
	                    Thread.sleep(150); 
	                    publish(i);
	                }
	            } catch (IOException e) {
	            	mostrarNotificacionAsync("Existen problemas al cargar los contactos");
	                e.printStackTrace();
	            }
	            return null;
	    	}
	    	
	    	@Override
	    	protected void process(List<Integer> chunks) {
	    		int progress = chunks.get(chunks.size() - 1);
	            delegado.barraProgreso.setValue(progress);
	    	}
	    	
	    	@Override
	        protected void done() {
	    		delegado.modeloTabla.setRowCount(0); 
	            if (contactos != null) {
	                for (persona contacto : contactos) {
	                    delegado.modeloTabla.addRow(new Object[]{
	                        contacto.getNombre(),
	                        contacto.getTelefono(),
	                        contacto.getEmail(),
	                        contacto.getCategoria(),
	                        contacto.isFavorito() ? "Sí" : "No"
	                    });
	                }
	            }
	            delegado.barraProgreso.setString("Carga completa");
	            new Timer().schedule(new TimerTask() {
	                @Override
	                public void run() {
	                    delegado.barraProgreso.setVisible(false);
	                }
	            }, 1000);
	        }
	    };
		worker.execute();
	}

	private void limpiarCampos() {
	    delegado.txt_nombres.setText("");
	    delegado.txt_telefono.setText("");
	    delegado.txt_email.setText("");
	    categoria = "";
	    favorito = false;
	    delegado.chb_favorito.setSelected(favorito);
	    delegado.cmb_categoria.setSelectedIndex(0);
	    incializacionCampos();
	    cargarContactosRegistrados();
	}

	// helper para notificaciones asíncronas en la UI sin colgar hilos secundarios
	private void mostrarNotificacionAsync(String mensaje) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(delegado, mensaje);
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		incializacionCampos(); 

	    if (e.getSource() == delegado.btn_add) {
	        if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
	            if ((!categoria.equals("Elija una Categoria")) && (!categoria.equals(""))) {
	                
	            	// valida en segundo plano
	            	SwingWorker<Boolean, Void> workerValidacion = new SwingWorker<Boolean, Void>() {
						@Override
						protected Boolean doInBackground() throws Exception {
							long inicioValidacion = System.currentTimeMillis();
							boolean duplicado = false;
							
							// sincroniza la lectura
							synchronized(lockManejoDatos) {
								if(contactos != null) {
									for(persona p : contactos) {
										if(p.getEmail().equalsIgnoreCase(email) || p.getNombre().equalsIgnoreCase(nombres)) {
											duplicado = true;
											break;
										}
									}
								}
							}
							System.out.println("TIEMPO VALIDACIÓN CONCURRENTE: " + (System.currentTimeMillis() - inicioValidacion) + " ms");
							return duplicado;
						}

						@Override
						protected void done() {
							try {
								boolean existe = get();
								if (existe) {
									mostrarNotificacionAsync("Error: El contacto ya se encuentra registrado");
								} else {
									synchronized(lockManejoDatos) {
										persona = new persona(nombres, telefono, email, categoria, favorito);
						                new personaDAO(persona).escribirArchivo();
									}
					                limpiarCampos();
					                mostrarNotificacionAsync("Contacto Registrado");
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					};
					workerValidacion.execute();
	            	
	            } else {
	            	mostrarNotificacionAsync("Elija una Categoria");
	            }
	        } else {
	        	mostrarNotificacionAsync("Todos los campos deben ser llenados");
	        }
	    } else if (e.getSource() == delegado.btn_eliminar) {
	        eliminarContacto();
	    } else if (e.getSource() == delegado.btn_modificar) {
	        editarContacto();
	    }
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) { 
	        int index = delegado.getIndiceContactoSeleccionado();
	        if (index >= 0 && contactos != null && index < contactos.size()) {
	            cargarContacto(index);
	        }
	    }
	}

	private void cargarContacto(int index) {
	    delegado.txt_nombres.setText(contactos.get(index).getNombre());
	    delegado.txt_telefono.setText(contactos.get(index).getTelefono());
	    delegado.txt_email.setText(contactos.get(index).getEmail());
	    delegado.chb_favorito.setSelected(contactos.get(index).isFavorito());
	    delegado.cmb_categoria.setSelectedItem(contactos.get(index).getCategoria());
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
	    if (e.getSource() == delegado.cmb_categoria) {
	        categoria = delegado.cmb_categoria.getSelectedItem().toString();
	    } else if (e.getSource() == delegado.chb_favorito) {
	        favorito = delegado.chb_favorito.isSelected();
	    }
	}
	
	private void inicializarEventos() {
		this.delegado.addEditarListener(e -> editarContacto());
		this.delegado.addEliminarListener(e -> eliminarContacto());
		this.delegado.addExportarListener(e -> exportarContactos());
		
		this.delegado.txt_buscar.addKeyListener(new KeyAdapter() {
	        public void keyReleased(KeyEvent evt) {
	            String texto = delegado.txt_buscar.getText();
	            
	            // cancela la búsqueda anterior si el usuario sigue tecleando
	            if(workerBusquedaActual != null && !workerBusquedaActual.isDone()) {
	            	workerBusquedaActual.cancel(true);
	            }
	            
	            // busqueda concurrente
	            workerBusquedaActual = new SwingWorker<RowFilter<Object, Object>, Void>() {
					@Override
					protected RowFilter<Object, Object> doInBackground() throws Exception {
						long inicioBusqueda = System.currentTimeMillis();
						RowFilter<Object, Object> filtro = null;
						if (texto.trim().length() > 0) {
							filtro = RowFilter.regexFilter("(?i)" + texto);
						}
						System.out.println("TIEMPO BÚSQUEDA CONCURRENTE: " + (System.currentTimeMillis() - inicioBusqueda) + " ms");
						return filtro;
					}

					@Override
					protected void done() {
						if (!isCancelled()) {
							try {
								delegado.sorter.setRowFilter(get());
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				};
				workerBusquedaActual.execute();
	        }
	    });
		
		this.delegado.tbl_contactos.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					eliminarContacto();
				}
				else if (e.getKeyCode() == KeyEvent.VK_F2) {
					editarContacto();
				}
				else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() && e.isShiftDown()) {
					exportarContactos();
				}
			}
		});
	}
	
	private void eliminarContacto() {
		int index = delegado.getIndiceContactoSeleccionado();
	    if (index != -1) {
	        int confirmar = JOptionPane.showConfirmDialog(delegado, "¿Deseas eliminar este contacto?", "Confirmar", JOptionPane.YES_NO_OPTION);
	        if (confirmar == JOptionPane.YES_OPTION) {
	        	// hilo independiente y bloqueo para eliminar
	        	new Thread(() -> {
	        		synchronized(lockManejoDatos) {
			            contactos.remove(index); 
			            try {
			                new personaDAO(new persona()).actualizarContactos(contactos);
			                SwingUtilities.invokeLater(() -> limpiarCampos()); 
			                mostrarNotificacionAsync("Contacto eliminado correctamente.");
			            } catch (IOException ex) {
			            	mostrarNotificacionAsync("Error al eliminar el contacto en el archivo.");
			                ex.printStackTrace();
			            }
	        		}
	        	}).start();
	        }
	    } else {
	    	mostrarNotificacionAsync("Seleccione un contacto para eliminarlo.");
	    }
    }
	
	private void editarContacto() {
	    int index = delegado.getIndiceContactoSeleccionado();
	    if (index != -1) {
	        incializacionCampos(); 
	        if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
	            
	        	// hilo independiente y sincronización para edición
	        	Thread hiloEdicion = new Thread(() -> {
	        		synchronized(lockManejoDatos) {
			            persona p = contactos.get(index);
			            p.setNombre(nombres);
			            p.setTelefono(telefono);
			            p.setEmail(email);
			            p.setCategoria(categoria);
			            p.setFavorito(favorito);
		
			            try {
			                new personaDAO(new persona()).actualizarContactos(contactos);
			                SwingUtilities.invokeLater(() -> limpiarCampos());
			                mostrarNotificacionAsync("Contacto modificado correctamente");
			            } catch (IOException ex) {
			            	mostrarNotificacionAsync("Error al modificar el contacto");
			                ex.printStackTrace();
			            }
	        		}
	        	});
	        	hiloEdicion.start();
	        } else {
	        	mostrarNotificacionAsync("Todos los campos deben estar llenos para modificaciones");
	        }
	    } else {
	    	mostrarNotificacionAsync("Seleccione un contacto para editar su información");
	    }
	}
	
	private void exportarContactos() {
	    if (contactos == null || contactos.isEmpty()) {
	    	mostrarNotificacionAsync("No hay contactos para exportar");
	        return;
	    }

	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Exportar a CSV");
	    
	    int seleccion = fileChooser.showSaveDialog(delegado);
	    if (seleccion == JFileChooser.APPROVE_OPTION) {
	        File archivoDestino = fileChooser.getSelectedFile();
	        String rutaOriginal = archivoDestino.getAbsolutePath();
	        final String ruta = rutaOriginal.toLowerCase().endsWith(".csv") ? rutaOriginal : rutaOriginal + ".csv";

	        // hilo múltiple para exportación en segundo plano y sincronización
	        Thread hiloExportacion = new Thread(() -> {
	        	long inicioExportacion = System.currentTimeMillis();
	        	
	        	synchronized(lockManejoDatos) {
			        try (FileWriter fw = new FileWriter(ruta)) {
			            fw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO\n");
			            for (persona p : contactos) {
			                fw.write(p.datosContacto() + "\n");
			            }
			            mostrarNotificacionAsync("Contactos exportados con éxito a:\n" + ruta);
			        } catch (IOException ex) {
			        	mostrarNotificacionAsync("Error al intentar exportar el archivo CSV");
			            ex.printStackTrace();
			        }
	        	}
		        System.out.println("TIEMPO EXPORTACIÓN CONCURRENTE: " + (System.currentTimeMillis() - inicioExportacion) + " ms");
	        });
	        hiloExportacion.start();
	    }
	}
}