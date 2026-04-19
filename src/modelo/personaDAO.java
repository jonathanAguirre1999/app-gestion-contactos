package modelo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Definición de la clase pública "personaDAO"
public class personaDAO {
	
	// Declaración de atributos privados de la clase "personaDAO"
	private File archivo; // Archivo donde se almacenarán los datos de los contactos
	private persona persona; // Objeto "persona" que se gestionará
	
	// Constructor público de la clase "personaDAO" que recibe un objeto "persona" como parámetro
	public personaDAO(persona persona) {
		this.persona = persona; // Asigna el objeto "persona" recibido al atributo de la clase
		archivo = new File("c:/gestionContactos"); // Establece la ruta donde se alojará el archivo
		// Llama al método para preparar el archivo
		prepararArchivo();
	}
	
	// Método privado para gestionar el archivo utilizando la clase File (MODIFICADO, GENERABA EXCEPCION FILENOTFOUND)
	private void prepararArchivo() {
		File carpeta = new File("c:/gestionContactos");
		if (!carpeta.exists()) {
			carpeta.mkdirs(); 
		}
		
		archivo = new File(carpeta, "datosContactos.csv");
		
		if (!archivo.exists()) {
			try {
				archivo.createNewFile();
				escribir("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void escribir(String texto){
		// Prepara el archivo para escribir en la última línea
		FileWriter escribir;
		try {
			escribir = new FileWriter(archivo.getAbsolutePath(), true);
			escribir.write(texto + "\n"); // Escribe los datos del contacto en el archivo
			// Cierra el archivo
			escribir.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	// Método público para escribir en el archivo
	public boolean escribirArchivo() {
//		// Prepara el archivo para escribir en la última línea
//		FileWriter escribir = new FileWriter(archivo.getAbsolutePath(), true);
//		escribir.write(persona.datosContacto() + "\n"); // Escribe los datos del contacto en el archivo
//		// Cierra el archivo
//		escribir.close();
		escribir(persona.datosContacto());
		return true; // Retorna true si la escritura fue exitosa
	}
	
	// Método público para leer los datos del archivo
	
	// METODO REEMPLAZADO POR PROVOCAR FALLOS AL MOMENTO DE REALIZAR CRUD DE CONTACTOS
	
	/*public List<persona> leerArchivo() throws IOException {
		// Cadena que contendrá toda la data del archivo
		String contactos = "";
		// Abre el archivo para leer
		FileReader leer = new FileReader(archivo.getAbsolutePath());
		int c;
		while ((c = leer.read()) != -1) { // Lee hasta la última línea del archivo
			contactos += String.valueOf((char) c);
		}
		// Separa cada contacto por salto de línea
		String[] datos = contactos.split("\n");
		// Crea una lista que almacenará cada persona encontrada
		List<persona> personas = new ArrayList<>();
		// Recorre cada contacto
		for (String contacto : datos) {
			// Crea una instancia de persona
			persona p = new persona();
			p.setNombre(contacto.split(";")[0]); // Asigna el nombre
			p.setTelefono(contacto.split(";")[1]); // Asigna el teléfono
			p.setEmail(contacto.split(";")[2]); // Asigna el email
			p.setCategoria(contacto.split(";")[3]); // Asigna la categoría
			p.setFavorito(Boolean.parseBoolean(contacto.split(";")[4])); // Asigna si es favorito
			// Añade cada persona a la lista
			personas.add(p);
		}
		// Cierra el archivo
		leer.close();
		// Retorna la lista de personas
		return personas;
	}*/
	
	// Método público para leer los datos del archivo (NUEVO METODO)
		public List<persona> leerArchivo() throws IOException {
			String contactos = "";
			if (!archivo.exists()) return new ArrayList<>(); // devuelve una lista vacia si el archivo no existe
			
			FileReader leer = new FileReader(archivo.getAbsolutePath());
			int c;
			while ((c = leer.read()) != -1) { 
				contactos += String.valueOf((char) c);
			}
			leer.close();
			
			String[] datos = contactos.split("\n");
			List<persona> personas = new ArrayList<>();
			
			for (int i = 0; i < datos.length; i++) {
				String linea = datos[i].trim();
				if (linea.isEmpty()) continue; // las lineas en blanco se omiten aca
				
				// permite omitir la primera linea
				//error del metodo anterior: creaba la primera linea como una entidad de manera erronea
				if (i == 0 && linea.startsWith("NOMBRE")) continue; 
				
				String[] partes = linea.split(";");
				if (partes.length >= 5) { 
					persona p = new persona();
					p.setNombre(partes[0]); 
					p.setTelefono(partes[1]); 
					p.setEmail(partes[2]); 
					p.setCategoria(partes[3]); 
					p.setFavorito(Boolean.parseBoolean(partes[4])); 
					personas.add(p);
				}
			}
			return personas;
		}
	
	// Método público para guardar los contactos modificados o eliminados (MODIFICADO)
	public void actualizarContactos(List<persona> personas) throws IOException {
		//borra el archivo y lo recrea insertando el encabezado una sola vez
		archivo.delete();
		prepararArchivo();
		
		// usa una sola instancia del dao para recorrer el array y actualizar la informacion en memoria 
		for (persona p : personas) {
			this.persona = p; 
			escribirArchivo(); 
		}
	}
}
