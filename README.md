# Sistema de Gestión de Contactos

Aplicación de escritorio desarrollada en Java utilizando la biblioteca **Swing**, diseñada para la gestión, almacenamiento y filtrado de contactos. Este proyecto aplica el patrón **MVC (Modelo-Vista-Controlador)** para asegurar la escalabilidad del código.

## Características Principales

* **Interfaz Optimizada:** Uso de `JTabbedPane` para separar la vista de contactos y el módulo de estadísticas.
* **Gestión de Datos:** Visualización de contactos mediante un `JTable` interactivo que soporta ordenamiento por columnas y filtrado de búsqueda en tiempo real usando `RowFilter`.
* **Eventos Avanzados:**
    * **Menú Contextual:** Implementación de un `JPopupMenu` para editar, eliminar o exportar contactos rápidamente.
    * **Atajos de Teclado (Key Bindings):** Soporte para teclas globales (`Suprimir` para eliminar, `F2` para editar, `Ctrl+Shift+S` para exportar).
* **Procesamiento Asíncrono:** Uso de `SwingWorker` para simular y gestionar la carga de datos sin bloquear el hilo principal de la interfaz gráfica (EDT), con retroalimentación visual mediante un `JProgressBar`.
* **Persistencia de Datos:** Almacenamiento local mediante lectura y escritura de archivos planos (`.csv`), y soporte para exportar copias de seguridad de la base de datos seleccionando la ruta de destino.

## Tecnologías y Herramientas

* **Lenguaje:** Java 8+
* **GUI:** Java Swing
* **IDE:** Eclipse
* **Patrón de Diseño:** MVC (Modelo-Vista-Controlador)

## Estructura del Proyecto

El proyecto está rigurosamente estructurado en tres paquetes principales:
* `modelo`: Clases base (`persona.java`) y acceso a datos (`personaDAO.java`).
* `vista`: Interfaz gráfica (`ventana.java`).
* `controlador`: Lógica de negocio principal (`logica_ventana.java`) que conecta la vista con el modelo mediante *Listeners*.

## Ejecución

Para ejecutar este proyecto localmente:
1. Clona este repositorio
2. Importa el proyecto en tu IDE, se recomienda usar Eclipse para asegurar compatibilidad.
3. Ejecuta la clase `ventana.java` ubicada en el paquete `vista`. La aplicación creará automáticamente una carpeta en `C:/gestionContactos/` para almacenar la base de datos principal.*

También puedes ejecutar el archivo `Gestor de contactos.jar` incluido en la carpeta raiz del proyecto para una prueba rápida del programa compilado.