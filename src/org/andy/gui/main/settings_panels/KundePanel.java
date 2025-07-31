package org.andy.gui.main.settings_panels;

import static org.andy.toolbox.misc.CreateObject.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.andy.code.entity.Kunde;
import org.andy.code.entity.KundeRepository;
import org.andy.gui.main.JFoverview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KundePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(KundePanel.class);
    
    private KundeRepository kundeRepository = new KundeRepository();
    private List<Kunde> kundeListe = new ArrayList<>();
    private Kunde leer = new Kunde();

    private final JTextField[] txtFields = new JTextField[16];
    private final JButton[] btnFields = new JButton[3];
    private JComboBox<String> cmbSelect;
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    private final Consumer<JTextField[]>[] operations = createOperations();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public KundePanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Kundenverwaltung");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        leer.setId(""); leer.setName(""); leer.setStrasse(""); leer.setPlz(""); leer.setOrt(""); leer.setLand(""); leer.setPronomen(""); leer.setPerson("");
        leer.setUstid(""); leer.setTaxvalue(""); leer.setDeposit(""); leer.setZahlungsziel(""); leer.setLeitwegId(""); leer.seteBillTyp("");
        leer.seteBillMail(""); leer.seteBillPhone(""); // Leeren Listeneintrag erzeugen
        
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	int x = 10, y = 45; // Variablen für automatische Positionierung
    	int btnWidth = JFoverview.getButtonx();
    	int btnHeight = JFoverview.getButtony();
    	
        String[] labels = { "Kunden-Nr.", "Name", "Strasse", "PLZ", "Ort", "Land", "Pronomen", "Ansprechpartner",
        		"USt.-ID", "Steuersatz", "Rabattschlüssel", "Zahlungsziel", "Leitweg-ID", "eBill-Typ", "E-Mail", "Telefon" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        kundeListe.clear();
        kundeListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        kundeListe.addAll(kundeRepository.findAll());
        String[] kundeTexte = kundeListe.stream()
                .map(Kunde::getName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(kundeTexte);
        cmbSelect.setBounds(10, 20, 810, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);

        for (int i = 0; i < labels.length/2; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x, y + i * 25, 100, 25);
            add(lblFields[i]);
        }
        x = lblFields[labels.length/2 - 1].getX() + lblFields[labels.length/2 - 1].getWidth();

        for (int i = 0; i < txtFields.length/2; i++) {
            txtFields[i] = makeField(x, y + i * 25, 300, 25, false, null);
            add(txtFields[i]);
        }
        x = txtFields[txtFields.length/2 - 1].getX() + txtFields[txtFields.length/2 - 1].getWidth() + 10;
        
        for (int i = labels.length/2; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x, y + (i - labels.length/2) * 25, 100, 25);
            add(lblFields[i]);
        }
        x = lblFields[labels.length - 1].getX() + lblFields[labels.length - 1].getWidth();

        for (int i = txtFields.length/2; i < txtFields.length; i++) {
            txtFields[i] = makeField(x, y + (i - txtFields.length/2) * 25, 300, 25, false, null);
            add(txtFields[i]);
        }
        x = 10; y = y + ((txtFields.length/2 - 1) * 25);

        try {
            btnFields[0] = createButton("<html>Kunde<br>anlegen</html>", "new.png");
            btnFields[1] = createButton("<html>Kunde<br>updaten</html>", "update.png");
            btnFields[2] = createButton("<html>Kunde<br>loeschen</html>", "delete.png");
            for (int i = 0; i < btnFields.length; i++) {
                btnFields[i].setBounds(x + i * (btnWidth + 10), y + 30, btnWidth, btnHeight);
                add(btnFields[i]);
            }
            for (int i = 0; i < btnFields.length; i++) {
                final int index = i;
                btnFields[i].addActionListener(_ -> {
                    operations[index].accept(txtFields);
                    rebuild();
                });
            }
            btnFields[0].setEnabled(true);
        } catch (RuntimeException e1) {
            logger.error("error creating button - " + e1);
        }
        
        x = 10 + ((lblFields[labels.length - 1].getWidth() + txtFields[txtFields.length - 1].getWidth()) *2) + 20;
        y = btnFields[btnFields.length - 1].getY() + btnFields[btnFields.length - 1].getHeight() + 20;

        setPreferredSize(new Dimension(x, y));
    }

	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbSelect.getSelectedIndex();
            Kunde kunde = kundeListe.get(idx);

            if (idx == 0) {
                // Leereintrag: Felder leeren, Buttons sperren etc.
                btnFields[0].setEnabled(true);
                btnFields[1].setEnabled(false);
                btnFields[2].setEnabled(false);
                txtFields[0].setEditable(true);
                clearFields(txtFields);
            } else {
                btnFields[0].setEnabled(false);
                btnFields[1].setEnabled(true);
                btnFields[2].setEnabled(true);
                txtFields[0].setEditable(false);

                txtFields[0].setText(kunde.getId());
                txtFields[1].setText(kunde.getName());
                txtFields[2].setText(kunde.getStrasse());
                txtFields[3].setText(kunde.getPlz());
                txtFields[4].setText(kunde.getOrt());
                txtFields[5].setText(kunde.getLand());
                txtFields[6].setText(kunde.getPronomen());
                txtFields[7].setText(kunde.getPerson());
                txtFields[8].setText(kunde.getUstid());
                txtFields[9].setText(kunde.getTaxvalue());
                txtFields[10].setText(kunde.getDeposit());
                txtFields[11].setText(kunde.getZahlungsziel());
                txtFields[12].setText(kunde.getLeitwegId());
                txtFields[13].setText(kunde.geteBillTyp());
                txtFields[14].setText(kunde.geteBillMail());
                txtFields[15].setText(kunde.geteBillPhone());
            }
        }
    };

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
        remove(cmbSelect);
        kundeListe.clear();
        kundeListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        kundeListe.addAll(kundeRepository.findAll());
        String[] kundeTexte = kundeListe.stream()
                .map(Kunde::getName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(kundeTexte);
        cmbSelect.setBounds(10, 20, 750, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);
        btnFields[0].setEnabled(true);
        btnFields[1].setEnabled(false);
        btnFields[2].setEnabled(false);
        revalidate();
        repaint();
    }
    
  //###################################################################################################################################################

    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.LEFT);
        t.setFocusable(true);
        if (bold) t.setFont(font);
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
  //###################################################################################################################################################

    private Kunde fromFields(JTextField[] fields) {
        Kunde a = new Kunde();
        a.setId(fields[0].getText().trim());
        a.setName(fields[1].getText().trim());
        a.setStrasse(fields[2].getText().trim());
        a.setPlz(fields[3].getText().trim());
        a.setOrt(fields[4].getText().trim());
        a.setLand(fields[5].getText().trim());
        a.setPronomen(fields[6].getText().trim());
        a.setPerson(fields[7].getText().trim());
        a.setUstid(fields[8].getText().trim());
        a.setTaxvalue(fields[9].getText().trim());
        a.setDeposit(fields[10].getText().trim());
        a.setZahlungsziel(fields[11].getText().trim());
        a.setLeitwegId(fields[12].getText().trim());
        a.seteBillTyp(fields[13].getText().trim());
        a.seteBillMail(fields[14].getText().trim());
        a.seteBillPhone(fields[15].getText().trim());
        return a;
    }
    
  //###################################################################################################################################################

    private boolean isValid(Kunde a) {
        if (a.getId().isEmpty() || a.getName().isEmpty() || a.getStrasse().toString().isEmpty() || a.getPlz().isEmpty() || a.getOrt().isEmpty() ||
        	a.getLand().toString().isEmpty() || a.getPronomen().isEmpty() || a.getPerson().isEmpty() || a.getUstid().toString().isEmpty() ||
        	a.getTaxvalue().toString().isEmpty() || a.getDeposit().isEmpty() || a.getZahlungsziel().isEmpty() || a.getLeitwegId().toString().isEmpty() ||
        	a.geteBillTyp().toString().isEmpty() || a.geteBillMail().isEmpty() || a.geteBillPhone().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Alle Felder müssen befüllt sein", "Validierung", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
  //###################################################################################################################################################

    private void clearFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("");
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    @SuppressWarnings("unchecked")
    private Consumer<JTextField[]>[] createOperations() {
        Consumer<JTextField[]> insert = fields -> {
            Kunde a = fromFields(fields);
            if (!isValid(a)) return;
            kundeRepository.insert(a);
            clearFields(fields);
        };

        Consumer<JTextField[]> update = fields -> {
            Kunde a = fromFields(fields);
            if (!isValid(a)) return;
            kundeRepository.update(a);
            clearFields(fields);
        };

        Consumer<JTextField[]> delete = fields -> {
            String id = fields[0].getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID fehlt", "Löschen", JOptionPane.ERROR_MESSAGE);
                return;
            }
            kundeRepository.delete(id);
            clearFields(fields);
        };

        return new Consumer[] { insert, update, delete };
    }
}
