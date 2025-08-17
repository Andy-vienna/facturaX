package org.andy.gui.main.overview_panels.edit_panels;

import javax.swing.JLabel;

import org.andy.gui.main.overview_panels.edit_panels.factory.RechnungNeuPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.RechnungPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.AngebotNeuPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.AusgabenPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.AngebotPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.EinkaufPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.SvTaxPanel;

public class EditPanelFactory {
	
    public static EditPanel create(String sTyp) {
        switch (sTyp) {
            case "AN":  return new AngebotPanel();
            case "NA":  return new AngebotNeuPanel();
            case "RE":  return new RechnungPanel();
            case "NR":  return new RechnungNeuPanel();
            case "PU":  return new EinkaufPanel();
            case "EX":  return new AusgabenPanel();
            case "SVT": return new SvTaxPanel();
            default:    return new EditPanel("Unbekannt") {
            	private static final long serialVersionUID = 1L;
				@Override public void initContent() {
                    this.add(new JLabel("Kein Inhalt verfügbar."));
                }
            };
        }
    }
}
