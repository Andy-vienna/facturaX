package org.andy.gui.main.overview_panels.edit_panels;

import javax.swing.JLabel;

public class EditPanelFactory {
	
    public static EditPanel create(String sTyp) {
        switch (sTyp) {
            case "AN":  return new OfferPanel();
            //case "REa": return new ExpensesPanel();
            case "PU": return new PurchasePanel();
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
