package vista.ui;

import vista.VentanaContactos;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Columna Acciones: iconos como {@link ImageIcon} (fiable en Windows; no depende de emojis).
 */
public class AccionesCellRenderer extends JPanel implements TableCellRenderer {

  private static final Icon ICONO_EDITAR = crearIcono(true);
  private static final Icon ICONO_ELIMINAR = crearIcono(false);

  private final JLabel lblEditar = new JLabel(ICONO_EDITAR);
  private final JLabel lblEliminar = new JLabel(ICONO_ELIMINAR);

  public AccionesCellRenderer() {
    super(new FlowLayout(FlowLayout.CENTER, 14, 0));
    setOpaque(true);
    lblEditar.setToolTipText("Editar");
    lblEliminar.setToolTipText("Eliminar");
    add(lblEditar);
    add(lblEliminar);
  }

  private static Icon crearIcono(boolean editar) {
    int size = 22;
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if (editar) {
      IconosTabla.dibujarLapiz(g, size / 2, size / 2);
    } else {
      IconosTabla.dibujarPapelera(g, size / 2, size / 2);
    }
    g.dispose();
    return new ImageIcon(img);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    Color fondo = isSelected
        ? table.getSelectionBackground()
        : (row % 2 == 0 ? VentanaContactos.C_ROW_A : VentanaContactos.C_ROW_B);
    setBackground(fondo);
    lblEditar.setOpaque(false);
    lblEliminar.setOpaque(false);
    setPreferredSize(new Dimension(120, table.getRowHeight()));
    return this;
  }
}
