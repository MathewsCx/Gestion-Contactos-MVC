package modelo;

public class Persona {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String categoria;
    private boolean favorito;

    public Persona(String nombre, String apellido, String telefono, String email, String categoria, boolean favorito) {
        this.nombre = nombre != null ? nombre : "";
        this.apellido = apellido != null ? apellido : "";
        this.telefono = telefono;
        this.email = email;
        this.categoria = categoria;
        this.favorito = favorito;
    }

    /** Compatibilidad con registros antiguos (sin apellido). */
    public Persona(String nombre, String telefono, String email, String categoria, boolean favorito) {
        this(nombre, "", telefono, email, categoria, favorito);
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public String nombreCompleto() {
        String a = apellido == null ? "" : apellido.trim();
        if (a.isEmpty()) {
            return nombre;
        }
        return nombre + " " + a;
    }

    public String datosContacto() {
        return escapeCsv(nombre) + "," + escapeCsv(apellido) + "," + escapeCsv(telefono) + ","
                + escapeCsv(email) + "," + escapeCsv(categoria) + "," + favorito;
    }

    private static String escapeCsv(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    public String formatoLista() {
        return nombreCompleto() + " - " + categoria + " (" + telefono + ")";
    }
}
