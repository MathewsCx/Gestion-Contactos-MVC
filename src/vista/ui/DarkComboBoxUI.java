package vista.ui;

import vista.VentanaContactos;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

/** UI del {@link JComboBox} coherente con el tema oscuro (evita fondo blanco en Windows). */
public class DarkComboBoxUI extends BasicComboBoxUI {

  @Override
  protected JButton createArrowButton() {
    JButton boton = new JButton("\u25BE");
    boton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    boton.setForeground(VentanaContactos.C_TEXT);
    boton.setBackground(VentanaContactos.C_INPUT);
    boton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, VentanaContactos.C_BORDER_LIGHT));
    boton.setFocusable(false);
    boton.setContentAreaFilled(true);
    boton.setOpaque(true);
    boton.setPreferredSize(new Dimension(28, 38));
    return boton;
  }

  @Override
  public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
    g.setColor(VentanaContactos.C_INPUT);
    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    if (comboBox.getSelectedItem() != null) {
      g.setColor(VentanaContactos.C_TEXT);
      g.setFont(comboBox.getFont());
      String texto = comboBox.getSelectedItem().toString();
      FontMetrics fm = g.getFontMetrics();
      int y = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;
      g.drawString(texto, bounds.x + 12, y);
    }
  }
}
