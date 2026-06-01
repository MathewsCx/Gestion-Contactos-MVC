# Sistema de Gestión de Contactos - MVC

Aplicación de escritorio en Java Swing (patrón **MVC**) para la asignatura Programación de Interfaces Gráficas — UPS.

## Características

- **Unidad 2:** Dark mode, layouts (`BorderLayout`, `GridBagLayout`), i18n ES / EN / PT con `ResourceBundle`.
- **Unidad 3:** Programación concurrente:
  - Validación de duplicados en segundo plano (`SwingWorker`).
  - Búsqueda con `SwingWorker` + debounce (UI no se congela).
  - Exportación CSV con `ExecutorService` y `synchronized`.
  - Notificaciones en barra inferior (`SwingUtilities.invokeLater`).
  - Bloqueo de edición por teléfono y `synchronized` en la lista de contactos.
- **Unidad 4:** Gestión de dependencias y JSON:
  - Migración del proyecto a **Maven** (`pom.xml`).
  - Integración de **FlatLaf** para apariencia moderna.
  - Integración de **Gson** para serialización/deserialización JSON.
  - Importación de contactos desde archivo JSON.
  - Exportación de contactos a JSON o CSV.
  - Configuración de versiones estables y exclusión transitive en dependencias.

## Estructura

```
src/
  Main.java
  modelo/          Persona, PersonaDAO
  vista/           VentanaContactos
  controlador/     logicaContactos
  concurrente/     Hilos, bloqueos, exportador
  util/            I18nManager
  i18n/            messages_es|en|pt.properties
```

## Compilar y ejecutar

```powershell
.\compile.ps1
mvn -q exec:java
```

Requisito: Maven 3.9+ disponible en `PATH`.

Datos: `c:/gestionContactos/datosContactos.csv`

## Autor

Paul Mateo Ramos Toapanta — Ingeniería de Software (UPS)
