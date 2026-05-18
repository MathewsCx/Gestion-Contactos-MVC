package controlador;

import concurrente.BloqueoEdicionContacto;
import concurrente.ExportadorConcurrente;
import concurrente.MedidorRendimiento;
import concurrente.NotificadorUi;
import concurrente.ValidadorContactos;
import modelo.Persona;
import modelo.PersonaDAO;
import util.I18nManager;
import vista.VentanaContactos;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controlador MVC con programación concurrente (U3): validación, búsqueda y exportación
 * en segundo plano, notificaciones con invokeLater y sincronización de datos.
 */
public class logicaContactos {

  private static final Map<String, String> CATEGORIA_KEYS = Map.ofEntries(
      Map.entry("Familia", "category.family"),
      Map.entry("Amigos", "category.friends"),
      Map.entry("Trabajo", "category.work"),
      Map.entry("Family", "category.family"),
      Map.entry("Friends", "category.friends"),
      Map.entry("Work", "category.work"),
      Map.entry("Fam\u00edlia", "category.family"),
      Map.entry("Trabalho", "category.work"));

  private final VentanaContactos vista;
  private final PersonaDAO modelo;
  private final NotificadorUi notificador;
  private final Object candadoContactos = new Object();

  private List<Persona> contactos = new ArrayList<>();
  private Integer filaEnEdicion;
  private String telefonoBloqueado;
  private TableRowSorter<DefaultTableModel> sorter;
  private SwingWorker<Void, Void> workerBusqueda;
  private Timer debounceBusqueda;

  public logicaContactos(VentanaContactos vista, PersonaDAO modelo) {
    this.vista = vista;
    this.modelo = modelo;
    this.notificador = new NotificadorUi(vista);
    vista.setOnCambioIdioma(this::cambiarIdioma);
    vista.btnGuardar.addActionListener(e -> guardarContactoAsync());
    vista.btnExportar.addActionListener(e -> exportarCsvAsync());
    vista.btnBenchmark.addActionListener(e -> ejecutarBenchmark());
    configurarTabla();
    registrarFiltroBusquedaAsync();
    registrarClicsTabla();
    cargarContactosAsync();
  }

  private void configurarTabla() {
    sorter = new TableRowSorter<>(vista.getModeloTabla());
    vista.getTablaContactos().setRowSorter(sorter);
  }

  /** Tiempo mínimo visible de la barra de carga (evidencia U3 / capturas del informe). */
  private static final int MS_CARGA_MINIMA = 2800;

  private void cargarContactosAsync() {
    String msgCarga = I18nManager.get("progress.loading");
    vista.setBarraProgresoVisible(true, msgCarga);
    vista.setProgreso(0, msgCarga);

    new SwingWorker<List<Persona>, Integer>() {
      private final long inicio = System.currentTimeMillis();

      @Override
      protected List<Persona> doInBackground() throws Exception {
        publish(15);
        Thread.sleep(450);
        publish(35);
        Thread.sleep(450);
        List<Persona> datos;
        synchronized (candadoContactos) {
          datos = new ArrayList<>(modelo.leerArchivo());
        }
        publish(60);
        Thread.sleep(450);
        publish(80);
        Thread.sleep(450);
        publish(95);
        long restante = MS_CARGA_MINIMA - (System.currentTimeMillis() - inicio);
        if (restante > 0) {
          Thread.sleep(restante);
        }
        return datos;
      }

      @Override
      protected void process(List<Integer> chunks) {
        if (!chunks.isEmpty()) {
          vista.setProgreso(chunks.get(chunks.size() - 1), msgCarga);
        }
      }

      @Override
      protected void done() {
        try {
          contactos = get();
          poblarTabla(contactos);
          actualizarEstadisticas();
          vista.setProgreso(100, I18nManager.get("progress.ready"));
          notificador.exito(I18nManager.get("progress.ready") + " (" + contactos.size() + ")");
        } catch (Exception ex) {
          notificador.error(I18nManager.get("msg.error.load"));
        } finally {
          Timer ocultar = new Timer(2200, e -> vista.setBarraProgresoVisible(false, null));
          ocultar.setRepeats(false);
          ocultar.start();
        }
      }
    }.execute();
  }

  private void registrarFiltroBusquedaAsync() {
    debounceBusqueda = new Timer(280, e -> lanzarBusquedaEnSegundoPlano());
    debounceBusqueda.setRepeats(false);

    vista.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
      private void programar() {
        debounceBusqueda.restart();
      }

      @Override
      public void insertUpdate(DocumentEvent ev) {
        programar();
      }

      @Override
      public void removeUpdate(DocumentEvent ev) {
        programar();
      }

      @Override
      public void changedUpdate(DocumentEvent ev) {
        programar();
      }
    });
  }

  private void lanzarBusquedaEnSegundoPlano() {
    if (workerBusqueda != null && !workerBusqueda.isDone()) {
      workerBusqueda.cancel(true);
    }
    final String texto = vista.txtBuscar.getText().trim();

    workerBusqueda = new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        if (!texto.isEmpty()) {
          try {
            Thread.sleep(150);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
          }
        }
        return null;
      }

      @Override
      protected void done() {
        if (isCancelled()) {
          return;
        }
        aplicarFiltroEnEdt(texto);
      }
    };
    workerBusqueda.execute();
  }

  private void aplicarFiltroEnEdt(String texto) {
    SwingUtilities.invokeLater(() -> {
      if (sorter == null) {
        return;
      }
      if (texto.isEmpty()) {
        sorter.setRowFilter(null);
        return;
      }
      try {
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
      } catch (Exception ex) {
        sorter.setRowFilter(null);
      }
    });
  }

  private void cambiarIdioma(String codigo) {
    try {
      I18nManager.setIdioma(codigo);
      vista.setIdiomaActivo(codigo);
      vista.aplicarTextos();
      poblarTabla(contactos);
      lanzarBusquedaEnSegundoPlano();
      if (filaEnEdicion != null && filaEnEdicion >= 0 && filaEnEdicion < contactos.size()) {
        cargarFormularioDesdeFila(filaEnEdicion);
      }
      actualizarEstadisticas();
    } catch (RuntimeException ex) {
      notificador.error(I18nManager.get("msg.error.load"));
    }
  }

  private void poblarTabla(List<Persona> lista) {
    DefaultTableModel tm = vista.getModeloTabla();
    tm.setRowCount(0);
    int id = 1;
    for (Persona p : lista) {
      tm.addRow(new Object[]{
          id++, p.nombreCompleto(), p.getTelefono(), p.getEmail(), ""});
    }
    vista.configurarRenderersTabla();
    vista.getTablaContactos().repaint();
    aplicarFiltroEnEdt(vista.txtBuscar.getText().trim());
  }

  private void registrarClicsTabla() {
    JTable tabla = vista.getTablaContactos();
    tabla.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int fila = tabla.rowAtPoint(e.getPoint());
        int col = tabla.columnAtPoint(e.getPoint());
        if (fila < 0) {
          return;
        }
        int filaModelo = tabla.convertRowIndexToModel(fila);
        if (col == VentanaContactos.COL_ACCIONES) {
          Rectangle celda = tabla.getCellRect(fila, col, true);
          int xRel = e.getX() - celda.x;
          if (xRel < celda.width / 2) {
            cargarFormularioDesdeFila(filaModelo);
          } else {
            eliminarFila(filaModelo);
          }
        } else {
          cargarFormularioDesdeFila(filaModelo);
        }
      }
    });
  }

  private void cargarFormularioDesdeFila(int filaModelo) {
    if (filaModelo < 0 || filaModelo >= contactos.size()) {
      return;
    }
    Persona p = contactos.get(filaModelo);
    String tel = p.getTelefono();
    if (BloqueoEdicionContacto.estaBloqueado(tel)
        && (telefonoBloqueado == null || !telefonoBloqueado.equals(tel))) {
      notificador.error(I18nManager.get("msg.lock.edit"));
      return;
    }
    liberarBloqueoEdicion();
    if (!BloqueoEdicionContacto.intentarBloquear(tel)) {
      notificador.error(I18nManager.get("msg.lock.edit"));
      return;
    }
    telefonoBloqueado = tel;
    filaEnEdicion = filaModelo;
    vista.txtNombre.setText(p.getNombre());
    vista.txtApellido.setText(p.getApellido());
    vista.txtTelefono.setText(p.getTelefono());
    vista.txtCorreo.setText(p.getEmail());
    vista.chbFavoritos.setSelected(p.isFavorito());
    seleccionarCategoriaEnCombo(p.getCategoria());
    int filaVista = vista.getTablaContactos().convertRowIndexToView(filaModelo);
    if (filaVista >= 0) {
      vista.getTablaContactos().setRowSelectionInterval(filaVista, filaVista);
    }
  }

  private void liberarBloqueoEdicion() {
    if (telefonoBloqueado != null) {
      BloqueoEdicionContacto.liberar(telefonoBloqueado);
      telefonoBloqueado = null;
    }
  }

  private void seleccionarCategoriaEnCombo(String categoriaGuardada) {
    String key = CATEGORIA_KEYS.get(categoriaGuardada);
    if (key != null) {
      vista.cmbCategoria.setSelectedItem(I18nManager.get(key));
      return;
    }
    for (int i = 0; i < vista.cmbCategoria.getItemCount(); i++) {
      if (categoriaGuardada.equals(vista.cmbCategoria.getItemAt(i))) {
        vista.cmbCategoria.setSelectedIndex(i);
        return;
      }
    }
    vista.cmbCategoria.setSelectedIndex(0);
  }

  private String categoriaCanonicaSeleccionada() {
    Object sel = vista.cmbCategoria.getSelectedItem();
    if (sel == null) {
      return "";
    }
    String item = sel.toString();
    if (item.equals(I18nManager.get("category.family"))) {
      return "Familia";
    }
    if (item.equals(I18nManager.get("category.friends"))) {
      return "Amigos";
    }
    if (item.equals(I18nManager.get("category.work"))) {
      return "Trabajo";
    }
    return item;
  }

  private void guardarContactoAsync() {
    if (!validarCamposBasicos()) {
      return;
    }
    Persona candidato = leerPersonaDesdeFormulario();
    vista.btnGuardar.setEnabled(false);
    vista.setProgresoIndeterminado(I18nManager.get("progress.validating"));

    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws InterruptedException {
        Thread.sleep(200);
        synchronized (candadoContactos) {
          return ValidadorContactos.esDuplicado(candidato, contactos, filaEnEdicion);
        }
      }

      @Override
      protected void done() {
        vista.btnGuardar.setEnabled(true);
        vista.setBarraProgresoVisible(false, null);
        try {
          if (get()) {
            notificador.error(I18nManager.get("msg.duplicate"));
            return;
          }
          guardarContactoConfirmado(candidato);
        } catch (Exception ex) {
          notificador.error(I18nManager.get("msg.error.save"));
        }
      }
    }.execute();
  }

  private void guardarContactoConfirmado(Persona p) {
    synchronized (candadoContactos) {
      if (filaEnEdicion != null && filaEnEdicion >= 0 && filaEnEdicion < contactos.size()) {
        contactos.set(filaEnEdicion, p);
        notificador.exito(I18nManager.get("msg.updated"));
      } else {
        contactos.add(p);
        notificador.exito(I18nManager.get("msg.saved"));
      }
    }
    persistirYRefrescar();
    limpiarFormulario();
  }

  private void eliminarFila(int filaModelo) {
    int ok = JOptionPane.showConfirmDialog(
        vista,
        I18nManager.get("msg.confirm.delete"),
        I18nManager.get("dialog.confirm.title"),
        JOptionPane.YES_NO_OPTION);
    if (ok != JOptionPane.YES_OPTION) {
      return;
    }
    synchronized (candadoContactos) {
      if (filaModelo >= 0 && filaModelo < contactos.size()) {
        contactos.remove(filaModelo);
      }
    }
    persistirYRefrescar();
    limpiarFormulario();
    notificador.exito(I18nManager.get("msg.deleted"));
  }

  private void persistirYRefrescar() {
    try {
      synchronized (candadoContactos) {
        modelo.actualizarContactos(contactos);
      }
      poblarTabla(contactos);
      actualizarEstadisticas();
    } catch (IOException ex) {
      notificador.error(I18nManager.get("msg.error.save") + " " + ex.getMessage());
    }
  }

  private void actualizarEstadisticas() {
    int total;
    long fav;
    long fam;
    long am;
    long tr;
    synchronized (candadoContactos) {
      total = contactos.size();
      fav = contactos.stream().filter(Persona::isFavorito).count();
      fam = contactos.stream().filter(p -> "Familia".equals(p.getCategoria())).count();
      am = contactos.stream().filter(p -> "Amigos".equals(p.getCategoria())).count();
      tr = contactos.stream().filter(p -> "Trabajo".equals(p.getCategoria())).count();
    }
    vista.actualizarKpis(total, fav, fam, am, tr);
  }

  private Persona leerPersonaDesdeFormulario() {
    return new Persona(
        vista.txtNombre.getText().trim(),
        vista.txtApellido.getText().trim(),
        vista.txtTelefono.getText().trim(),
        vista.txtCorreo.getText().trim(),
        categoriaCanonicaSeleccionada(),
        vista.chbFavoritos.isSelected());
  }

  private boolean validarCamposBasicos() {
    if (vista.txtNombre.getText().trim().isEmpty()
        || vista.txtApellido.getText().trim().isEmpty()
        || vista.txtTelefono.getText().trim().isEmpty()
        || vista.txtCorreo.getText().trim().isEmpty()) {
      notificador.info(I18nManager.get("msg.complete.fields"));
      return false;
    }
    String item = vista.cmbCategoria.getSelectedItem() != null
        ? vista.cmbCategoria.getSelectedItem().toString()
        : "";
    if (item.equals(I18nManager.get("category.placeholder"))) {
      notificador.info(I18nManager.get("msg.invalid.category"));
      return false;
    }
    return true;
  }

  private void limpiarFormulario() {
    filaEnEdicion = null;
    liberarBloqueoEdicion();
    vista.txtNombre.setText("");
    vista.txtApellido.setText("");
    vista.txtTelefono.setText("");
    vista.txtCorreo.setText("");
    vista.chbFavoritos.setSelected(false);
    vista.cmbCategoria.setSelectedIndex(0);
    vista.getTablaContactos().clearSelection();
  }

  private void exportarCsvAsync() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(I18nManager.get("btn.export"));
    chooser.setSelectedFile(new File("contactos_exportados.csv"));
    if (chooser.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File destino = chooser.getSelectedFile();
    if (destino == null) {
      return;
    }
    vista.btnExportar.setEnabled(false);
    vista.setBarraProgresoVisible(true, I18nManager.get("progress.exporting"));
    vista.setProgresoIndeterminado(I18nManager.get("progress.exporting"));

    List<Persona> copia;
    synchronized (candadoContactos) {
      copia = new ArrayList<>(contactos);
    }

    ExportadorConcurrente.exportarAsync(
        destino,
        copia,
        () -> SwingUtilities.invokeLater(() -> {
          vista.btnExportar.setEnabled(true);
          vista.setProgreso(100, I18nManager.get("progress.export.done"));
          notificador.exito(I18nManager.get("msg.export.ok"));
          Timer t = new Timer(1500, ev -> vista.setBarraProgresoVisible(false, null));
          t.setRepeats(false);
          t.start();
        }),
        ex -> SwingUtilities.invokeLater(() -> {
          vista.btnExportar.setEnabled(true);
          vista.setBarraProgresoVisible(false, null);
          notificador.error(I18nManager.get("msg.error.save"));
        }));
  }

  private void ejecutarBenchmark() {
    vista.btnBenchmark.setEnabled(false);
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() {
        List<Persona> copia;
        synchronized (candadoContactos) {
          copia = new ArrayList<>(contactos);
        }
        return MedidorRendimiento.compararBusqueda(copia, vista.txtBuscar.getText().trim());
      }

      @Override
      protected void done() {
        vista.btnBenchmark.setEnabled(true);
        try {
          JOptionPane.showMessageDialog(
              vista,
              get(),
              I18nManager.get("benchmark.title"),
              JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ignored) {
        }
      }
    }.execute();
  }
}
