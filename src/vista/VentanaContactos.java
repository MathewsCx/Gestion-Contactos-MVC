package vista;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Vista (capa MVC) de la aplicación de gestión de contactos.
 * <p>
 * Contiene únicamente composición visual, estilos y componentes públicos/accesores
 * para que el controlador {@code controlador.logicaContactos} registre listeners
 * sin acoplar lógica de negocio en la interfaz.
 * </p>
 */
public class VentanaContactos extends JFrame {

    /* Paleta "Elite 2026" dark — alineada a rúbrica (#1E1E1E, #2D2D2D, acentos discretos) */
    public static final Color C_BG = new Color(0x1E1E1E);
    public static final Color C_SURFACE = new Color(0x2D2D2D);
    public static final Color C_CARD = new Color(0x333333);
    public static final Color C_BORDER = new Color(0x454545);
    public static final Color C_TEXT = new Color(0xF0F0F0);
    public static final Color C_MUTED = new Color(0xB0B0B0);
    public static final Color C_ACCENT = new Color(0x3B82F6);
    public static final Color C_ACCENT_DIM = new Color(0x2563EB);
    public static final Color C_CYAN = new Color(0x06B6D4);
    public static final Color C_DANGER = new Color(0xDC2626);
    public static final Color C_ROW_A = new Color(0x2A2A2A);
    public static final Color C_ROW_B = new Color(0x323232);

    public JTabbedPane panelPestanas;
    public JTable tablaContactos;
    public DefaultTableModel modeloTabla;
    public JProgressBar barraProgreso;

    public JTextField txt_nombre, txt_telefono, txt_correo, txt_buscar;
    public JButton btn_add, btn_editar, btn_eliminar, btn_exportar;
    public JCheckBox chb_favoritos;
    public JComboBox<String> cmb_categoria;

    /** Menú contextual sobre la tabla (opciones Editar / Eliminar). */
    private final JPopupMenu menuContextualTabla = new JPopupMenu();
    private final JMenuItem mitContextoEditar = new JMenuItem("Editar");
    private final JMenuItem mitContextoEliminar = new JMenuItem("Eliminar");

    /** KPIs de la pestaña Estadísticas (rellenados por el controlador). */
    private JLabel lblStatTotal;
    private JLabel lblStatFavoritos;
    private JLabel lblStatFamilia;
    private JLabel lblStatAmigos;
    private JLabel lblStatTrabajo;

    public VentanaContactos() {
        prepararTemaGlobal();
        configurarVentanaPrincipal();
        inicializarComponentes();
        aplicarEstilosFinales();
    }

    /**
     * Ajustes globales de LAF y colores Swing antes de instanciar componentes.
     */
    public static void prepararTemaGlobal() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ignored) {
            // Se conserva el LAF por defecto.
        }
        Font base = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("defaultFont", new FontUIResource(base));
        UIManager.put("Panel.background", new ColorUIResource(C_BG));
        UIManager.put("TabbedPane.background", new ColorUIResource(C_SURFACE));
        UIManager.put("TabbedPane.foreground", new ColorUIResource(C_TEXT));
        UIManager.put("TabbedPane.selected", new ColorUIResource(C_CARD));
        UIManager.put("TabbedPane.contentAreaColor", new ColorUIResource(C_SURFACE));
        UIManager.put("TabbedPane.highlight", new ColorUIResource(C_BORDER));
        UIManager.put("TabbedPane.shadow", new ColorUIResource(C_BG));
        UIManager.put("TabbedPane.darkShadow", new ColorUIResource(C_BG));
        UIManager.put("TabbedPane.light", new ColorUIResource(C_BORDER));
    }

    private void configurarVentanaPrincipal() {
        setTitle("Gestión de Contactos — Elite 2026");
        setMinimumSize(new Dimension(980, 660));
        setSize(1060, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(0, 0));
    }

    private void inicializarComponentes() {
        JPanel env = new JPanel(new BorderLayout(0, 0));
        env.setOpaque(false);
        env.setBorder(new EmptyBorder(10, 16, 10, 16));

        env.add(crearCabecera(), BorderLayout.NORTH);

        panelPestanas = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        panelPestanas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelPestanas.setBackground(C_SURFACE);
        panelPestanas.setForeground(C_TEXT);

        JPanel pestGestion = construirPanelGestion();
        pestGestion.setOpaque(true);
        pestGestion.setBackground(C_SURFACE);

        JPanel pestStats = construirPanelEstadisticas();
        pestStats.setOpaque(true);
        pestStats.setBackground(C_SURFACE);

        panelPestanas.addTab("Gestión de Contactos", pestGestion);
        panelPestanas.addTab("Estadísticas", pestStats);
        personalizarPestanasDark();

        env.add(panelPestanas, BorderLayout.CENTER);
        add(env, BorderLayout.CENTER);

        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        barraProgreso.setValue(0);
        barraProgreso.setString("Iniciando…");
        barraProgreso.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        barraProgreso.setForeground(C_ACCENT);
        barraProgreso.setBackground(C_SURFACE);
        barraProgreso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
                new EmptyBorder(8, 16, 10, 16)));
        add(barraProgreso, BorderLayout.SOUTH);

        menuContextualTabla.setBackground(C_CARD);
        mitContextoEditar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mitContextoEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        menuContextualTabla.add(mitContextoEditar);
        menuContextualTabla.add(mitContextoEliminar);
    }

    /**
     * Cabeceras de pestaña con fondo oscuro para coherencia con el tema (evita “tabs blancos”).
     */
    private void personalizarPestanasDark() {
        for (int i = 0; i < panelPestanas.getTabCount(); i++) {
            String texto = panelPestanas.getTitleAt(i);
            JLabel lab = new JLabel(texto);
            lab.setForeground(C_TEXT);
            lab.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
            lab.setBorder(new EmptyBorder(10, 18, 10, 18));
            JPanel tabComp = new JPanel(new GridBagLayout());
            tabComp.setOpaque(true);
            tabComp.setBackground(C_SURFACE);
            tabComp.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDER));
            tabComp.add(lab);
            panelPestanas.setTabComponentAt(i, tabComp);
        }
    }

    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(C_SURFACE);
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(14, 18, 14, 18)));

        JLabel titulo = new JLabel("Gestión de contactos");
        titulo.setForeground(C_TEXT);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subt = new JLabel("Interfaz minimalista · tabla ordenable · búsqueda en vivo · atajos Ctrl+S / Ctrl+E");
        subt.setForeground(C_MUTED);
        subt.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(4, 1));
        stripe.setBackground(C_ACCENT);

        JPanel textos = new JPanel(new BorderLayout(0, 4));
        textos.setOpaque(false);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(subt, BorderLayout.CENTER);

        JPanel oeste = new JPanel(new BorderLayout(8, 0));
        oeste.setOpaque(false);
        oeste.add(stripe, BorderLayout.WEST);
        oeste.add(textos, BorderLayout.CENTER);

        cab.add(oeste, BorderLayout.WEST);
        return cab;
    }

    private JPanel construirPanelGestion() {
        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setOpaque(false);
        raiz.setBorder(new EmptyBorder(8, 4, 8, 4));
        raiz.add(crearTarjetaFormulario(), BorderLayout.NORTH);
        raiz.add(crearTarjetaTabla(), BorderLayout.CENTER);
        raiz.add(crearTarjetaBusqueda(), BorderLayout.SOUTH);
        return raiz;
    }

    private JPanel crearTarjetaFormulario() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(C_CARD);
        card.setBorder(tarjetaConTitulo("Datos del contacto"));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;

        txt_nombre = new JTextField();
        txt_telefono = new JTextField();
        txt_correo = new JTextField();
        estiloCampo(txt_nombre);
        estiloCampo(txt_telefono);
        estiloCampo(txt_correo);

        cmb_categoria = new JComboBox<>(new String[]{"Elija una categoría", "Familia", "Amigos", "Trabajo"});
        estiloCombo(cmb_categoria);

        chb_favoritos = new JCheckBox("Contacto favorito");
        chb_favoritos.setOpaque(false);
        chb_favoritos.setForeground(C_TEXT);
        chb_favoritos.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btn_add = crearBoton("Agregar", C_ACCENT);
        btn_editar = crearBoton("Modificar", C_ACCENT_DIM);
        btn_eliminar = crearBoton("Eliminar", C_DANGER);
        btn_exportar = crearBoton("Exportar CSV", new Color(0x0D9488));

        int y = 0;
        g.gridx = 0;
        g.gridy = y;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        grid.add(etiqueta("Nombre"), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        grid.add(txt_nombre, g);

        g.gridx = 0;
        g.gridy = ++y;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        grid.add(etiqueta("Teléfono"), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        grid.add(txt_telefono, g);

        g.gridx = 0;
        g.gridy = ++y;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        grid.add(etiqueta("Correo electrónico"), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        grid.add(txt_correo, g);

        g.gridx = 0;
        g.gridy = ++y;
        grid.add(etiqueta("Categoría"), g);
        g.gridx = 1;
        JPanel filaCat = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaCat.setOpaque(false);
        filaCat.add(cmb_categoria);
        filaCat.add(chb_favoritos);
        g.fill = GridBagConstraints.HORIZONTAL;
        grid.add(filaCat, g);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        acciones.setOpaque(false);
        acciones.add(btn_add);
        acciones.add(btn_editar);
        acciones.add(btn_eliminar);

        g.gridx = 0;
        g.gridy = ++y;
        g.gridwidth = 2;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        grid.add(acciones, g);

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private Border tarjetaConTitulo(String titulo) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER), titulo);
        tb.setTitleFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        tb.setTitleColor(C_CYAN);
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                BorderFactory.createCompoundBorder(new EmptyBorder(12, 14, 14, 14), tb));
    }

    private JPanel crearTarjetaTabla() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel hint = new JLabel("Tabla de contactos · clic en cabeceras para ordenar · clic derecho: Editar / Eliminar");
        hint.setForeground(C_MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(hint, BorderLayout.NORTH);

        String[] columnas = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setRowHeight(30);
        tablaContactos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaContactos.setForeground(C_TEXT);
        tablaContactos.setSelectionBackground(C_ACCENT_DIM);
        tablaContactos.setSelectionForeground(Color.WHITE);
        tablaContactos.setGridColor(C_BORDER);
        tablaContactos.setShowHorizontalLines(true);
        tablaContactos.setShowVerticalLines(false);

        JTableHeader header = tablaContactos.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        header.setBackground(C_SURFACE);
        header.setForeground(C_TEXT);
        Dimension hp = header.getPreferredSize();
        header.setPreferredSize(new Dimension(hp.width, 32));

        aplicarFilasAlternas();

        JScrollPane scroll = new JScrollPane(tablaContactos);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER, 1, true));
        scroll.getViewport().setBackground(C_ROW_A);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void aplicarFilasAlternas() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? C_ROW_A : C_ROW_B);
                    c.setForeground(C_TEXT);
                }
                return c;
            }
        };
        for (int i = 0; i < tablaContactos.getColumnCount(); i++) {
            tablaContactos.getColumnModel().getColumn(i).setCellRenderer(r);
        }
    }

    private JPanel crearTarjetaBusqueda() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)));

        JPanel izq = new JPanel(new BorderLayout(6, 0));
        izq.setOpaque(false);
        JLabel l = etiqueta("Buscar (filtra la tabla en tiempo real)");
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        txt_buscar = new JTextField();
        estiloCampo(txt_buscar);
        txt_buscar.setToolTipText("Escribe para filtrar por cualquier columna visible");
        izq.add(l, BorderLayout.NORTH);
        izq.add(txt_buscar, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(btn_exportar);

        card.add(izq, BorderLayout.CENTER);
        card.add(der, BorderLayout.EAST);
        return card;
    }

    private JPanel construirPanelEstadisticas() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel intro = new JLabel("<html><body style='width:520px;color:#B0B0B0;font-family:Segoe UI;font-size:13px'>"
                + "Resumen calculado a partir de los contactos cargados en memoria. "
                + "Se actualiza al agregar, modificar o eliminar registros.</body></html>");
        root.add(intro, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);

        lblStatTotal = crearCeldaKpi("Total contactos", "0");
        lblStatFavoritos = crearCeldaKpi("Favoritos", "0");
        lblStatFamilia = crearCeldaKpi("Familia", "0");
        lblStatAmigos = crearCeldaKpi("Amigos", "0");
        lblStatTrabajo = crearCeldaKpi("Trabajo", "0");

        grid.add(envolverKpi(lblStatTotal));
        grid.add(envolverKpi(lblStatFavoritos));
        grid.add(envolverKpi(lblStatFamilia));
        grid.add(envolverKpi(lblStatAmigos));
        grid.add(envolverKpi(lblStatTrabajo));
        grid.add(new JPanel()); // hueco visual

        root.add(grid, BorderLayout.CENTER);
        return root;
    }

    private JLabel crearCeldaKpi(String titulo, String valorInicial) {
        JLabel v = new JLabel(valorInicial, SwingConstants.CENTER);
        v.setName(titulo);
        v.setForeground(C_TEXT);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return v;
    }

    private JPanel envolverKpi(JLabel valor) {
        JPanel box = new JPanel(new BorderLayout(0, 6));
        box.setBackground(C_CARD);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(14, 12, 14, 12)));
        String titulo = valor.getName();
        JLabel t = new JLabel(titulo, SwingConstants.CENTER);
        t.setForeground(C_MUTED);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.add(t, BorderLayout.NORTH);
        box.add(valor, BorderLayout.CENTER);
        return box;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(C_MUTED);
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        return l;
    }

    private void estiloCampo(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(C_TEXT);
        f.setCaretColor(C_CYAN);
        f.setBackground(new Color(0x252525));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(7, 10, 7, 10)));
        f.setPreferredSize(new Dimension(180, 34));
    }

    private void estiloCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(C_TEXT);
        combo.setBackground(new Color(0x252525));
        combo.setPreferredSize(new Dimension(200, 34));
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(fondo);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        final Color base = fondo;
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(base.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(base);
            }
        });
        return b;
    }

    private void aplicarEstilosFinales() {
        panelPestanas.setBackground(C_SURFACE);
        for (int i = 0; i < panelPestanas.getTabCount(); i++) {
            Component c = panelPestanas.getComponentAt(i);
            if (c != null) {
                c.setBackground(C_SURFACE);
            }
        }
    }

    // ——— Accesores para el controlador (MVC) ———

    public DefaultTableModel getModeloTabla() {
        return modeloTabla;
    }

    public JTable getTablaContactos() {
        return tablaContactos;
    }

    public JTextField getTxtBuscar() {
        return txt_buscar;
    }

    public JTextField getTxtNombre() {
        return txt_nombre;
    }

    public JTextField getTxtTelefono() {
        return txt_telefono;
    }

    public JTextField getTxtCorreo() {
        return txt_correo;
    }

    public JComboBox<String> getCmbCategoria() {
        return cmb_categoria;
    }

    public JCheckBox getChbFavoritos() {
        return chb_favoritos;
    }

    public JButton getBtnAdd() {
        return btn_add;
    }

    public JButton getBtnEditar() {
        return btn_editar;
    }

    public JButton getBtnEliminar() {
        return btn_eliminar;
    }

    public JButton getBtnExportar() {
        return btn_exportar;
    }

    public JProgressBar getBarraProgreso() {
        return barraProgreso;
    }

    public JPopupMenu getMenuContextualTabla() {
        return menuContextualTabla;
    }

    public JMenuItem getMitContextoEditar() {
        return mitContextoEditar;
    }

    public JMenuItem getMitContextoEliminar() {
        return mitContextoEliminar;
    }

    public JLabel getLblStatTotal() {
        return lblStatTotal;
    }

    public JLabel getLblStatFavoritos() {
        return lblStatFavoritos;
    }

    public JLabel getLblStatFamilia() {
        return lblStatFamilia;
    }

    public JLabel getLblStatAmigos() {
        return lblStatAmigos;
    }

    public JLabel getLblStatTrabajo() {
        return lblStatTrabajo;
    }

    public Color getColorTextoPrincipal() {
        return C_TEXT;
    }
}
