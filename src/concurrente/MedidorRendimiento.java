package concurrente;

import modelo.Persona;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compara tiempos de búsqueda secuencial vs concurrente (para evidencia del informe U3).
 */
public final class MedidorRendimiento {

  private MedidorRendimiento() {
  }

  public static String compararBusqueda(List<Persona> contactos, String termino) {
    List<Persona> copia = new ArrayList<>(contactos);
    int n = Math.max(copia.size(), 800);
    while (copia.size() < n) {
      copia.add(new Persona("Demo", "User" + copia.size(), "099" + copia.size(),
          "demo" + copia.size() + "@ups.edu.ec", "Trabajo", false));
    }
    String t = termino.toLowerCase(Locale.ROOT);

    long t0 = System.nanoTime();
    int c1 = 0;
    for (Persona p : copia) {
      if (p.nombreCompleto().toLowerCase(Locale.ROOT).contains(t)
          || p.getTelefono().contains(t)) {
        c1++;
      }
      Thread.yield();
    }
    long msSecuencial = (System.nanoTime() - t0) / 1_000_000;

    long t1 = System.nanoTime();
    long c2 = copia.parallelStream()
        .filter(p -> p.nombreCompleto().toLowerCase(Locale.ROOT).contains(t)
            || p.getTelefono().contains(t))
        .count();
    long msConcurrente = (System.nanoTime() - t1) / 1_000_000;

    return String.format(
        "Registros: %d | Coincidencias: %d%nSecuencial: %d ms%nConcurrente (parallelStream): %d ms",
        copia.size(), c2, msSecuencial, msConcurrente);
  }
}
