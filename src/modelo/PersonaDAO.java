package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private File archivo;
    private Persona persona;

    public PersonaDAO() {
        // Inicializamos la ruta base como pide el pdf
        File directorio = new File("c:/gestionContactos");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        archivo = new File(directorio, "datosContactos.csv");
        prepararArchivo();
    }

    public PersonaDAO(Persona persona) {
        this(); // Llama al constructor vacío para preparar el archivo
        this.persona = persona;
    }

    private void prepararArchivo() {
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error al preparar el archivo: " + e.getMessage());
        }
    }

    public boolean escribirArchivo() throws IOException {
        // Escribimos en modo "append" (true) para no borrar los anteriores
        try (FileWriter fw = new FileWriter(archivo, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            
            pw.println(persona.datosContacto());
            return true;
        }
    }

    public List<Persona> leerArchivo() {
        List<Persona> listaContactos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                // Aseguramos que la línea tenga los 5 datos
                if (datos.length == 5) {
                    Persona p = new Persona(
                        datos[0], // nombre
                        datos[1], // telefono
                        datos[2], // email
                        datos[3], // categoria
                        Boolean.parseBoolean(datos[4]) // favorito
                    );
                    listaContactos.add(p);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return listaContactos;
    }

    public void actualizarContactos(List<Persona> listaNueva) throws IOException {
        // Sobrescribimos el archivo completo con la lista actualizada
        try (FileWriter fw = new FileWriter(archivo, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            
            for (Persona p : listaNueva) {
                pw.println(p.datosContacto());
            }
        }
    }
}