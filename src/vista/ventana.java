package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import controlador.logica_ventana;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class ventana extends JFrame {

	public JPanel contentPane; // Panel principal que contendrá todos los componentes de la interfaz.
	public JTextField txt_nombres; // Campo de texto para ingresar nombres.
	public JTextField txt_telefono; // Campo de texto para ingresar números de teléfono.
	public JTextField txt_email; // Campo de texto para ingresar direcciones de correo electrónico.
	public JTextField txt_buscar; // Campo de texto adicional.
	public JCheckBox chb_favorito; // Casilla de verificación para marcar un contacto como favorito.
	public JComboBox cmb_categoria; // Menú desplegable para seleccionar la categoría de contacto.
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Define el comportamiento al cerrar la ventana.
		setResizable(false); // Evita que la ventana sea redimensionable.
		setBounds(100, 100, 1026, 748); // Establece el tamaño y la posición inicial de la ventana.
		contentPane = new JPanel(); // Crea un nuevo panel de contenido.
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // Establece un borde vacío alrededor del panel.

		setContentPane(contentPane); // Establece el panel de contenido como el panel principal de la ventana.
		contentPane.setLayout(null); // Configura el diseño del panel como nulo para posicionar manualmente los componentes.
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setToolTipText("");
		tabbedPane.setBounds(12, 10, 972, 653);
		contentPane.add(tabbedPane);
		
		JPanel panelContactos = new JPanel();
		tabbedPane.addTab("CONTACTOS", null, panelContactos, null);
		tabbedPane.setEnabledAt(0, true);
		panelContactos.setLayout(null);
		
		// Creación y configuración de etiquetas para los campos de entrada.
		JLabel lbl_etiqueta1 = new JLabel("NOMBRES:");
		lbl_etiqueta1.setBounds(1, 13, 89, 13);
		panelContactos.add(lbl_etiqueta1);
		lbl_etiqueta1.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JLabel lbl_etiqueta2 = new JLabel("TELEFONO:");
		lbl_etiqueta2.setBounds(1, 52, 89, 13);
		panelContactos.add(lbl_etiqueta2);
		lbl_etiqueta2.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JLabel lbl_etiqueta3 = new JLabel("EMAIL:");
		lbl_etiqueta3.setBounds(1, 94, 89, 13);
		panelContactos.add(lbl_etiqueta3);
		lbl_etiqueta3.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		JLabel lbl_etiqueta4 = new JLabel("BUSCAR POR NOMBRE:");
		lbl_etiqueta4.setBounds(1, 633, 192, 13);
		panelContactos.add(lbl_etiqueta4);
		lbl_etiqueta4.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		// Creación y configuración de campos de texto para ingresar nombres, teléfonos y correos electrónicos.
		txt_nombres = new JTextField();
		txt_nombres.setBounds(100, 0, 427, 31);
		panelContactos.add(txt_nombres);
		txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_nombres.setColumns(10); // Establece el número de columnas para el campo de texto.
		
		txt_telefono = new JTextField();
		txt_telefono.setBounds(100, 41, 427, 31);
		panelContactos.add(txt_telefono);
		txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_telefono.setColumns(10);
		
		txt_email = new JTextField();
		txt_email.setBounds(100, 82, 427, 31);
		panelContactos.add(txt_email);
		txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_email.setColumns(10);
		
		txt_buscar = new JTextField();
		txt_buscar.setBounds(188, 622, 784, 31);
		panelContactos.add(txt_buscar);
		txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_buscar.setColumns(10);
		
		// Creación y configuración de una casilla de verificación para indicar si un contacto es favorito.
		chb_favorito = new JCheckBox("CONTACTO FAVORITO");
		chb_favorito.setBounds(0, 142, 193, 21);
		panelContactos.add(chb_favorito);
		chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
				
				cmb_categoria = new JComboBox();
				cmb_categoria.setBounds(276, 139, 251, 31);
				panelContactos.add(cmb_categoria);
				
						btn_add = new JButton("AGREGAR"); // Crea un nuevo botón con el texto "AGREGAR".
						btn_add.setBounds(577, 42, 125, 65);
						panelContactos.add(btn_add);
						btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15));
						
						btn_modificar = new JButton("MODIFICAR");
						btn_modificar.setBounds(712, 42, 125, 65);
						panelContactos.add(btn_modificar);
						btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
						
						btn_eliminar = new JButton("ELIMINAR");
						btn_eliminar.setBounds(847, 41, 125, 65);
						panelContactos.add(btn_eliminar);
						btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
						
						/*lst_contactos = new JList(); // Crea una nueva JList para mostrar la lista de contactos.
						lst_contactos.setFont(new Font("Tahoma", Font.PLAIN, 15)); // Configura la fuente de la JList.
						lst_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Establece el modo de selección a un solo elemento.
						lst_contactos.setBounds(25, 242, 971, 398); // Establece la posición y el tamaño de la JList en el panel.*/
						
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
						
						//ordenamiento para la tabla
						sorter = new TableRowSorter<>(modeloTabla);
						tbl_contactos.setRowSorter(sorter);
												
						scrLista = new JScrollPane(tbl_contactos);
						scrLista.setBounds(1, 214, 971, 350);
						panelContactos.add(scrLista);
								
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
						
						tbl_contactos.setComponentPopupMenu(popupMenu);
								
						// barra de progreso
						barraProgreso = new JProgressBar();
						barraProgreso.setBounds(1, 570, 971, 25);
						barraProgreso.setStringPainted(true); // porcentaje
						barraProgreso.setVisible(false); // oculta 
						panelContactos.add(barraProgreso);

		
		panelEstadisticas = new JPanel();
		tabbedPane.addTab("ESTADISTICAS", null, panelEstadisticas, null);

		// Arreglo que contiene las categorías disponibles.
		String[] categorias = {"Elija una Categoria", "Familia", "Amigos", "Trabajo"};
		for (String categoria : categorias) {
		    // Agrega cada categoría al JComboBox.
		    cmb_categoria.addItem(categoria);
		}
		
		//Instanciar el controlador para usar el delegado
		logica_ventana lv=new logica_ventana(this, new modelo.persona());
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
}
