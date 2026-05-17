package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.FlatLightLaf;

import controlador.logica_ventana;
import modelo.persona;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.Locale;
import java.util.ResourceBundle;

public class ventana extends JFrame {

	public JComboBox<String> cmb_idioma; 
	public ResourceBundle mensajes;
	
	public JPanel contentPane; // Panel principal que contendrá todos los componentes de la interfaz.
	public JTextField txt_nombres; // Campo de texto para ingresar nombres.
	public JTextField txt_telefono; // Campo de texto para ingresar números de teléfono.
	public JTextField txt_email; // Campo de texto para ingresar direcciones de correo electrónico.
	public JTextField txt_buscar; // Campo de texto adicional.
	public JCheckBox chb_favorito; // Casilla de verificación para marcar un contacto como favorito.
	public JComboBox<String> cmb_categoria; // Menú desplegable para seleccionar la categoría de contacto.
	public JButton btn_add; // Botón para agregar un nuevo contacto.
	public JButton btn_modificar; // Botón para modificar un contacto existente.
	public JButton btn_eliminar; // Botón para eliminar un contacto.
	//public JList lst_contactos; // Lista para mostrar los contactos. --- SE REEMPLAZA POR TABLA
	public JScrollPane scrLista; // Panel de desplazamiento para la lista de contactos.
	private JTabbedPane tabbedPane;
	private JPanel panelEstadisticas;
	private JMenuItem itemEliminar, itemEditar, itemExportar; //items del menu desplegable
	private JPopupMenu popupMenu; //menu desplegable
	public JTable tbl_contactos; 
	public DefaultTableModel modeloTabla; 
	public JProgressBar barraProgreso; 
	public TableRowSorter<DefaultTableModel> sorter;
	public JLabel lbl_titulo_principal, lbl_etiqueta1, lbl_etiqueta2, lbl_etiqueta3, lbl_etiqueta4, lblIdioma;
	public JButton btn_exportar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
			UIManager.put("Button.arc", 30);         
			UIManager.put("TextComponent.arc", 15); 
			UIManager.put("ComboBox.arc", 15);
		} catch (Exception e) {
			System.err.println("Error iniciando Flatlaf" + e.getMessage());
			e.printStackTrace();
		}
		
		
		 // Invoca el método invokeLater de la clase EventQueue para ejecutar la creación de la interfaz de usuario en un hilo de despacho de eventos (Event Dispatch Thread).
	    EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            try {
	                // Dentro de este método, se crea una instancia de la clase ventana, que es la ventana principal de la aplicación.
	                ventana frame = new ventana();
	                // Establece la visibilidad de la ventana como verdadera, lo que hace que la ventana sea visible para el usuario.
	                frame.setVisible(true);
	            } catch (Exception e) {
	                // En caso de que ocurra una excepción durante la creación o visualización de la ventana, se imprime la traza de la pila de la excepción.
	                e.printStackTrace();
	            }
	        }
	    });
	}

	
	@SuppressWarnings("serial")
	public ventana() {
		setTitle("GESTION DE CONTACTOS"); // Establece el título de la ventana.
		getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(21, 93, 182));
		getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define el comportamiento al cerrar la ventana.
		setResizable(true);
		setBounds(100, 100, 1026, 748); // Establece el tamaño y la posición inicial de la ventana.
		contentPane = new JPanel(); // Crea un nuevo panel de contenido.
		contentPane.setBorder(null); 
		setContentPane(contentPane); // Establece el panel de contenido como el panel principal de la ventana.
		contentPane.setLayout(new BorderLayout());
		
		//SELECTOR DE IDIOMAS
		JPanel panelCabecera = new JPanel(new BorderLayout());
		panelCabecera.setBackground(new Color(21, 93, 182)); 
		panelCabecera.setBorder(new EmptyBorder(5, 15, 5, 15)); 
		
		lbl_titulo_principal = new JLabel("GESTION DE CONTACTOS");
		lbl_titulo_principal.setFont(new Font("Tahoma", Font.BOLD, 18)); 
		lbl_titulo_principal.setForeground(Color.WHITE);
		panelCabecera.add(lbl_titulo_principal, BorderLayout.WEST);
		
		JPanel panelIdiomaControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		panelIdiomaControles.setOpaque(false); 
		
		lblIdioma = new JLabel("Idioma:");
		lblIdioma.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblIdioma.setForeground(Color.WHITE);
		
		cmb_idioma = new JComboBox<String>(new String[]{"Español", "Inglés", "Portugués"});
		cmb_idioma.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		cmb_idioma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String idiomaSeleccionado = (String) cmb_idioma.getSelectedItem();
				cambiarIdioma(idiomaSeleccionado);
			}
		});
		
		panelIdiomaControles.add(lblIdioma);
		panelIdiomaControles.add(cmb_idioma);
		
		panelCabecera.add(panelIdiomaControles, BorderLayout.EAST);
		
		contentPane.add(panelCabecera, BorderLayout.NORTH);

		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setToolTipText("");
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panelContactos = new JPanel();
		panelContactos.setLayout(new BorderLayout(15, 15)); 
		panelContactos.setBorder(new EmptyBorder(15, 15, 15, 15)); 
		tabbedPane.addTab("CONTACTOS", null, panelContactos, null);
		
		JPanel panelSuperior = new JPanel(new BorderLayout(20, 0));
		
		JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 15));
		
		
		
		// Creación y configuración de etiquetas para los campos de entrada.
		lbl_etiqueta1 = new JLabel("NOMBRES:");
		lbl_etiqueta1.setFont(new Font("Tahoma", Font.BOLD, 15));
		txt_nombres = new JTextField();
		txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		lbl_etiqueta2 = new JLabel("TELEFONO:");
		lbl_etiqueta2.setFont(new Font("Tahoma", Font.BOLD, 15));
		txt_telefono = new JTextField();
		txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		lbl_etiqueta3 = new JLabel("EMAIL:");
		lbl_etiqueta3.setFont(new Font("Tahoma", Font.BOLD, 15));
		txt_email = new JTextField();
		txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		chb_favorito = new JCheckBox("CONTACTO FAVORITO");
		chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		cmb_categoria = new JComboBox<String>();
		cmb_categoria.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		panelFormulario.add(lbl_etiqueta1);
		panelFormulario.add(txt_nombres);
		panelFormulario.add(lbl_etiqueta2);
		panelFormulario.add(txt_telefono);
		panelFormulario.add(lbl_etiqueta3);
		panelFormulario.add(txt_email);
		panelFormulario.add(chb_favorito);
		panelFormulario.add(cmb_categoria);
		panelFormulario.add(new JLabel("")); 
		panelFormulario.add(new JLabel(""));
		
		JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 10));
		panelBotones.setBorder(new EmptyBorder(0, 20, 0, 0));
		
		btn_add = new JButton("AGREGAR");
		btn_add.setBackground(new Color(245, 158, 11));
		btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_add.setForeground(Color.WHITE); 
		
		btn_modificar = new JButton("MODIFICAR");
		btn_modificar.setBackground(new Color(21, 93, 182));
		btn_modificar.setForeground(Color.WHITE);
		btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		btn_eliminar = new JButton("ELIMINAR");
		btn_eliminar.setBackground(new Color(225, 29, 72));
		btn_eliminar.setForeground(Color.WHITE);
		btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		btn_exportar = new JButton("EXPORTAR");
		btn_exportar.setFont(new Font("Tahoma", Font.PLAIN, 15));		
		btn_exportar.setForeground(new Color(16, 185, 129)); 
		btn_exportar.putClientProperty("JButton.buttonType", "outlined");
		btn_exportar.putClientProperty("JButton.outlineColor", new Color(16, 185, 129));
		
		//FLATLAF PERMITE INSERTAR DIRECTAMENTE PROPIEDADES CSS A LOS COMPONENTES
		//ESTA LINEA PERMITE EL REDONDEADO Y DELINEADO DEL BOTON EXPORTAR
		//BORRARLA CAUSARA QUE EL BOTON NO SE REDONDEE O PIERDA EL DELINEADO
		btn_exportar.putClientProperty("FlatLaf.style", 
				"borderColor: #10B981;" +        
				"hoverBorderColor: #059669;" +   
				"focusedBorderColor: #10B981;" +
				"borderWidth: 2"                 
			);
		
		try {
		    btn_add.setIcon(new ImageIcon(getClass().getResource("/img/add.png")));
		    btn_modificar.setIcon(new ImageIcon(getClass().getResource("/img/edit.png")));
		    btn_eliminar.setIcon(new ImageIcon(getClass().getResource("/img/delete.png")));
		    btn_eliminar.setIcon(new ImageIcon(getClass().getResource("/img/export.png")));
		    
		} catch (Exception e) {
		    System.out.println("No se encontraron los iconos de los botones");
		}
		
		
		panelBotones.add(btn_add);
		panelBotones.add(btn_modificar);
		panelBotones.add(btn_eliminar);
		panelBotones.add(btn_exportar);
		
		panelSuperior.add(panelFormulario, BorderLayout.CENTER);
		panelSuperior.add(panelBotones, BorderLayout.EAST);
		
		panelContactos.add(panelSuperior, BorderLayout.NORTH);
							
		modeloTabla = new DefaultTableModel(
				new Object[][] {},
				new String[] {"NOMBRE", "TELEFONO", "EMAIL", "CATEGORIA", "FAVORITO"}
		) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // no permite editar desde la propia celda
			}
		};
			
		tbl_contactos = new JTable(modeloTabla);
		tbl_contactos.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tbl_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbl_contactos.setRowHeight(25);
		
		sorter = new TableRowSorter<>(modeloTabla);
		tbl_contactos.setRowSorter(sorter);
		
		scrLista = new JScrollPane(tbl_contactos);
				
		popupMenu = new JPopupMenu();
		itemEliminar = new JMenuItem("Eliminar");
		itemEliminar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		popupMenu.add(itemEliminar);
		itemEditar = new JMenuItem("Editar");
		itemEditar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		popupMenu.add(itemEditar);
		itemExportar = new JMenuItem("Exportar a CSV");
		itemExportar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		popupMenu.add(itemExportar);
		
		itemEditar.setIcon(new ImageIcon(getClass().getResource("/img/edit.png")));
		itemExportar.setIcon(new ImageIcon(getClass().getResource("/img/export.png")));
		itemEliminar.setIcon(new ImageIcon(getClass().getResource("/img/delete.png")));
		
		tbl_contactos.setComponentPopupMenu(popupMenu);
				
		JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
		panelCentro.setBorder(new EmptyBorder(15, 0, 15, 0));
		panelCentro.add(scrLista, BorderLayout.CENTER);
		
		//barra de progreso
		barraProgreso = new JProgressBar();
		barraProgreso.setStringPainted(true);
		barraProgreso.setVisible(false);
		panelCentro.add(barraProgreso, BorderLayout.SOUTH);
		
		panelContactos.add(panelCentro, BorderLayout.CENTER);
						
		//busqueda
		JPanel panelSur = new JPanel(new BorderLayout(10, 0));
				
		lbl_etiqueta4 = new JLabel("BUSCAR POR NOMBRE:");
		lbl_etiqueta4.setFont(new Font("Tahoma", Font.BOLD, 15));
						
		txt_buscar = new JTextField();
		txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
						
		panelSur.add(lbl_etiqueta4, BorderLayout.WEST);
		panelSur.add(txt_buscar, BorderLayout.CENTER);
						
		panelContactos.add(panelSur, BorderLayout.SOUTH);
		
		panelEstadisticas = new JPanel();
		tabbedPane.addTab("ESTADISTICAS", null, panelEstadisticas, null);
		
		try {
		    tabbedPane.setIconAt(0, new ImageIcon(getClass().getResource("/img/contacto.png")));
		    tabbedPane.setIconAt(1, new ImageIcon(getClass().getResource("/img/estadisticas.png")));
		} catch (Exception e) {
		    System.out.println("No se encontraron los iconos de las pestañas");
		    e.printStackTrace();
		}

		// Arreglo que contiene las categorías disponibles.
		String[] categorias = {"Elija una Categoria", "Familia", "Amigos", "Trabajo"};
		for (String categoria : categorias) {
		    cmb_categoria.addItem(categoria);
		}
		
		logica_ventana lv = new logica_ventana(this, new persona());
		cambiarIdioma("Español");
	}
	
	// LISTENERS PARA EVENTOS
	public void addEliminarListener(ActionListener listener) {
	    itemEliminar.addActionListener(listener);
	}

	public void addEditarListener(ActionListener listener) {
	    itemEditar.addActionListener(listener);
	}

	public void addExportarListener(ActionListener listener) {
	    itemExportar.addActionListener(listener);
	    if(btn_exportar != null) {
	        btn_exportar.addActionListener(listener);
	    }
	}
	
	// OBTIENE EL DATO DEL CONTACTO SELECCIONADO
	public int getIndiceContactoSeleccionado() {
		int index = tbl_contactos.getSelectedRow();
	    if (index != -1) {
	        // obtiene el indice real de acuerdo a la fila seleccionada trayendo los datos reales
	        return tbl_contactos.convertRowIndexToModel(index);
	    }
	    return -1;
	}
	
	// MUESTRA MENSAJES DE ERROR
	public void mostrarMensaje(String mensaje) {
	    JOptionPane.showMessageDialog(this, mensaje);
	}
	
	//INTERNACIONALZACION - CAMBIO DE IDIOMAS
	public void cambiarIdioma(String idioma) {
		Locale localizacion;
		
		switch (idioma) {
			case "Inglés":
				localizacion = Locale.of("en");
				break;
			case "Portugués":
				localizacion = Locale.of("pt");
				break;
			default: // Español por defecto
				localizacion = Locale.of("es");
				break;
		}
		
		mensajes = ResourceBundle.getBundle("idiomas.mensajes", localizacion);
		
		this.setTitle(mensajes.getString("titulo.ventana"));
		
		tabbedPane.setTitleAt(0, mensajes.getString("tab.contactos"));
		tabbedPane.setTitleAt(1, mensajes.getString("tab.estadisticas"));
		
		if(lbl_etiqueta1 != null) lbl_etiqueta1.setText(mensajes.getString("lbl.nombres"));
		if(lbl_etiqueta2 != null) lbl_etiqueta2.setText(mensajes.getString("lbl.telefono"));
		if(lbl_etiqueta3 != null) lbl_etiqueta3.setText(mensajes.getString("lbl.email"));
		if(lbl_etiqueta4 != null) lbl_etiqueta4.setText(mensajes.getString("lbl.buscar"));
		if(lblIdioma != null) lblIdioma.setText(mensajes.getString("lbl.idioma"));
		if(lbl_titulo_principal != null) lbl_titulo_principal.setText(mensajes.getString("titulo.ventana"));

		
		if(chb_favorito != null) chb_favorito.setText(mensajes.getString("chk.favorito"));
		if(btn_add != null) btn_add.setText(mensajes.getString("btn.agregar"));
		if(btn_modificar != null) btn_modificar.setText(mensajes.getString("btn.modificar"));
		if(btn_eliminar != null) btn_eliminar.setText(mensajes.getString("btn.eliminar"));
		if(btn_exportar != null) btn_exportar.setText(mensajes.getString("btn.exportar"));
		
		if(itemEliminar != null) itemEliminar.setText(mensajes.getString("menu.eliminar"));
		if(itemEditar != null) itemEditar.setText(mensajes.getString("menu.editar"));
		if(itemExportar != null) itemExportar.setText(mensajes.getString("menu.exportar"));
		
		if(txt_nombres != null) txt_nombres.putClientProperty("JTextField.placeholderText", mensajes.getString("ph.nombres"));
		if(txt_telefono != null) txt_telefono.putClientProperty("JTextField.placeholderText", mensajes.getString("ph.telefono"));
		if(txt_email != null) txt_email.putClientProperty("JTextField.placeholderText", mensajes.getString("ph.email"));
		if(txt_buscar != null) txt_buscar.putClientProperty("JTextField.placeholderText", mensajes.getString("ph.buscar"));
		
		if(modeloTabla != null) {
			String[] columnas = {
				mensajes.getString("col.nombre"),
				mensajes.getString("col.telefono"),
				mensajes.getString("col.email"),
				mensajes.getString("col.categoria"),
				mensajes.getString("col.favorito")
			};
			modeloTabla.setColumnIdentifiers(columnas);
		}
		
		if (cmb_categoria != null && cmb_categoria.getItemCount() > 0) {
			cmb_categoria.removeItemAt(0);
			cmb_categoria.insertItemAt(mensajes.getString("combo.defecto"), 0);
			cmb_categoria.setSelectedIndex(0);
		}
	}
}
