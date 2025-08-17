package org.andy.gui.main.settings_panels;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.code.misc.ArithmeticHelper.parseStringToIntSafe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;

import org.andy.code.dataStructure.entitiyMaster.Gwb;
import org.andy.code.dataStructure.repositoryMaster.GwbRepository;
import org.andy.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.gui.main.HauptFenster;
import org.andy.gui.misc.CommaHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GwbTabellePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(GwbTabellePanel.class);
    
    private GwbRepository gwbRepository = new GwbRepository();
    private List<Gwb> gwbListe = new ArrayList<>();
    private Gwb leer = new Gwb();
	
	private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
    private JComboBox<String> cmbSelect;
    private JTextField txtJahr = new JTextField();
    private final JTextField[] txtFields = new JTextField[8];
    private final JButton[] btnFields = new JButton[2];
	
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public GwbTabellePanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Gewinnfreibetragsgrenzen");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);

        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	int x = 10, y = 20; // Variablen für automatische Positionierung
    	int btnWidth = HauptFenster.getButtonx();
    	int btnHeight = HauptFenster.getButtony();
    	
    	JLabel lblJahr = new JLabel("Jahr");
    	lblJahr.setBounds(x, y, 120, 25);
    	lblJahr.setFont(font);
    	add(lblJahr);
    	x = lblJahr.getY() + lblJahr.getWidth();
    	y = 20;
    	
    	gwbListe.clear();
        gwbListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        gwbListe.addAll(gwbRepository.findAll());
        String[] gwbYear = gwbListe.stream()
        	    .map(t -> t.getYear() == 0 ? "" : String.valueOf(t.getYear()))
        	    .toArray(String[]::new);
        cmbSelect = new JComboBox<>(gwbYear);
        cmbSelect.setBounds(x, y, 140, 25);
        cmbSelect.addActionListener(cmbListener);
        add(cmbSelect);
        x = 10;
        y = cmbSelect.getY() + cmbSelect.getHeight();
        
        txtJahr = makeField(310, 20, 130, 25, true, null);
        txtJahr.setVisible(false);
        add(txtJahr);
        
        JLabel lblGwbTabelle = new JLabel("Gewinnfreibetragstabelle");
        lblGwbTabelle.setBounds(x, y, 200, 25);
        lblGwbTabelle.setFont(font);
    	add(lblGwbTabelle);
    	x = 10;
    	y = lblGwbTabelle.getY() + lblGwbTabelle.getHeight();
    	
        String[] labels = { "bis / weitere", "Freibetrag" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x + i * 100, y, 100, 25);
            lblFields[i].setHorizontalAlignment(SwingConstants.CENTER);
            add(lblFields[i]);
        }
        x = 10;
        y = lblFields[lblFields.length - 1].getY() + lblFields[lblFields.length - 1].getHeight();
        
        for (int i = 0; i < txtFields.length; i++) {
        	if (i < 2) {
        		txtFields[i] = makeField(x + i * 100, y, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 1 && i < 4) {
        		txtFields[i] = makeField(x + (i - 2) * 100, y + 25, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 3 && i < 6) {
        		txtFields[i] = makeField(x + (i - 4) * 100, y + 50, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 5) {
        		txtFields[i] = makeField(x + (i - 6) * 100, y + 75, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	attachCommaToDot(txtFields[i]);
            
        }
        x = txtFields[txtFields.length - 1].getX() + txtFields[txtFields.length - 1].getWidth() + 100;
        y = txtFields[5].getY();
        
        try {
			btnFields[0] = createButton("<html>Jahr anlegen</html>", null);
			btnFields[1] = createButton("<html>Tabelle<br>updaten</html>", "update.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

        btnFields[0].setEnabled(true);
        btnFields[0].setVisible(false);
        btnFields[1].setEnabled(false);
		btnFields[0].setBounds(x, 45, btnWidth, btnHeight);
		btnFields[1].setBounds(x, y, btnWidth, btnHeight);
		btnFields[0].addActionListener(btn0Listener);
		btnFields[1].addActionListener(btn1Listener);
		add(btnFields[0]);
		add(btnFields[1]);
        
        x = btnFields[btnFields.length - 1].getX() + btnFields[btnFields.length - 1].getWidth() + 10;
        y = txtFields[txtFields.length - 1].getY() + txtFields[txtFields.length - 1].getHeight() + 20;

        setPreferredSize(new Dimension(x, y));
        
        noFocusFields(txtFields);
    }
        
        
    //###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private final ActionListener cmbListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbSelect.getSelectedIndex();
            Gwb gwb = gwbListe.get(idx);

            if (idx == 0) {
                txtJahr.setVisible(true);
                btnFields[0].setVisible(true);
                btnFields[1].setEnabled(false);
                clearFields(txtFields);
                noFocusFields(txtFields);
            } else {
            	txtJahr.setVisible(false);
                btnFields[0].setVisible(false);
                btnFields[1].setEnabled(true);
                txtFields[0].setText(gwb.getBis_1().toString());
                txtFields[1].setText(gwb.getVal_1().toString());
                txtFields[2].setText(gwb.getWeitere_2().toString());
                txtFields[3].setText(gwb.getVal_2().toString());
                txtFields[4].setText(gwb.getWeitere_3().toString());
                txtFields[5].setText(gwb.getVal_3().toString());
                txtFields[6].setText(gwb.getWeitere_4().toString());
                txtFields[7].setText(gwb.getVal_4().toString());
                focusFields(txtFields);
            }
        }
    };
    
    private final ActionListener btn0Listener = new ActionListener() { // neues Jahr Button
    	@Override
        public void actionPerformed(ActionEvent actionEvent) {
    		fillFields(txtFields);
    		Gwb gwb = new Gwb();
    		gwb.setYear(parseStringToIntSafe(txtJahr.getText()));
    		gwb.setBis_1(parseStringToBigDecimalSafe(txtFields[0].getText(), LocaleFormat.AUTO));
    		gwb.setVal_1(parseStringToBigDecimalSafe(txtFields[1].getText(), LocaleFormat.AUTO));
    		gwb.setWeitere_2(parseStringToBigDecimalSafe(txtFields[2].getText(), LocaleFormat.AUTO));
    		gwb.setVal_2(parseStringToBigDecimalSafe(txtFields[3].getText(), LocaleFormat.AUTO));
    		gwb.setWeitere_3(parseStringToBigDecimalSafe(txtFields[4].getText(), LocaleFormat.AUTO));
    		gwb.setVal_3(parseStringToBigDecimalSafe(txtFields[5].getText(), LocaleFormat.AUTO));
    		gwb.setWeitere_4(parseStringToBigDecimalSafe(txtFields[6].getText(), LocaleFormat.AUTO));
    		gwb.setVal_4(parseStringToBigDecimalSafe(txtFields[7].getText(), LocaleFormat.AUTO));
    		
    		gwbRepository.insert(gwb);
    		rebuild();
    	}
    };
    
    private final ActionListener btn1Listener = new ActionListener() { // Update-Button
    	@Override
        public void actionPerformed(ActionEvent actionEvent) {
    		Gwb gwb = new Gwb();
    		gwb.setYear(parseStringToIntSafe((String) cmbSelect.getSelectedItem()));
    		gwb.setBis_1(parseStringToBigDecimalSafe(txtFields[0].getText(), LocaleFormat.AUTO));
    		gwb.setVal_1(parseStringToBigDecimalSafe(txtFields[1].getText(), LocaleFormat.AUTO));
    		gwb.setWeitere_2(parseStringToBigDecimalSafe(txtFields[2].getText(), LocaleFormat.AUTO));
    		gwb.setVal_2(parseStringToBigDecimalSafe(txtFields[3].getText(), LocaleFormat.AUTO));
    		gwb.setWeitere_3(parseStringToBigDecimalSafe(txtFields[4].getText(), LocaleFormat.AUTO));
    		gwb.setVal_3(parseStringToBigDecimalSafe(txtFields[5].getText(), LocaleFormat.AUTO));
    		gwb.setWeitere_4(parseStringToBigDecimalSafe(txtFields[6].getText(), LocaleFormat.AUTO));
    		gwb.setVal_4(parseStringToBigDecimalSafe(txtFields[7].getText(), LocaleFormat.AUTO));
    		
    		gwbRepository.update(gwb);
    		rebuild();
    	}
    };
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
    	remove(cmbSelect);
    	gwbListe.clear();
    	gwbListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
    	gwbListe.addAll(gwbRepository.findAll());
        String[] taxYear = gwbListe.stream()
        	    .map(t -> t.getYear() == 0 ? "" : String.valueOf(t.getYear()))
        	    .toArray(String[]::new);
        cmbSelect = new JComboBox<>(taxYear);
        cmbSelect.setBounds(130, 20, 140, 25);
        cmbSelect.addActionListener(cmbListener);
        add(cmbSelect);
        txtJahr.setVisible(false);
        btnFields[0].setVisible(false);
        btnFields[1].setEnabled(false);
        clearFields(txtFields);
        noFocusFields(txtFields);
        revalidate();
        repaint();
    }
    
    //###################################################################################################################################################

    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(true);
        if (bold) t.setFont(font);
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
        
    //###################################################################################################################################################

    private void clearFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("");
    }
    
    //###################################################################################################################################################

    private void fillFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("0.00");
    }
    
    //###################################################################################################################################################

    private void noFocusFields(JTextField[] fields) {
        for (JTextField f : fields) f.setFocusable(false);
    }
    
    //###################################################################################################################################################

    private void focusFields(JTextField[] fields) {
        for (JTextField f : fields) f.setFocusable(true);
    }

}
