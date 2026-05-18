package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Gestor centralizado de internacionalización mediante {@link ResourceBundle}.
 * Archivos: {@code i18n/messages_es.properties}, {@code messages_en}, {@code messages_pt}.
 */
public final class I18nManager {

    public static final String LANG_ES = "es";
    public static final String LANG_EN = "en";
    public static final String LANG_PT = "pt";

    private static final String BUNDLE_BASE = "i18n.messages";

    private static Locale localeActual = localeParaCodigo(LANG_ES);
    private static ResourceBundle bundle = cargarBundle(localeActual);

    private I18nManager() {
    }

    public static void setIdioma(String codigo) {
        localeActual = localeParaCodigo(codigo);
        bundle = cargarBundle(localeActual);
    }

    private static Locale localeParaCodigo(String codigo) {
        if (LANG_EN.equals(codigo)) {
            return Locale.ENGLISH;
        }
        if (LANG_PT.equals(codigo)) {
            return Locale.forLanguageTag("pt-BR");
        }
        return Locale.forLanguageTag("es-ES");
    }

    public static Locale getLocale() {
        return localeActual;
    }

    public static String get(String clave) {
        try {
            return bundle.getString(clave);
        } catch (MissingResourceException ex) {
            return "!" + clave + "!";
        }
    }

    public static DateFormat formatoFecha() {
        return new SimpleDateFormat(get("date.pattern"), localeActual);
    }

    private static ResourceBundle cargarBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_BASE, locale);
    }
}
