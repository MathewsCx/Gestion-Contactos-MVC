package vista.ui;

import vista.VentanaContactos;

import java.awt.*;

/** Iconos vectoriales para la columna Acciones. */
public final class IconosTabla {

  private IconosTabla() {
  }

  public static void dibujarLapiz(Graphics2D g, int cx, int cy) {
    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.drawLine(cx - 7, cy + 7, cx + 6, cy - 6);
    g.drawLine(cx + 6, cy - 6, cx + 9, cy - 3);
    g.drawLine(cx - 7, cy + 7, cx - 4, cy + 10);
    g.setColor(new Color(0xCCCCCC));
    g.fillRect(cx - 8, cy + 8, 5, 3);
  }

  public static void dibujarPapelera(Graphics2D g, int cx, int cy) {
    g.setColor(VentanaContactos.C_DANGER);
    g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.drawLine(cx - 8, cy - 4, cx + 8, cy - 4);
    g.drawLine(cx - 6, cy - 4, cx - 5, cy + 8);
    g.drawLine(cx + 6, cy - 4, cx + 5, cy + 8);
    g.drawLine(cx - 5, cy + 8, cx + 5, cy + 8);
    g.drawLine(cx - 4, cy - 8, cx + 4, cy - 8);
    g.drawLine(cx - 3, cy - 1, cx - 3, cy + 6);
    g.drawLine(cx + 3, cy - 1, cx + 3, cy + 6);
  }
}
