# Sistema de Gestión de Contactos - MVC (Elite Edition)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![Arquitectura](https://img.shields.io/badge/Arquitectura-MVC-success?style=for-the-badge)

Aplicación de escritorio de alto rendimiento desarrollada en Java, aplicando estrictamente el patrón arquitectónico **Modelo-Vista-Controlador (MVC)**. Diseñada con un enfoque en la usabilidad, la eficiencia en la entrada de datos y una estética minimalista "Dark Mode".

Proyecto académico para la asignatura de Programación de Interfaces Gráficas - Universidad Politécnica Salesiana.

## 🚀 Características Principales

### Interfaz y Experiencia de Usuario (UI/UX)
* **Estética Elite Dark Mode:** Interfaz personalizada construida sobre Swing (`BasicTabbedPaneUI`), utilizando paletas oscuras y tipografía *Segoe UI* para reducir la fatiga visual.
* **Navegación por Pestañas:** Implementación de `JTabbedPane` para separar el módulo de Gestión del dashboard de Estadísticas.
* **Feedback Asíncrono:** Uso de `JProgressBar` y `SwingWorker` para simular la carga segura de datos en segundo plano sin congelar la interfaz principal.
* **Dashboard Dinámico:** Panel de control (`GridBagLayout`) con tarjetas KPI que calculan y actualizan en tiempo real el total de contactos y su distribución.

### Lógica y Manipulación de Datos
* **Filtrado en Tiempo Real:** El `JTable` cuenta con un `TableRowSorter` y `RowFilter` (Case-insensitive) que filtra instantáneamente los registros conforme el usuario escribe.
* **Manejo Avanzado de Eventos:** * Integración de `KeyBindings` a nivel global (`Ctrl+S` para guardar, `Ctrl+E` para exportar).
  * `JPopupMenu` interactivo (clic derecho sobre las filas detectado vía `MouseAdapter`) para operaciones rápidas de Edición y Eliminación.
* **Persistencia Robusta:** Los datos se almacenan en formato `.csv` manejados a través del patrón DAO.
* **Exportación Selectiva:** Funcionalidad para exportar contactos mediante `JFileChooser`. Solo exporta las filas visibles (respeta filtros) y utiliza codificación UTF-8 con inyección BOM (`\ufeff`) para compatibilidad nativa con Excel.

## 🏗️ Arquitectura del Sistema (Patrón MVC)

* **Modelo (`src/modelo/`):** Entidad `Persona` y lógica de persistencia `PersonaDAO` (`C:/gestionContactos/datosContactos.csv`).
* **Vista (`src/vista/`):** Únicamente interfaces gráficas (`VentanaContactos`). Cero lógica de negocio, expone componentes mediante getters.
* **Controlador (`src/controlador/`):** La clase `logicaContactos` orquesta el sistema, vinculando los listeners e inyectando dependencias.

## ⚙️ Compilación y Ejecución


# Compilar
javac -encoding UTF-8 -d out -sourcepath src src/Main.java src/vista/VentanaContactos.java src/controlador/logicaContactos.java src/modelo/Persona.java src/modelo/PersonaDAO.java

# Ejecutar
java -cp out Main
👨‍💻 Autor
Paul Mateo Ramos Toapanta - Ingeniería de Software (UPS)
