package com.afogleson.ui;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class DisplayKey {

    private static ResourceBundle bundle = ResourceBundle.getBundle("M3uHandler");

    private DisplayKey() {}

    public static String get(String key, Object... params) {
        try {
            String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, params);
        }
        catch (Throwable t) {
            return key;
        }
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        }
        catch (Throwable t) {
            return key;
        }
    }
}
