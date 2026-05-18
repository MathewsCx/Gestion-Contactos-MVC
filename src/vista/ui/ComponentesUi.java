package vista.ui;

import vista.VentanaContactos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Fábrica de estilos visuales (dark mode dinámico). */
public final class ComponentesUi {

  private ComponentesUi() {
  }

  public static JPanel envolverConFranja(JComponent contenido, Color franja, int anchoFranja) {
    JPanel tarjeta = new JPanel(new BorderLayout());
    tarjeta.setBackground(VentanaContactos.C_SURFACE);
    tarjeta.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(VentanaContactos.C_BORDER, 1, true),
        new EmptyBorder(0, 0, 0, 0)));
    JPanel barra = new JPanel();
    barra.setBackground(franja);
    barra.setPreferredSize(new Dimension(anchoFranja, 0));
    JPanel cuerpo = new JPanel(new BorderLayout());
    cuerpo.setOpaque(false);
    cuerpo.setBorder(new EmptyBorder(20, 20, 20, 20));
    cuerpo.add(contenido, BorderLayout.CENTER);
    tarjeta.add(barra, BorderLayout.WEST);
    tarjeta.add(cuerpo, BorderLayout.CENTER);
    return tarjeta;
  }

  public static JPanel tarjetaKpi(JLabel titulo, JLabel valor, Color acento) {
    JPanel box = new JPanel(new BorderLayout(0, 8)) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(VentanaContactos.C_CARD);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.setColor(acento);
        g2.fillRoundRect(0, 0, getWidth() - 1, 4, 10, 10);
        g2.setColor(VentanaContactos.C_BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
      }
    };
    box.setOpaque(false);
    box.setBorder(new EmptyBorder(16, 14, 16, 14));
    titulo.setForeground(VentanaContactos.C_MUTED);
    titulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    titulo.setHorizontalAlignment(SwingConstants.CENTER);
    valor.setForeground(VentanaContactos.C_TEXT);
    valor.setFont(new Font("Segoe UI", Font.BOLD, 30));
    box.add(titulo, BorderLayout.NORTH);
    box.add(valor, BorderLayout.CENTER);
    return box;
  }

  public static void decorarBoton(JButton boton, Color base, Color hover) {
    boton.setBackground(base);
    boton.setForeground(Color.WHITE);
    boton.setFocusPainted(false);
    boton.setBorderPainted(false);
    boton.setOpaque(true);
    boton.setBorder(new EmptyBorder(12, 20, 12, 20));
    boton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        boton.setBackground(hover);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        boton.setBackground(base);
      }
    });
  }

  public static void decorarCampoTexto(JTextField campo) {
    campo.setBackground(VentanaContactos.C_INPUT);
    campo.setForeground(VentanaContactos.C_TEXT);
    campo.setCaretColor(VentanaContactos.C_ACCENT);
    campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    campo.setBorder(new LineBorder(VentanaContactos.C_BORDER, 1, true));
    campo.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        campo.setBorder(new LineBorder(VentanaContactos.C_ACCENT, 2, true));
      }

      @Override
      public void focusLost(FocusEvent e) {
        campo.setBorder(new LineBorder(VentanaContactos.C_BORDER, 1, true));
      }
    });
  }

  public static JPanel crearCabecera(JComponent izquierda, JComponent derecha) {
    JPanel barra = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint degradado = new GradientPaint(
            0, 0, new Color(0x1A1A1A),
            getWidth(), getHeight(), VentanaContactos.C_BG);
        g2.setPaint(degradado);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(0x007ACC));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2.fillRect(0, getHeight() - 2, getWidth(), 2);
        g2.dispose();
      }
    };
    barra.setOpaque(false);
    barra.setBorder(new EmptyBorder(18, 22, 14, 22));
    barra.add(izquierda, BorderLayout.WEST);
    barra.add(derecha, BorderLayout.EAST);
    return barra;
  }

  public static JComponent crearPestana(String texto, boolean activa) {
    JLabel lab = new JLabel(texto);
    lab.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    lab.setForeground(activa ? VentanaContactos.C_TEXT : VentanaContactos.C_MUTED);
    lab.setBorder(new EmptyBorder(10, 20, 10, 20));
    JPanel tab = new JPanel(new BorderLayout());
    tab.setOpaque(true);
    tab.setBackground(activa ? VentanaContactos.C_SURFACE : VentanaContactos.C_BG);
    if (activa) {
      tab.setBorder(BorderFactory.createCompoundBorder(
          new MatteBorder(0, 0, 2, 0, VentanaContactos.C_ACCENT),
          new LineBorder(VentanaContactos.C_BORDER, 1, true)));
    } else {
      tab.setBorder(new MatteBorder(0, 0, 1, 0, VentanaContactos.C_BORDER));
    }
    tab.add(lab, BorderLayout.CENTER);
    return tab;
  }

  public static void actualizarEnlacesIdioma(AbstractButton link, boolean activo) {
    link.setFont(new Font("Segoe UI", activo ? Font.BOLD : Font.PLAIN, 13));
    link.setForeground(activo ? Color.WHITE : VentanaContactos.C_MUTED);
    link.setOpaque(activo);
    link.setContentAreaFilled(activo);
    link.setBorderPainted(activo);
    link.setBackground(activo ? new Color(0x264F73) : VentanaContactos.C_BG);
    link.setBorder(activo
        ? BorderFactory.createCompoundBorder(
            new LineBorder(VentanaContactos.C_ACCENT, 1, true),
            new EmptyBorder(4, 10, 4, 10))
        : new EmptyBorder(5, 10, 5, 10));
  }
}
