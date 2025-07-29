package omxium.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private static final String BUNDLE_BASE = "i18n.messages";

    private static ResourceBundle bundle = ResourceBundle.getBundle(
            BUNDLE_BASE, Locale.getDefault());

    public static String t(String key) {
        return bundle.getString(key);
    }

    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(BUNDLE_BASE, locale);
    }
}