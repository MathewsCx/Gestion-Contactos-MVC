package concurrente;

import vista.VentanaContactos;

import javax.swing.*;

/**
 * Notificaciones en tiempo real sobre la UI usando {@link SwingUtilities#invokeLater}.
 */
public class NotificadorUi {

  private final VentanaContactos vista;

  public NotificadorUi(VentanaContactos vista) {
    this.vista = vista;
  }

  public void exito(String mensaje) {
    SwingUtilities.invokeLater(() -> vista.mostrarNotificacion(mensaje, VentanaContactos.C_ACCENT));
  }

  public void error(String mensaje) {
    SwingUtilities.invokeLater(() -> vista.mostrarNotificacion(mensaje, VentanaContactos.C_DANGER));
  }

  public void info(String mensaje) {
    SwingUtilities.invokeLater(() -> vista.mostrarNotificacion(mensaje, new java.awt.Color(0x0E639C)));
  }
}
