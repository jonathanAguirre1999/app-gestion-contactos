package controlador;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vista.ventana;
import modelo.*;

//Definición de la clase logica_ventana que implementa tres interfaces para manejar eventos.
public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {
	private ventana delegado; // Referencia a la ventana principal que contiene la GUI.
	private String nombres, email, telefono, categoria=""; // Variables para almacenar datos del contacto.
	private persona persona; // Objeto de tipo persona, que representa un contacto.
	private List<persona> contactos; // Lista de objetos persona que representa todos los contactos.
	private boolean favorito = false; // Booleano que indica si un contacto es favorito.

	// Constructor que inicializa la clase y configura los escuchadores de eventos para los componentes de la GUI.
	public logica_ventana(ventana delegado, persona persona) {
		// Asigna la ventana recibida como parámetro a la variable de instancia delegado.
		//se asigna tambien una instancia del contacto para fin de uso de los listeners
	    this.delegado = delegado;
	    this.persona = persona;
	    // Carga los contactos almacenados al inicializar.
	    cargarContactosRegistrados(); 
	    // Registra los ActionListener para los botones de la GUI.
	    this.delegado.btn_add.addActionListener(this);
	    this.delegado.btn_eliminar.addActionListener(this);
	    this.delegado.btn_modificar.addActionListener(this);
	    // Registra los ListSelectionListener para la lista de contactos.
	    //this.delegado.lst_contactos.addListSelectionListener(this); --> SE ESTA USANDO EL METODO DE TABLAS
	    this.delegado.tbl_contactos.getSelectionModel().addListSelectionListener(this);
	    // Registra los ItemListener para el JComboBox de categoría y el JCheckBox de favoritos.
	    this.delegado.cmb_categoria.addItemListener(this);
	    this.delegado.chb_favorito.addItemListener(this);
	    
	    inicializarEventos(); //inicializa los listeners correspondientes a las nuevas funcionalidades
	}

	// Método privado para inicializar las variables con los valores ingresados en la GUI.
	private void incializacionCampos() {
		// Obtiene el texto ingresado en los campos de nombres, email y teléfono de la GUI.
		nombres = delegado.txt_nombres.getText();
		email = delegado.txt_email.getText();
		telefono = delegado.txt_telefono.getText();
	}

	// Método privado para cargar los contactos almacenados desde un archivo.
	private void cargarContactosRegistrados() {
		delegado.barraProgreso.setVisible(true);
	    delegado.barraProgreso.setValue(0);
	    delegado.barraProgreso.setString("Cargando base de datos...");
	    
	    SwingWorker <Void, Integer> worker = new SwingWorker<Void, Integer>() {
	    	
	    	@Override
	    	protected Void doInBackground() throws Exception {
	    		try {
	                contactos = new personaDAO(new persona()).leerArchivo();
	                
	                // se simula demora para que la barra se pueda apreciar
	                for (int i = 0; i <= 100; i += 25) { 
	                    Thread.sleep(150); 
	                    publish(i);
	                }
	            } catch (IOException e) {
	                JOptionPane.showMessageDialog(delegado, "Existen problemas al cargar los contactos");
	                e.printStackTrace();
	            }
	            return null;
	    	}
	    	
	    	@Override
	    	protected void process(List<Integer> chunks) {
	    		// carga la barra con el utilmo valor
	    		int progress = chunks.get(chunks.size() - 1);
	            delegado.barraProgreso.setValue(progress);
	    	}
	    	
	    	@Override
	        protected void done() {
	            //al terminar limpia todo 
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
	            
	            //la barra se oculta sola despues de un segundo de terminar la tarea
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

	// Método privado para limpiar los campos de entrada en la GUI y reiniciar variables.
	private void limpiarCampos() {
		// Limpia los campos de nombres, email y teléfono en la GUI.
	    delegado.txt_nombres.setText("");
	    delegado.txt_telefono.setText("");
	    delegado.txt_email.setText("");
	    // Reinicia las variables de categoría y favorito.
	    categoria = "";
	    favorito = false;
	    // Desmarca la casilla de favorito y establece la categoría por defecto.
	    delegado.chb_favorito.setSelected(favorito);
	    delegado.cmb_categoria.setSelectedIndex(0);
	    // Reinicia las variables con los valores actuales de la GUI.
	    incializacionCampos();
	    // Recarga los contactos en la lista de contactos de la GUI.
	    cargarContactosRegistrados();
	}

	// Método que maneja los eventos de acción (clic) en los botones.
	@Override
	public void actionPerformed(ActionEvent e) {
		incializacionCampos(); // Inicializa las variables con los valores actuales de la GUI.

	    // Verifica si el evento proviene del botón "Agregar".
	    if (e.getSource() == delegado.btn_add) {
	        // Verifica si los campos de nombres, teléfono y email no están vacíos.
	        if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
	            // Verifica si se ha seleccionado una categoría válida.
	            if ((!categoria.equals("Elija una Categoria")) && (!categoria.equals(""))) {
	                // Crea un nuevo objeto persona con los datos ingresados y lo guarda.
	                persona = new persona(nombres, telefono, email, categoria, favorito);
	                new personaDAO(persona).escribirArchivo();
	                // Limpia los campos después de agregar el contacto.
	                limpiarCampos();
	                // Muestra un mensaje de éxito.
	                JOptionPane.showMessageDialog(delegado, "Contacto Registrado!!!");
	            } else {
	                // Muestra un mensaje de advertencia si no se ha seleccionado una categoría válida.
	                JOptionPane.showMessageDialog(delegado, "Elija una Categoria!!!");
	            }
	        } else {
	            // Muestra un mensaje de advertencia si algún campo está vacío.
	            JOptionPane.showMessageDialog(delegado, "Todos los campos deben ser llenados!!!");
	        }
	    } else if (e.getSource() == delegado.btn_eliminar) {
	        eliminarContacto();
	    } else if (e.getSource() == delegado.btn_modificar) {
	        editarContacto();
	    }
	}

	// Método que maneja los eventos de selección en la lista de contactos.
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// manejo de idempotencia para el evento del click
		if (!e.getValueIsAdjusting()) { 
	        int index = delegado.getIndiceContactoSeleccionado();
	        if (index >= 0 && contactos != null && index < contactos.size()) {
	            cargarContacto(index);
	        }
	    }
	}

	// Método privado para cargar los datos del contacto seleccionado en los campos de la GUI.
	private void cargarContacto(int index) {
		// Establece el nombre del contacto en el campo de texto de nombres.
	    delegado.txt_nombres.setText(contactos.get(index).getNombre());
	    // Establece el teléfono del contacto en el campo de texto de teléfono.
	    delegado.txt_telefono.setText(contactos.get(index).getTelefono());
	    // Establece el correo electrónico del contacto en el campo de texto de correo electrónico.
	    delegado.txt_email.setText(contactos.get(index).getEmail());
	    // Establece el estado de favorito del contacto en el JCheckBox de favorito.
	    delegado.chb_favorito.setSelected(contactos.get(index).isFavorito());
	    // Establece la categoría del contacto en el JComboBox de categoría.
	    delegado.cmb_categoria.setSelectedItem(contactos.get(index).getCategoria());
	}

	// Método que maneja los eventos de cambio de estado en los componentes cmb_categoria y chb_favorito.
	@Override
	public void itemStateChanged(ItemEvent e) {
		// Verifica si el evento proviene del JComboBox de categoría.
	    if (e.getSource() == delegado.cmb_categoria) {
	        // Obtiene el elemento seleccionado en el JComboBox y lo convierte en una cadena.
	        categoria = delegado.cmb_categoria.getSelectedItem().toString();
	        // Actualiza la categoría seleccionada en la variable "categoria".
	    } else if (e.getSource() == delegado.chb_favorito) {
	        // Verifica si el evento proviene del JCheckBox de favorito.
	        favorito = delegado.chb_favorito.isSelected();
	        // Obtiene el estado seleccionado del JCheckBox y actualiza el estado de favorito en la variable "favorito".
	    }
	}
	
	//se encarga de los eventos relacionados con atajos de teclado y menu desplegable
	private void inicializarEventos() {
		this.delegado.addEditarListener(e -> editarContacto());
		this.delegado.addEliminarListener(e -> eliminarContacto());
		this.delegado.addExportarListener(e -> exportarContactos());
		
		// realiza un filtrado en tiempo real 
		this.delegado.txt_buscar.addKeyListener(new KeyAdapter() {
	        public void keyReleased(KeyEvent evt) {
	            String texto = delegado.txt_buscar.getText();
	            if (texto.trim().length() == 0) {
	                delegado.sorter.setRowFilter(null);
	            } else {
	                // permite ignorar mayusculas y minusculas
	                delegado.sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
	            }
	        }
	    });
		
		// permite usar los atajos de teclado
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
	
	//logica de eliminacion de contacto
	private void eliminarContacto() {
		int index = delegado.getIndiceContactoSeleccionado();
	    
	    if (index != -1) {
	        int confirmar = JOptionPane.showConfirmDialog(delegado, "¿Seguro que deseas eliminar este contacto?", "Confirmar", JOptionPane.YES_NO_OPTION);
	        if (confirmar == JOptionPane.YES_OPTION) {
	            contactos.remove(index); // se elimina de la lista
	            try {
	                //sobreescribe el archivo creado en el DAO
	                new personaDAO(new persona()).actualizarContactos(contactos);
	                limpiarCampos(); 
	                delegado.mostrarMensaje("Contacto eliminado correctamente.");
	            } catch (IOException ex) {
	                delegado.mostrarMensaje("Error al eliminar el contacto en el archivo.");
	                ex.printStackTrace();
	            }
	        }
	    } else {
	        delegado.mostrarMensaje("Seleccione un contacto para eliminarlo.");
	    }
    }
	
	//logica de edicion de contacto
	private void editarContacto() {
	    int index = delegado.getIndiceContactoSeleccionado();
	    
	    if (index != -1) {
	        incializacionCampos(); 
	        
	        if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
	            persona p = contactos.get(index);
	            p.setNombre(nombres);
	            p.setTelefono(telefono);
	            p.setEmail(email);
	            p.setCategoria(categoria);
	            p.setFavorito(favorito);

	            try {
	                new personaDAO(new persona()).actualizarContactos(contactos);
	                limpiarCampos();
	                delegado.mostrarMensaje("Contacto modificado correctamente.");
	            } catch (IOException ex) {
	                delegado.mostrarMensaje("Error al modificar el contacto.");
	                ex.printStackTrace();
	            }
	        } else {
	            delegado.mostrarMensaje("Todos los campos deben estar llenos para modificar.");
	        }
	    } else {
	        delegado.mostrarMensaje("Seleccione un contacto para editar su información.");
	    }
	}
	
	// funcion para exportar archivo CSV
	private void exportarContactos() {
	    if (contactos == null || contactos.isEmpty()) {
	        delegado.mostrarMensaje("No hay contactos para exportar.");
	        return;
	    }

	    // abre una ventana que permite al usuario elegir donde guardar el archivo
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Exportar a CSV");
	    
	    int seleccion = fileChooser.showSaveDialog(delegado);
	    if (seleccion == JFileChooser.APPROVE_OPTION) {
	        File archivoDestino = fileChooser.getSelectedFile();
	        String ruta = archivoDestino.getAbsolutePath();
	        
	        // fuerza al archivo a guardarse con la extensión correcta
	        if (!ruta.toLowerCase().endsWith(".csv")) {
	            ruta += ".csv";
	        }

	        try (FileWriter fw = new FileWriter(ruta)) {
	            // Cabecera del archivo
	            fw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO\n");
	            for (persona p : contactos) {
	                fw.write(p.datosContacto() + "\n");
	            }
	            delegado.mostrarMensaje("Contactos exportados con éxito a:\n" + ruta);
	        } catch (IOException ex) {
	            delegado.mostrarMensaje("Error al intentar exportar el archivo CSV.");
	            ex.printStackTrace();
	        }
	    }
	}
}