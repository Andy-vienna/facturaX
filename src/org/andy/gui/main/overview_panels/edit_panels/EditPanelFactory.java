package org.andy.gui.main.overview_panels.edit_panels;

import javax.swing.JLabel;

import org.andy.gui.main.overview_panels.EditPanel;

public class EditPanelFactory {
    public static EditPanel create(String sTyp) {
        switch (sTyp) {
            //case "AN":  return new ExpensesPanel();
            //case "REa": return new ExpensesPanel();
            //case "REe": return new ExpensesPanel();
            case "EX":  return new ExpensesPanel();
            //case "SVT": return new ExpensesPanel();
            default:    return new EditPanel("Unbekannt") {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override public void initContent() {
                    this.add(new JLabel("Kein Inhalt verfügbar."));
                }
            };
        }
    }
}
