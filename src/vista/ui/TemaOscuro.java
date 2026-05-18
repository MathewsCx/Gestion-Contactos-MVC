package vista.ui;

import vista.VentanaContactos;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/** Colores y {@link UIManager} compartidos para toda la aplicación. */
public final class TemaOscuro {

  private TemaOscuro() {
  }

  public static void aplicarGlobal() {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception ignored) {
    }
    Font base = new Font("Segoe UI", Font.PLAIN, 13);
    UIManager.put("defaultFont", new FontUIResource(base));

    ColorUIResource bg = new ColorUIResource(VentanaContactos.C_BG);
    ColorUIResource surface = new ColorUIResource(VentanaContactos.C_SURFACE);
    ColorUIResource input = new ColorUIResource(VentanaContactos.C_INPUT);
    ColorUIResource text = new ColorUIResource(VentanaContactos.C_TEXT);
    ColorUIResource muted = new ColorUIResource(VentanaContactos.C_MUTED);
    ColorUIResource accent = new ColorUIResource(VentanaContactos.C_ACCENT);

    UIManager.put("Panel.background", bg);
    UIManager.put("Label.foreground", text);
    UIManager.put("OptionPane.background", surface);
    UIManager.put("OptionPane.foreground", text);

    UIManager.put("ComboBox.background", input);
    UIManager.put("ComboBox.foreground", text);
    UIManager.put("ComboBox.selectionBackground", accent);
    UIManager.put("ComboBox.selectionForeground", Color.WHITE);
    UIManager.put("ComboBox.buttonBackground", input);
    UIManager.put("ComboBox.buttonHighlight", input);
    UIManager.put("ComboBox.buttonShadow", input);
    UIManager.put("ComboBox.buttonDarkShadow", input);
    UIManager.put("ComboBox.listBackground", surface);
    UIManager.put("ComboBox.listForeground", text);

    UIManager.put("CheckBox.background", surface);
    UIManager.put("CheckBox.foreground", text);
    UIManager.put("CheckBox.focus", accent);

    UIManager.put("TabbedPane.background", bg);
    UIManager.put("TabbedPane.foreground", muted);
    UIManager.put("TabbedPane.selected", surface);
    UIManager.put("TabbedPane.selectedForeground", text);
    UIManager.put("TabbedPane.contentAreaColor", bg);
    UIManager.put("TabbedPane.highlight", VentanaContactos.C_BORDER);
    UIManager.put("TabbedPane.shadow", bg);
    UIManager.put("TabbedPane.darkShadow", bg);
    UIManager.put("TabbedPane.light", VentanaContactos.C_BORDER);
    UIManager.put("TabbedPane.borderHightlightColor", VentanaContactos.C_BORDER);
    UIManager.put("TabbedPane.focus", accent);

    UIManager.put("Table.background", new ColorUIResource(VentanaContactos.C_ROW_A));
    UIManager.put("Table.foreground", text);
    UIManager.put("Table.gridColor", new ColorUIResource(VentanaContactos.C_BORDER));
    UIManager.put("TableHeader.background", surface);
    UIManager.put("TableHeader.foreground", text);
    UIManager.put("Table.selectionBackground", new ColorUIResource(0x094771));
    UIManager.put("Table.selectionForeground", text);

    UIManager.put("ScrollPane.background", bg);
    UIManager.put("ScrollBar.thumb", new ColorUIResource(VentanaContactos.C_BORDER));
    UIManager.put("ScrollBar.track", input);
  }
}
