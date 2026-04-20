package controlador;

import modelo.Persona;
import modelo.PersonaDAO;
import vista.VentanaContactos;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controlador MVC para la gestión de contactos en {@link VentanaContactos}.
 * <p>
 * Responsabilidades: enlazar la vista con {@link PersonaDAO}, registrar todos los
 * listeners (acciones, teclado, ratón, filtrado de tabla), simular carga inicial con
 * {@link JProgressBar}, exportación CSV avanzada y actualización de estadísticas.
 * </p>
 */
public class logicaContactos {

    private static final String PLACEHOLDER_CATEGORIA = "Elija una categoría";

    private final VentanaContactos vista;
    private final PersonaDAO modelo;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Construye el controlador, enlaza modelo y vista e inicia el comportamiento de la aplicación.
     *
     * @param vista  capa de presentación (solo UI)
     * @param modelo acceso a persistencia CSV
     */
    public logicaContactos(VentanaContactos vista, PersonaDAO modelo) {
        this.vista = vista;
        this.modelo = modelo;
        configurarOrdenamientoYFiltro();
        registrarAccionesBotones();
        registrarMenuContextual();
        registrarAtajosTeclado();
        registrarSeleccionTabla();
        registrarFiltroBusqueda();
        iniciarCargaSimulada();
    }

    // ——— Configuración de tabla ———

    private void configurarOrdenamientoYFiltro() {
        DefaultTableModel tm = vista.getModeloTabla();
        sorter = new TableRowSorter<>(tm);
        vista.getTablaContactos().setRowSorter(sorter);
        // Ordenación nativa por columnas (clic en cabeceras).
        for (int i = 0; i < tm.getColumnCount(); i++) {
            sorter.setSortable(i, true);
        }
    }

    private void aplicarFiltroTexto() {
        if (sorter == null) {
            return;
        }
        String texto = vista.getTxtBuscar().getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        // Búsqueda insensible a mayúsculas en todas las columnas visibles.
        String regex = "(?i)" + Pattern.quote(texto);
        try {
            sorter.setRowFilter(RowFilter.regexFilter(regex));
        } catch (Exception ex) {
            sorter.setRowFilter(null);
        }
    }

    private void registrarFiltroBusqueda() {
        vista.getTxtBuscar().getDocument().addDocumentListener(new DocumentListener() {
            private void actualizar() {
                SwingUtilities.invokeLater(logicaContactos.this::aplicarFiltroTexto);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizar();
            }
        });
    }

    // ——— Botones ———

    private void registrarAccionesBotones() {
        vista.getBtnAdd().addActionListener(e -> agregarOGuardarContacto());
        vista.getBtnEditar().addActionListener(e -> modificarContactoSeleccionado());
        vista.getBtnEliminar().addActionListener(e -> eliminarContactoSeleccionado());
        vista.getBtnExportar().addActionListener(e -> exportarCsvConOpciones());
    }

    // ——— Menú contextual (clic derecho en fila) ———

    private void registrarMenuContextual() {
        JTable tabla = vista.getTablaContactos();
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                manejarPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                manejarPopup(e);
            }

            private void manejarPopup(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    return;
                }
                int filaVista = tabla.rowAtPoint(e.getPoint());
                if (filaVista < 0) {
                    return;
                }
                tabla.setRowSelectionInterval(filaVista, filaVista);
                vista.getMenuContextualTabla().show(tabla, e.getX(), e.getY());
            }
        });

        vista.getMitContextoEditar().addActionListener(e -> {
            int v = vista.getTablaContactos().getSelectedRow();
            if (v >= 0) {
                cargarFormularioDesdeFilaVista(v);
            }
        });
        vista.getMitContextoEliminar().addActionListener(e -> eliminarContactoSeleccionado());
    }

    // ——— Atajos globales ———

    private void registrarAtajosTeclado() {
        JRootPane root = vista.getRootPane();
        int cond = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap im = root.getInputMap(cond);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("control S"), "guardarContacto");
        am.put("guardarContacto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarOGuardarContacto();
            }
        });

        im.put(KeyStroke.getKeyStroke("control E"), "exportarCsv");
        am.put("exportarCsv", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarCsvConOpciones();
            }
        });
    }

    private void registrarSeleccionTabla() {
        vista.getTablaContactos().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int v = vista.getTablaContactos().getSelectedRow();
                if (v >= 0) {
                    cargarFormularioDesdeFilaVista(v);
                }
            }
        });
    }

    // ——— Carga inicial simulada ———

    private void iniciarCargaSimulada() {
        vista.getBarraProgreso().setIndeterminate(false);
        vista.getBarraProgreso().setValue(0);
        vista.getBarraProgreso().setString("Preparando interfaz…");

        SwingWorker<List<Persona>, Integer> worker = new SwingWorker<List<Persona>, Integer>() {
            @Override
            protected List<Persona> doInBackground() throws Exception {
                for (int p = 5; p <= 45; p += 10) {
                    Thread.sleep(35);
                    publish(p);
                }
                List<Persona> datos = modelo.leerArchivo();
                for (int p = 55; p <= 95; p += 10) {
                    Thread.sleep(25);
                    publish(p);
                }
                return datos;
            }

            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int v = chunks.get(chunks.size() - 1);
                    vista.getBarraProgreso().setValue(v);
                    vista.getBarraProgreso().setString("Cargando contactos desde almacenamiento…");
                }
            }

            @Override
            protected void done() {
                try {
                    List<Persona> lista = get();
                    poblarTablaDesdeLista(lista);
                    vista.getBarraProgreso().setValue(100);
                    vista.getBarraProgreso().setString("Listo · " + lista.size() + " contacto(s) cargados");
                    actualizarPanelEstadisticas(lista);
                } catch (Exception ex) {
                    vista.getBarraProgreso().setString("Error al cargar datos");
                    notificarError("No se pudieron leer los contactos: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void poblarTablaDesdeLista(List<Persona> lista) {
        DefaultTableModel tm = vista.getModeloTabla();
        tm.setRowCount(0);
        for (Persona p : lista) {
            tm.addRow(filaDesdePersona(p));
        }
        aplicarFiltroTexto();
        actualizarPanelEstadisticas(listaDesdeTabla());
    }

    private Object[] filaDesdePersona(Persona p) {
        return new Object[]{
                p.getNombre(),
                p.getTelefono(),
                p.getEmail(),
                p.getCategoria(),
                p.isFavorito() ? "Sí" : "No"
        };
    }

    private List<Persona> listaDesdeTabla() {
        DefaultTableModel tm = vista.getModeloTabla();
        List<Persona> out = new ArrayList<>();
        for (int i = 0; i < tm.getRowCount(); i++) {
            out.add(personaDesdeFilaModelo(i));
        }
        return out;
    }

    private Persona personaDesdeFilaModelo(int filaModelo) {
        DefaultTableModel tm = vista.getModeloTabla();
        return new Persona(
                String.valueOf(tm.getValueAt(filaModelo, 0)),
                String.valueOf(tm.getValueAt(filaModelo, 1)),
                String.valueOf(tm.getValueAt(filaModelo, 2)),
                String.valueOf(tm.getValueAt(filaModelo, 3)),
                parseFavorito(tm.getValueAt(filaModelo, 4))
        );
    }

    private boolean parseFavorito(Object valor) {
        String s = String.valueOf(valor).trim();
        return s.equalsIgnoreCase("sí") || s.equalsIgnoreCase("si")
                || s.equalsIgnoreCase("true") || s.equals("1");
    }

    private void persistirListaActual() throws IOException {
        modelo.actualizarContactos(listaDesdeTabla());
    }

    private void cargarFormularioDesdeFilaVista(int filaVista) {
        JTable t = vista.getTablaContactos();
        int filaModelo = t.convertRowIndexToModel(filaVista);
        DefaultTableModel tm = vista.getModeloTabla();
        vista.getTxtNombre().setText(String.valueOf(tm.getValueAt(filaModelo, 0)));
        vista.getTxtTelefono().setText(String.valueOf(tm.getValueAt(filaModelo, 1)));
        vista.getTxtCorreo().setText(String.valueOf(tm.getValueAt(filaModelo, 2)));
        String cat = String.valueOf(tm.getValueAt(filaModelo, 3));
        vista.getCmbCategoria().setSelectedItem(cat);
        vista.getChbFavoritos().setSelected(parseFavorito(tm.getValueAt(filaModelo, 4)));
    }

    private void agregarOGuardarContacto() {
        if (!validarFormulario()) {
            return;
        }
        Persona nuevo = leerPersonaDesdeFormulario();
        DefaultTableModel tm = vista.getModeloTabla();
        tm.addRow(filaDesdePersona(nuevo));
        try {
            persistirListaActual();
            notificarInfo("Contacto guardado correctamente (Ctrl+S).");
            limpiarFormulario();
            vista.getTablaContactos().clearSelection();
            actualizarPanelEstadisticas(listaDesdeTabla());
            vista.getBarraProgreso().setString("Contacto agregado · sincronizado");
        } catch (IOException ex) {
            notificarError("No se pudo guardar en disco: " + ex.getMessage());
            tm.removeRow(tm.getRowCount() - 1);
        }
    }

    private void modificarContactoSeleccionado() {
        int v = vista.getTablaContactos().getSelectedRow();
        if (v < 0) {
            notificarInfo("Seleccione una fila en la tabla para modificar.");
            return;
        }
        if (!validarFormulario()) {
            return;
        }
        int m = vista.getTablaContactos().convertRowIndexToModel(v);
        DefaultTableModel tm = vista.getModeloTabla();
        Persona p = leerPersonaDesdeFormulario();
        Object[] fila = filaDesdePersona(p);
        for (int c = 0; c < fila.length; c++) {
            tm.setValueAt(fila[c], m, c);
        }
        try {
            persistirListaActual();
            notificarInfo("Contacto actualizado.");
            actualizarPanelEstadisticas(listaDesdeTabla());
            vista.getBarraProgreso().setString("Cambios guardados");
        } catch (IOException ex) {
            notificarError("Error al guardar cambios: " + ex.getMessage());
        }
    }

    private void eliminarContactoSeleccionado() {
        int v = vista.getTablaContactos().getSelectedRow();
        if (v < 0) {
            notificarInfo("Seleccione una fila para eliminar.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(
                vista,
                "¿Eliminar el contacto seleccionado? Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        int m = vista.getTablaContactos().convertRowIndexToModel(v);
        DefaultTableModel tm = vista.getModeloTabla();
        tm.removeRow(m);
        try {
            persistirListaActual();
            notificarInfo("Contacto eliminado.");
            limpiarFormulario();
            actualizarPanelEstadisticas(listaDesdeTabla());
            vista.getBarraProgreso().setString("Lista actualizada");
        } catch (IOException ex) {
            notificarError("Error al persistir tras eliminar: " + ex.getMessage());
        }
    }

    private Persona leerPersonaDesdeFormulario() {
        String cat = String.valueOf(vista.getCmbCategoria().getSelectedItem());
        return new Persona(
                vista.getTxtNombre().getText().trim(),
                vista.getTxtTelefono().getText().trim(),
                vista.getTxtCorreo().getText().trim(),
                cat,
                vista.getChbFavoritos().isSelected()
        );
    }

    private boolean validarFormulario() {
        String n = vista.getTxtNombre().getText().trim();
        String t = vista.getTxtTelefono().getText().trim();
        String em = vista.getTxtCorreo().getText().trim();
        Object catObj = vista.getCmbCategoria().getSelectedItem();
        String cat = catObj != null ? catObj.toString() : "";

        if (n.isEmpty() || t.isEmpty() || em.isEmpty()) {
            notificarInfo("Complete nombre, teléfono y correo electrónico.");
            return false;
        }
        if (cat.isEmpty() || PLACEHOLDER_CATEGORIA.equals(cat)) {
            notificarInfo("Seleccione una categoría válida.");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        vista.getTxtNombre().setText("");
        vista.getTxtTelefono().setText("");
        vista.getTxtCorreo().setText("");
        vista.getCmbCategoria().setSelectedIndex(0);
        vista.getChbFavoritos().setSelected(false);
    }

    // ——— Exportación CSV avanzada ———

    private void exportarCsvConOpciones() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar contactos a CSV");
        chooser.setSelectedFile(new File("contactos_exportados.csv"));

        JCheckBox soloVisibles = new JCheckBox("Exportar solo filas visibles (respeta filtro actual)", true);
        soloVisibles.setOpaque(false);
        soloVisibles.setForeground(vista.getColorTextoPrincipal());
        JPanel acc = new JPanel(new BorderLayout());
        acc.setOpaque(false);
        acc.add(soloVisibles, BorderLayout.CENTER);
        chooser.setAccessory(acc);

        int res = chooser.showSaveDialog(vista);
        if (res != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File destino = chooser.getSelectedFile();
        if (destino == null) {
            return;
        }

        try {
            exportarArchivoCsv(destino, soloVisibles.isSelected());
            notificarInfo("Archivo exportado correctamente:\n" + destino.getAbsolutePath());
            vista.getBarraProgreso().setString("Exportación completada · " + destino.getName());
        } catch (IOException ex) {
            notificarError("No se pudo exportar: " + ex.getMessage());
        }
    }

    private void exportarArchivoCsv(File destino, boolean soloFilasVisibles) throws IOException {
        JTable tabla = vista.getTablaContactos();
        DefaultTableModel tm = vista.getModeloTabla();
        String[] cabeceras = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};

        try (BufferedWriter bw = Files.newBufferedWriter(destino.toPath(), StandardCharsets.UTF_8)) {
            // BOM UTF-8 para compatibilidad con Excel al abrir CSV.
            bw.write('\uFEFF');
            bw.write(String.join(",", cabeceras));
            bw.newLine();

            if (soloFilasVisibles) {
                for (int vr = 0; vr < tabla.getRowCount(); vr++) {
                    int mr = tabla.convertRowIndexToModel(vr);
                    escribirLineaCsv(bw, tm, mr);
                }
            } else {
                for (int mr = 0; mr < tm.getRowCount(); mr++) {
                    escribirLineaCsv(bw, tm, mr);
                }
            }
        }
    }

    private void escribirLineaCsv(BufferedWriter bw, DefaultTableModel tm, int filaModelo) throws IOException {
        Persona p = personaDesdeFilaModelo(filaModelo);
        bw.write(p.datosContacto());
        bw.newLine();
    }

    // ——— Estadísticas ———

    private void actualizarPanelEstadisticas(List<Persona> lista) {
        int total = lista.size();
        long fav = lista.stream().filter(Persona::isFavorito).count();
        long fam = lista.stream().filter(p -> "Familia".equals(p.getCategoria())).count();
        long am = lista.stream().filter(p -> "Amigos".equals(p.getCategoria())).count();
        long tr = lista.stream().filter(p -> "Trabajo".equals(p.getCategoria())).count();

        vista.getLblStatTotal().setText(String.valueOf(total));
        vista.getLblStatFavoritos().setText(String.valueOf(fav));
        vista.getLblStatFamilia().setText(String.valueOf(fam));
        vista.getLblStatAmigos().setText(String.valueOf(am));
        vista.getLblStatTrabajo().setText(String.valueOf(tr));
    }

    private void notificarInfo(String msg) {
        JOptionPane.showMessageDialog(vista, msg, "Gestión de contactos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void notificarError(String msg) {
        JOptionPane.showMessageDialog(vista, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
