package org.andy.gui.iconHandler;

import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum ButtonIcon {
    ACT("act.png"),
    CALC("calc.png"),
    DEL("del.png"),
    DOWN("down.png"),
    EDIT("edit.png"),
    EXPORT("export.png"),
    GEMINI("gemini.png"),
    MAIL("mail.png"),
    NEW("new.png"),
    OK("ok.png"),
    PRINT("print.png"),
    REV("rev.png"),
    SAVE("save.png"),
    UP("up.png"),
    UPDATE("update.png");

	private static final String BASE = "/org/resources/icons/buttons/";
    private final ImageIcon icon;
    
    ButtonIcon(String file) {
        URL url = ButtonIcon.class.getResource(BASE + file);
        if (url == null) throw new IllegalStateException("Icon fehlt: " + BASE + file);
        this.icon = new ImageIcon(url);
    }

    public ImageIcon icon() { return icon; }

    public static Icon byKey(String key) {
        return valueOf(key.toUpperCase(Locale.ROOT)).icon();
    }
}

