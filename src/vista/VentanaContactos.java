package vista;

import util.I18nManager;
import vista.ui.ComponentesUi;
import vista.ui.DarkComboBoxUI;
import vista.ui.AccionesCellRenderer;
import vista.ui.TemaOscuro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Vista MVC — diseño compacto dark mode con pestañas, i18n ES/EN/PT,
 * formulario lateral, tabla con acciones, búsqueda y exportación.
 */
public class VentanaContactos extends JFrame {

  public static final Color C_BG = new Color(0x1E1E1E);
  public static final Color C_SURFACE = new Color(0x2D2D30);
  public static final Color C_CARD = new Color(0x333333);
  public static final Color C_INPUT = new Color(0x3F3F46);
  public static final Color C_BORDER = new Color(0x454545);
  public static final Color C_BORDER_LIGHT = new Color(0x9CA3AF);
  public static final Color C_TEXT = new Color(0xF1F1F1);
  public static final Color C_MUTED = new Color(0x9CA3AF);
  public static final Color C_ACCENT = new Color(0x007ACC);
  public static final Color C_ACCENT_HOVER = new Color(0x1C8FD6);
  public static final Color C_EXPORT = new Color(0x0D9488);
  public static final Color C_EXPORT_HOVER = new Color(0x14B8A6);
  public static final Color C_KPI_FAV = new Color(0xF59E0B);
  public static final Color C_KPI_FAM = new Color(0x8B5CF6);
  public static final Color C_KPI_AMI = new Color(0x10B981);
  public static final Color C_KPI_TRA = new Color(0xEC4899);
  public static final Color C_DANGER = new Color(0xD32F2F);
  public static final Color C_ROW_A = new Color(0x252526);
  public static final Color C_ROW_B = new Color(0x2D2D30);

  public static final int COL_EMAIL = 3;
  public static final int COL_ACCIONES = 4;

  private JLabel lblTituloVentana;
  private JLabel lblSubtitulo;
  private JLabel lblTituloFormulario;
  private JLabel lblNombre;
  private JLabel lblApellido;
  private JLabel lblTelefono;
  private JLabel lblCorreo;
  private JLabel lblCategoria;
  private JLabel lblHintTabla;
  private JLabel lblBuscar;

  public JTextField txtNombre;
  public JTextField txtApellido;
  public JTextField txtTelefono;
  public JTextField txtCorreo;
  public JComboBox<String> cmbCategoria;
  public JCheckBox chbFavoritos;
  public JButton btnGuardar;
  public JTextField txtBuscar;
  public JButton btnImportar;
  public JButton btnExportar;
  public JButton btnBenchmark;

  public JProgressBar barraEstado;
  private JPanel panelNotificacion;
  private JLabel lblNotificacion;

  public JTabbedPane panelPestanas;
  public JTable tablaContactos;
  public DefaultTableModel modeloTabla;
  private final AccionesCellRenderer rendererAcciones = new AccionesCellRenderer();

  private JLabel lblStatTotal;
  private JLabel lblStatFavoritos;
  private JLabel lblStatFamilia;
  private JLabel lblStatAmigos;
  private JLabel lblStatTrabajo;

  private final JButton btnLangEs = new JButton("ES");
  private final JButton btnLangEn = new JButton("EN");
  private final JButton btnLangPt = new JButton("PT");
  private String idiomaActivo = I18nManager.LANG_ES;

  private Consumer<String> onCambioIdioma;

  public VentanaContactos() {
    TemaOscuro.aplicarGlobal();
    configurarVentana();
    construirInterfaz();
    aplicarTextos();
  }

  public static void prepararTemaGlobal() {
    TemaOscuro.aplicarGlobal();
  }

  private void configurarVentana() {
    setMinimumSize(new Dimension(1020, 640));
    setSize(1140, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    getContentPane().setBackground(C_BG);
    setLayout(new BorderLayout(0, 0));
  }

  private void construirInterfaz() {
    add(crearCabecera(), BorderLayout.NORTH);
    panelPestanas = new JTabbedPane();
    panelPestanas.setBackground(C_BG);
    panelPestanas.setForeground(C_MUTED);
    panelPestanas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    panelPestanas.setBorder(new EmptyBorder(8, 16, 0, 16));
    panelPestanas.addTab("x", crearPestanaGestion());
    panelPestanas.addTab("y", crearPestanaEstadisticas());
    panelPestanas.addChangeListener(e -> refrescarEstiloPestanas());
    add(panelPestanas, BorderLayout.CENTER);
    add(crearPieGlobal(), BorderLayout.SOUTH);
    refrescarEstiloPestanas();
  }

  private void refrescarEstiloPestanas() {
    for (int i = 0; i < panelPestanas.getTabCount(); i++) {
      panelPestanas.setTabComponentAt(
          i,
          ComponentesUi.crearPestana(panelPestanas.getTitleAt(i), i == panelPestanas.getSelectedIndex()));
    }
  }

  private JPanel crearPieGlobal() {
    JPanel pie = new JPanel(new BorderLayout());
    pie.setBackground(C_BG);

    panelNotificacion = new JPanel(new BorderLayout());
    panelNotificacion.setBackground(new Color(0x0E639C));
    panelNotificacion.setVisible(false);
    panelNotificacion.setBorder(new EmptyBorder(12, 20, 12, 20));
    lblNotificacion = new JLabel();
    lblNotificacion.setForeground(Color.WHITE);
    lblNotificacion.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    panelNotificacion.add(lblNotificacion, BorderLayout.CENTER);

    barraEstado = new JProgressBar(0, 100);
    barraEstado.setStringPainted(true);
    barraEstado.setVisible(false);
    barraEstado.setBackground(new Color(0x2A2A2A));
    barraEstado.setForeground(C_ACCENT);
    barraEstado.setBorder(BorderFactory.createCompoundBorder(
        new EmptyBorder(4, 16, 12, 16),
        new LineBorder(C_BORDER, 1, true)));

    pie.add(panelNotificacion, BorderLayout.NORTH);
    pie.add(barraEstado, BorderLayout.SOUTH);
    return pie;
  }

  public void mostrarNotificacion(String texto, Color colorFondo) {
    lblNotificacion.setText(texto);
    panelNotificacion.setBackground(colorFondo);
    panelNotificacion.setVisible(true);
    revalidate();
    Timer ocultar = new Timer(4000, e -> panelNotificacion.setVisible(false));
    ocultar.setRepeats(false);
    ocultar.start();
  }

  public void setBarraProgresoVisible(boolean visible, String mensaje) {
    barraEstado.setVisible(visible);
    if (mensaje != null) {
      barraEstado.setString(mensaje);
    }
    if (!visible) {
      barraEstado.setValue(0);
      barraEstado.setIndeterminate(false);
    }
    barraEstado.repaint();
    revalidate();
  }

  public void setProgreso(int valor, String mensaje) {
    barraEstado.setIndeterminate(false);
    barraEstado.setValue(valor);
    if (mensaje != null) {
      barraEstado.setString(mensaje);
    }
  }

  public void setProgresoIndeterminado(String mensaje) {
    barraEstado.setIndeterminate(true);
    if (mensaje != null) {
      barraEstado.setString(mensaje);
    }
  }

  private JPanel crearCabecera() {
    lblTituloVentana = new JLabel();
    lblTituloVentana.setForeground(C_TEXT);
    lblTituloVentana.setFont(new Font("Segoe UI", Font.BOLD, 24));

    lblSubtitulo = new JLabel();
    lblSubtitulo.setForeground(C_MUTED);
    lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

    JPanel titulos = new JPanel();
    titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
    titulos.setOpaque(false);
    titulos.add(lblTituloVentana);
    titulos.add(Box.createVerticalStrut(6));
    titulos.add(lblSubtitulo);

    return ComponentesUi.crearCabecera(titulos, crearSelectorIdioma());
  }

  private JPanel crearSelectorIdioma() {
    JPanel selector = new JPanel();
    selector.setLayout(new BoxLayout(selector, BoxLayout.X_AXIS));
    selector.setOpaque(false);
    configurarBotonIdioma(btnLangEs, I18nManager.LANG_ES);
    configurarBotonIdioma(btnLangEn, I18nManager.LANG_EN);
    configurarBotonIdioma(btnLangPt, I18nManager.LANG_PT);
    for (JButton b : new JButton[] { btnLangEs, btnLangEn, btnLangPt }) {
      Dimension tam = new Dimension(58, 32);
      b.setMinimumSize(tam);
      b.setPreferredSize(tam);
      b.setMaximumSize(tam);
    }
    selector.add(btnLangEs);
    selector.add(Box.createHorizontalStrut(6));
    selector.add(btnLangEn);
    selector.add(Box.createHorizontalStrut(6));
    selector.add(btnLangPt);
    selector.setPreferredSize(new Dimension(192, 36));
    selector.setMinimumSize(new Dimension(192, 36));
    return selector;
  }

  private JPanel crearPestanaGestion() {
    JPanel root = new JPanel(new BorderLayout(16, 0));
    root.setBackground(C_BG);
    root.setBorder(new EmptyBorder(8, 8, 12, 8));

    JPanel columnaForm = new JPanel();
    columnaForm.setLayout(new BoxLayout(columnaForm, BoxLayout.Y_AXIS));
    columnaForm.setOpaque(false);
    columnaForm.setPreferredSize(new Dimension(300, 0));
    columnaForm.setMinimumSize(new Dimension(280, 0));
    columnaForm.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));
    columnaForm.add(crearPanelRegistro());
    columnaForm.add(Box.createVerticalGlue());

    root.add(columnaForm, BorderLayout.WEST);
    root.add(crearPanelTablaYBusqueda(), BorderLayout.CENTER);
    return root;
  }

  private JPanel crearPanelRegistro() {
    JPanel interior = new JPanel(new BorderLayout(0, 12));
    interior.setOpaque(false);
    lblTituloFormulario = new JLabel();
    lblTituloFormulario.setForeground(C_ACCENT);
    lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblTituloFormulario.setBorder(new EmptyBorder(0, 0, 4, 0));

    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints g = new GridBagConstraints();
    g.gridx = 0;
    g.weightx = 1;
    g.fill = GridBagConstraints.HORIZONTAL;

    txtNombre = crearCampo();
    txtApellido = crearCampo();
    txtTelefono = crearCampo();
    txtCorreo = crearCampo();
    cmbCategoria = crearComboCategoria();

    lblNombre = etiquetaCampo("");
    lblApellido = etiquetaCampo("");
    lblTelefono = etiquetaCampo("");
    lblCorreo = etiquetaCampo("");
    lblCategoria = etiquetaCampo("");

    int row = 0;
    agregarFila(form, g, row++, lblNombre, txtNombre);
    agregarFila(form, g, row++, lblApellido, txtApellido);
    agregarFila(form, g, row++, lblTelefono, txtTelefono);
    agregarFila(form, g, row++, lblCorreo, txtCorreo);
    agregarFila(form, g, row++, lblCategoria, cmbCategoria);

    chbFavoritos = new JCheckBox();
    chbFavoritos.setOpaque(false);
    chbFavoritos.setForeground(C_TEXT);
    chbFavoritos.setBackground(C_SURFACE);
    chbFavoritos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    chbFavoritos.setFocusPainted(false);
    chbFavoritos.setIcon(crearIconoCheck(false));
    chbFavoritos.setSelectedIcon(crearIconoCheck(true));
    g.gridy = row * 2;
    g.gridwidth = 1;
    g.insets = new Insets(12, 0, 0, 0);
    form.add(chbFavoritos, g);
    row++;

    btnGuardar = crearBotonPrimario();
    g.gridy = row * 2 + 1;
    g.insets = new Insets(16, 0, 0, 0);
    form.add(btnGuardar, g);

    interior.add(lblTituloFormulario, BorderLayout.NORTH);
    interior.add(form, BorderLayout.CENTER);
    return ComponentesUi.envolverConFranja(interior, C_ACCENT, 4);
  }

  private Icon crearIconoCheck(boolean seleccionado) {
    return new Icon() {
      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(seleccionado ? C_ACCENT : C_INPUT);
        g2.fillRoundRect(x, y + 2, 16, 16, 3, 3);
        g2.setColor(seleccionado ? C_ACCENT : C_BORDER_LIGHT);
        g2.drawRoundRect(x, y + 2, 16, 16, 3, 3);
        if (seleccionado) {
          g2.setColor(Color.WHITE);
          g2.drawLine(x + 4, y + 10, x + 7, y + 13);
          g2.drawLine(x + 7, y + 13, x + 13, y + 6);
        }
        g2.dispose();
      }

      @Override
      public int getIconWidth() {
        return 18;
      }

      @Override
      public int getIconHeight() {
        return 18;
      }
    };
  }

  @SuppressWarnings("unchecked")
  private JComboBox<String> crearComboCategoria() {
    JComboBox<String> combo = new JComboBox<>();
    combo.setUI(new DarkComboBoxUI());
    combo.setBackground(C_INPUT);
    combo.setForeground(C_TEXT);
    combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    combo.setBorder(new LineBorder(C_BORDER_LIGHT, 1, true));
    combo.setPreferredSize(new Dimension(240, 38));
    combo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setOpaque(true);
        if (isSelected) {
          setBackground(C_ACCENT);
          setForeground(Color.WHITE);
        } else {
          setBackground(index < 0 ? C_INPUT : C_SURFACE);
          setForeground(C_TEXT);
        }
        setBorder(new EmptyBorder(6, 12, 6, 12));
        return this;
      }
    });
    return combo;
  }

  private JPanel crearPanelTablaYBusqueda() {
    JPanel panel = new JPanel(new BorderLayout(0, 12));
    panel.setOpaque(false);

    lblHintTabla = new JLabel();
    lblHintTabla.setForeground(C_MUTED);
    lblHintTabla.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    lblHintTabla.setBorder(BorderFactory.createCompoundBorder(
        new EmptyBorder(0, 4, 8, 4),
        new MatteBorder(0, 0, 0, 3, C_ACCENT)));

    String[] cols = {"", "", "", "", ""};
    modeloTabla = new DefaultTableModel(cols, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    tablaContactos = new JTable(modeloTabla);
    tablaContactos.setRowHeight(44);
    tablaContactos.setFillsViewportHeight(true);
    tablaContactos.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    tablaContactos.setShowGrid(false);
    tablaContactos.setIntercellSpacing(new Dimension(0, 0));
    tablaContactos.setBackground(C_ROW_A);
    tablaContactos.setForeground(C_TEXT);
    tablaContactos.setSelectionBackground(new Color(0x0D4A7A));
    tablaContactos.setSelectionForeground(C_TEXT);
    tablaContactos.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    JTableHeader header = tablaContactos.getTableHeader();
    header.setReorderingAllowed(true);
    header.setBackground(C_SURFACE);
    header.setForeground(C_MUTED);
    header.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

    configurarRenderersTabla();

    JScrollPane scroll = new JScrollPane(tablaContactos);
    scroll.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(C_BORDER, 1, true),
        new EmptyBorder(2, 2, 2, 2)));
    scroll.getViewport().setBackground(C_ROW_A);
    scroll.getVerticalScrollBar().setUnitIncrement(16);

    JPanel centro = new JPanel(new BorderLayout(0, 8));
    centro.setOpaque(false);
    centro.add(lblHintTabla, BorderLayout.NORTH);
    centro.add(scroll, BorderLayout.CENTER);

    panel.add(centro, BorderLayout.CENTER);
    panel.setPreferredSize(new Dimension(400, 300));
    panel.add(crearBarraBusqueda(), BorderLayout.SOUTH);
    return panel;
  }

  private JPanel crearBarraBusqueda() {
    JPanel barra = new JPanel(new BorderLayout(12, 0));
    barra.setOpaque(false);
    barra.setBorder(new EmptyBorder(4, 0, 0, 0));

    JPanel izq = new JPanel(new BorderLayout(4, 0));
    izq.setOpaque(false);
    lblBuscar = etiquetaCampo("");
    txtBuscar = crearCampo();
    izq.add(lblBuscar, BorderLayout.NORTH);
    izq.add(txtBuscar, BorderLayout.CENTER);

    btnImportar = crearBotonSecundario();
    btnExportar = crearBotonExportar();

    JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    acciones.setOpaque(false);
    acciones.add(btnImportar);
    acciones.add(btnExportar);

    barra.add(izq, BorderLayout.CENTER);
    barra.add(acciones, BorderLayout.EAST);
    return barra;
  }

  private JPanel crearPestanaEstadisticas() {
    JPanel root = new JPanel(new BorderLayout());
    root.setBackground(C_BG);
    root.setBorder(new EmptyBorder(16, 20, 20, 20));

    JLabel intro = new JLabel(" ");
    intro.setName("stats.intro");
    intro.setForeground(C_MUTED);
    intro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

    JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
    grid.setOpaque(false);
    grid.setPreferredSize(new Dimension(600, 280));
    lblStatTotal = crearValorKpi("0");
    lblStatFavoritos = crearValorKpi("0");
    lblStatFamilia = crearValorKpi("0");
    lblStatAmigos = crearValorKpi("0");
    lblStatTrabajo = crearValorKpi("0");
    grid.add(envolverKpi("stat.total", lblStatTotal, C_ACCENT));
    grid.add(envolverKpi("stat.favorites", lblStatFavoritos, C_KPI_FAV));
    grid.add(envolverKpi("stat.family", lblStatFamilia, C_KPI_FAM));
    grid.add(envolverKpi("stat.friends", lblStatAmigos, C_KPI_AMI));
    grid.add(envolverKpi("stat.work", lblStatTrabajo, C_KPI_TRA));
    grid.add(new JPanel());

    btnBenchmark = new JButton();
    btnBenchmark.setBackground(C_CARD);
    btnBenchmark.setForeground(C_TEXT);
    btnBenchmark.setFocusPainted(false);
    btnBenchmark.setBorder(new LineBorder(C_BORDER, 1, true));
    btnBenchmark.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    btnBenchmark.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        btnBenchmark.setBackground(C_SURFACE);
        btnBenchmark.setBorder(new LineBorder(C_ACCENT, 1, true));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        btnBenchmark.setBackground(C_CARD);
        btnBenchmark.setBorder(new LineBorder(C_BORDER, 1, true));
      }
    });
    JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
    sur.setOpaque(false);
    sur.add(btnBenchmark);

    root.add(intro, BorderLayout.NORTH);
    root.add(grid, BorderLayout.CENTER);
    root.add(sur, BorderLayout.SOUTH);
    return root;
  }

  private JLabel crearValorKpi(String v) {
    JLabel l = new JLabel(v, SwingConstants.CENTER);
    l.setForeground(C_TEXT);
    l.setFont(new Font("Segoe UI", Font.BOLD, 28));
    return l;
  }

  private JPanel envolverKpi(String titleKey, JLabel valor, Color acento) {
    JLabel t = new JLabel();
    t.setName(titleKey);
    return ComponentesUi.tarjetaKpi(t, valor, acento);
  }

  private void agregarFila(JPanel form, GridBagConstraints g, int row, JLabel lbl, JComponent campo) {
    g.gridy = row * 2;
    g.insets = new Insets(row == 0 ? 14 : 10, 0, 4, 0);
    form.add(lbl, g);
    g.gridy = row * 2 + 1;
    g.insets = new Insets(0, 0, 0, 0);
    form.add(campo, g);
  }

  private JLabel etiquetaCampo(String texto) {
    JLabel l = new JLabel(texto);
    l.setForeground(C_MUTED);
    l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    return l;
  }

  private JTextField crearCampo() {
    JTextField f = new JTextField();
    ComponentesUi.decorarCampoTexto(f);
    f.setPreferredSize(new Dimension(200, 40));
    return f;
  }

  private JButton crearBotonPrimario() {
    JButton b = new JButton();
    b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    ComponentesUi.decorarBoton(b, C_ACCENT, C_ACCENT_HOVER);
    b.setPreferredSize(new Dimension(240, 44));
    return b;
  }

  private JButton crearBotonExportar() {
    JButton b = new JButton();
    b.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
    ComponentesUi.decorarBoton(b, C_EXPORT, C_EXPORT_HOVER);
    b.setPreferredSize(new Dimension(150, 40));
    return b;
  }

  private JButton crearBotonSecundario() {
    JButton b = new JButton();
    b.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
    ComponentesUi.decorarBoton(b, new Color(0x4B5563), new Color(0x6B7280));
    b.setPreferredSize(new Dimension(150, 40));
    return b;
  }

  private void configurarBotonIdioma(JButton boton, String codigo) {
    boton.putClientProperty("lang", codigo);
    boton.setFocusPainted(false);
    boton.setBorderPainted(false);
    boton.setContentAreaFilled(false);
    boton.setOpaque(false);
    boton.setMargin(new Insets(2, 4, 2, 4));
    boton.setHorizontalAlignment(SwingConstants.CENTER);
    boton.addActionListener(e -> {
      if (onCambioIdioma != null) {
        onCambioIdioma.accept(codigo);
      }
    });
  }

  /** Reaplica renderers (necesario tras setColumnIdentifiers o recargar filas). */
  public void configurarRenderersTabla() {
    if (tablaContactos == null || tablaContactos.getColumnCount() <= COL_ACCIONES) {
      return;
    }
    DefaultTableCellRenderer filas = new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
          boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
          c.setBackground(row % 2 == 0 ? C_ROW_A : C_ROW_B);
          c.setForeground(C_TEXT);
        }
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));
        setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
        return c;
      }
    };
    for (int i = 0; i < COL_ACCIONES; i++) {
      tablaContactos.getColumnModel().getColumn(i).setCellRenderer(filas);
    }
    TableColumn colAcc = tablaContactos.getColumnModel().getColumn(COL_ACCIONES);
    colAcc.setCellRenderer(rendererAcciones);
    colAcc.setMinWidth(115);
    colAcc.setPreferredWidth(120);
    colAcc.setMaxWidth(130);
    tablaContactos.getColumnModel().getColumn(0).setMinWidth(45);
    tablaContactos.getColumnModel().getColumn(0).setMaxWidth(55);
    tablaContactos.getColumnModel().getColumn(1).setPreferredWidth(180);
    tablaContactos.getColumnModel().getColumn(2).setPreferredWidth(120);
    tablaContactos.getColumnModel().getColumn(2).setMaxWidth(140);
    tablaContactos.getColumnModel().getColumn(COL_EMAIL).setPreferredWidth(220);
  }

  public void aplicarTextos() {
    String titulo = I18nManager.get("app.title");
    setTitle(titulo);
    lblTituloVentana.setText(titulo);
    lblSubtitulo.setText(I18nManager.get("app.subtitle"));
    lblTituloFormulario.setText(I18nManager.get("form.title"));
    lblNombre.setText(I18nManager.get("field.name").toUpperCase(I18nManager.getLocale()));
    lblApellido.setText(I18nManager.get("field.lastname").toUpperCase(I18nManager.getLocale()));
    lblTelefono.setText(I18nManager.get("field.phone").toUpperCase(I18nManager.getLocale()));
    lblCorreo.setText(I18nManager.get("field.email").toUpperCase(I18nManager.getLocale()));
    lblCategoria.setText(I18nManager.get("field.category").toUpperCase(I18nManager.getLocale()));
    chbFavoritos.setText(I18nManager.get("field.favorite"));
    lblHintTabla.setText(I18nManager.get("table.hint.sort"));
    lblBuscar.setText(I18nManager.get("search.label").toUpperCase(I18nManager.getLocale()));
    btnGuardar.setText(I18nManager.get("btn.save"));
    btnImportar.setText(I18nManager.get("btn.import"));
    btnExportar.setText(I18nManager.get("btn.export"));
    if (btnBenchmark != null) {
      btnBenchmark.setText(I18nManager.get("btn.benchmark"));
    }

    if (modeloTabla != null) {
      modeloTabla.setColumnIdentifiers(new String[]{
          I18nManager.get("table.col.id"),
          I18nManager.get("table.col.name"),
          I18nManager.get("table.col.phone"),
          I18nManager.get("table.col.email"),
          I18nManager.get("table.col.actions")
      });
    }

    if (panelPestanas != null && panelPestanas.getTabCount() >= 2) {
      panelPestanas.setTitleAt(0, I18nManager.get("tab.management"));
      panelPestanas.setTitleAt(1, I18nManager.get("tab.statistics"));
    }

    actualizarTextosEstadisticas();
    recargarCategoriasCombo();
    actualizarEstiloIdiomas();
    refrescarEstiloPestanas();
    configurarRenderersTabla();
  }

  private void actualizarTextosEstadisticas() {
    if (panelPestanas == null || panelPestanas.getTabCount() < 2) {
      return;
    }
    Component tab = panelPestanas.getComponentAt(1);
    if (!(tab instanceof JPanel root)) {
      return;
    }
    for (Component c : root.getComponents()) {
      if (c instanceof JLabel intro && "stats.intro".equals(intro.getName())) {
        intro.setText(I18nManager.get("stats.intro"));
      } else if (c instanceof JPanel grid) {
        for (Component kpi : grid.getComponents()) {
          if (kpi instanceof JPanel tarjeta) {
            for (Component inner : tarjeta.getComponents()) {
              if (inner instanceof JLabel lbl && lbl.getName() != null && lbl.getName().startsWith("stat.")) {
                lbl.setText(I18nManager.get(lbl.getName()));
              }
            }
          }
        }
      }
    }
  }

  private void recargarCategoriasCombo() {
    Object sel = cmbCategoria.getSelectedItem();
    cmbCategoria.removeAllItems();
    cmbCategoria.addItem(I18nManager.get("category.placeholder"));
    cmbCategoria.addItem(I18nManager.get("category.family"));
    cmbCategoria.addItem(I18nManager.get("category.friends"));
    cmbCategoria.addItem(I18nManager.get("category.work"));
    if (sel != null) {
      for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
        if (sel.toString().equals(cmbCategoria.getItemAt(i))) {
          cmbCategoria.setSelectedIndex(i);
          return;
        }
      }
    }
    cmbCategoria.setSelectedIndex(0);
  }

  public void setIdiomaActivo(String codigo) {
    idiomaActivo = codigo;
    actualizarEstiloIdiomas();
  }

  private void actualizarEstiloIdiomas() {
    pintarBotonIdioma(btnLangEs, I18nManager.LANG_ES);
    pintarBotonIdioma(btnLangEn, I18nManager.LANG_EN);
    pintarBotonIdioma(btnLangPt, I18nManager.LANG_PT);
  }

  private void pintarBotonIdioma(JButton boton, String codigo) {
    ComponentesUi.actualizarEnlacesIdioma(boton, codigo.equals(idiomaActivo));
  }

  public void setOnCambioIdioma(Consumer<String> listener) {
    onCambioIdioma = listener;
  }

  public void actualizarKpis(int total, long fav, long fam, long am, long tr) {
    lblStatTotal.setText(String.valueOf(total));
    lblStatFavoritos.setText(String.valueOf(fav));
    lblStatFamilia.setText(String.valueOf(fam));
    lblStatAmigos.setText(String.valueOf(am));
    lblStatTrabajo.setText(String.valueOf(tr));
  }

  public JTable getTablaContactos() {
    return tablaContactos;
  }

  public DefaultTableModel getModeloTabla() {
    return modeloTabla;
  }
}
