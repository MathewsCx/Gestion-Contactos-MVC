package concurrente;

import modelo.Persona;

import java.util.List;
import java.util.Locale;

/**
 * Validación de duplicados (teléfono / correo) ejecutable desde un hilo de fondo.
 */
public final class ValidadorContactos {

  private ValidadorContactos() {
  }

  public static boolean esDuplicado(Persona candidato, List<Persona> lista, Integer indiceIgnorar) {
    String tel = normalizar(candidato.getTelefono());
    String mail = normalizar(candidato.getEmail());
    for (int i = 0; i < lista.size(); i++) {
      if (indiceIgnorar != null && indiceIgnorar == i) {
        continue;
      }
      Persona p = lista.get(i);
      if (tel.equals(normalizar(p.getTelefono())) || mail.equals(normalizar(p.getEmail()))) {
        return true;
      }
    }
    return false;
  }

  private static String normalizar(String s) {
    return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
  }
}
