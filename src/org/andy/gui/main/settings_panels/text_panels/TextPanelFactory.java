package org.andy.gui.main.settings_panels.text_panels;

import javax.swing.JLabel;

import org.andy.gui.main.settings_panels.text_panels.factory.TextAngebot;
import org.andy.gui.main.settings_panels.text_panels.factory.TextAngebotRev;
import org.andy.gui.main.settings_panels.text_panels.factory.TextBestaetigung;
import org.andy.gui.main.settings_panels.text_panels.factory.TextBestellung;
import org.andy.gui.main.settings_panels.text_panels.factory.TextLieferschein;
import org.andy.gui.main.settings_panels.text_panels.factory.TextMahnungStufe1;
import org.andy.gui.main.settings_panels.text_panels.factory.TextMahnungStufe2;
import org.andy.gui.main.settings_panels.text_panels.factory.TextRechnung;
import org.andy.gui.main.settings_panels.text_panels.factory.TextZahlErin;

public class TextPanelFactory {
	
    public static TextPanel create(String sTyp) {
        switch (sTyp) {
            case "AnT": return new TextAngebot();
            case "AnTR": return new TextAngebotRev();
            case "AbT": return new TextBestaetigung();
            case "ReT": return new TextRechnung();
            case "ZeT": return new TextZahlErin();
            case "Ma1T": return new TextMahnungStufe1();
            case "Ma2T": return new TextMahnungStufe2();
            case "BeT": return new TextBestellung();
            case "LsT": return new TextLieferschein();
            default:    return new TextPanel("Unbekannt") {
            	private static final long serialVersionUID = 1L;
				@Override public void initContent() {
                    this.add(new JLabel("Kein Inhalt verfügbar."));
                }
            };
        }
    }
}
