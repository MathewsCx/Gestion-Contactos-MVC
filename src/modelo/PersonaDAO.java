package modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private final File archivo;

    public PersonaDAO() {
        File directorio = new File("c:/gestionContactos");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        archivo = new File(directorio, "datosContactos.csv");
        prepararArchivo();
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

    public List<Persona> leerArchivo() {
        List<Persona> listaContactos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                Persona p = parsearLinea(linea);
                if (p != null) {
                    listaContactos.add(p);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return listaContactos;
    }

    private Persona parsearLinea(String linea) {
        String[] datos = linea.split(",", -1);
        try {
            if (datos.length >= 6) {
                return new Persona(
                        datos[0],
                        datos[1],
                        datos[2],
                        datos[3],
                        datos[4],
                        Boolean.parseBoolean(datos[5].trim()));
            }
            if (datos.length == 5) {
                return new Persona(datos[0], datos[1], datos[2], datos[3], Boolean.parseBoolean(datos[4].trim()));
            }
        } catch (Exception ignored) {
            // línea corrupta
        }
        return null;
    }

    public void actualizarContactos(List<Persona> listaNueva) throws IOException {
        try (FileWriter fw = new FileWriter(archivo, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            for (Persona p : listaNueva) {
                pw.println(p.datosContacto());
            }
        }
    }
}
