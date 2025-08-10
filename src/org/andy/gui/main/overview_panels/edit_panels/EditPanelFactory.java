package org.andy.gui.main.overview_panels.edit_panels;

import javax.swing.JLabel;

import org.andy.gui.main.overview_panels.edit_panels.factory.BillCreatePanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.BillPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.OfferCreatePanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.ExpensesPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.OfferPanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.PurchasePanel;
import org.andy.gui.main.overview_panels.edit_panels.factory.SvTaxPanel;

public class EditPanelFactory {
	
    public static EditPanel create(String sTyp) {
        switch (sTyp) {
            case "AN":  return new OfferPanel();
            case "NA":  return new OfferCreatePanel();
            case "RE":  return new BillPanel();
            case "NR":  return new BillCreatePanel();
            case "PU":  return new PurchasePanel();
            case "EX":  return new ExpensesPanel();
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
