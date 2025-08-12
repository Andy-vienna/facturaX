package org.andy.gui.main.settings_panels.text_panels;

import javax.swing.JLabel;

import org.andy.gui.main.settings_panels.text_panels.factory.TextAngebot;
import org.andy.gui.main.settings_panels.text_panels.factory.TextBestaetigung;
import org.andy.gui.main.settings_panels.text_panels.factory.TextMahnung;
import org.andy.gui.main.settings_panels.text_panels.factory.TextUSt;
import org.andy.gui.main.settings_panels.text_panels.factory.TextZahlErin;
import org.andy.gui.main.settings_panels.text_panels.factory.TextZahlZiel;

public class TextPanelFactory {
	
    public static TextPanel create(String sTyp) {
        switch (sTyp) {
            case "AnT":  return new TextAngebot();
            case "AbT":  return new TextBestaetigung();
            case "ReT":  return new TextUSt();
            case "ZzT":  return new TextZahlZiel();
            case "ZeT":  return new TextZahlErin();
            case "MaT":  return new TextMahnung();
            default:    return new TextPanel("Unbekannt") {
            	private static final long serialVersionUID = 1L;
				@Override public void initContent() {
                    this.add(new JLabel("Kein Inhalt verfügbar."));
                }
            };
        }
    }
}
