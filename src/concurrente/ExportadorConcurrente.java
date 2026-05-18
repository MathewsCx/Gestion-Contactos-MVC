package concurrente;

import modelo.Persona;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Exportación CSV en segundo plano con {@code synchronized} para evitar corrupción
 * si se lanzan varias exportaciones simultáneas.
 */
public class ExportadorConcurrente {

  /** Candado compartido para escritura segura del archivo de exportación. */
  public static final Object CANDADO_ARCHIVO = new Object();

  private static final ExecutorService ejecutor = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "exportador-contactos");
    t.setDaemon(true);
    return t;
  });

  private ExportadorConcurrente() {
  }

  public static Future<Void> exportarAsync(File destino, List<Persona> contactos, Runnable alTerminar,
      java.util.function.Consumer<Exception> alError) {
    return ejecutor.submit(() -> {
      try {
        escribirCsvSincronizado(destino, contactos);
        if (alTerminar != null) {
          alTerminar.run();
        }
      } catch (Exception ex) {
        if (alError != null) {
          alError.accept(ex);
        }
      }
      return null;
    });
  }

  public static void escribirCsvSincronizado(File destino, List<Persona> contactos) throws IOException {
    synchronized (CANDADO_ARCHIVO) {
      try (BufferedWriter bw = Files.newBufferedWriter(destino.toPath(), StandardCharsets.UTF_8)) {
        bw.write('\uFEFF');
        bw.write("Nombre,Apellido,Telefono,Email,Categoria,Favorito");
        bw.newLine();
        for (Persona p : contactos) {
          bw.write(p.datosContacto());
          bw.newLine();
          Thread.sleep(8);
        }
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        throw new IOException("Exportación interrumpida", ie);
      }
    }
  }
}
