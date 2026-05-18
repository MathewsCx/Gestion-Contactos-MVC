package concurrente;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Bloqueo de recurso por teléfono: solo un hilo puede editar el mismo contacto a la vez.
 */
public final class BloqueoEdicionContacto {

  private static final Set<String> telefonosEnEdicion =
      Collections.synchronizedSet(new HashSet<>());

  private BloqueoEdicionContacto() {
  }

  public static synchronized boolean intentarBloquear(String telefono) {
    if (telefono == null || telefono.isBlank()) {
      return true;
    }
    return telefonosEnEdicion.add(telefono.trim());
  }

  public static synchronized void liberar(String telefono) {
    if (telefono != null) {
      telefonosEnEdicion.remove(telefono.trim());
    }
  }

  public static synchronized boolean estaBloqueado(String telefono) {
    return telefono != null && telefonosEnEdicion.contains(telefono.trim());
  }
}
