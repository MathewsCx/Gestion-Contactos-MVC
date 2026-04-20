import controlador.logicaContactos;
import modelo.PersonaDAO;
import vista.VentanaContactos;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada: ensambla MVC (modelo {@link PersonaDAO}, vista {@link VentanaContactos},
 * controlador {@link logicaContactos}).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaContactos.prepararTemaGlobal();
            PersonaDAO modelo = new PersonaDAO();
            VentanaContactos vista = new VentanaContactos();
            new logicaContactos(vista, modelo);
            vista.setVisible(true);
        });
    }
}
