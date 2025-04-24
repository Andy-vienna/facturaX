package org.andy.gui.settings;

import static main.java.toolbox.misc.CreateObject.addBorderFocusListener;
import static main.java.toolbox.misc.CreateObject.createButton;
import static main.java.toolbox.sql.Delete.sqlDeleteNoReturn;
import static main.java.toolbox.sql.Insert.sqlInsert;
import static main.java.toolbox.sql.Read.sqlReadArray;
import static main.java.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.sql.SQLmasterData;
import org.andy.org.eclipse.wb.swing.FocusTraversalOnArray;
import main.java.toolbox.misc.SetFrameIcon;

public class JFkunde extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFkunde.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static String sConn;
	private static String[][] arrKunde = new String[100][3];
	private static ArrayList<String> KDdata = new ArrayList<>();

	private static int AnzKunde;
	private JTextField textKdNr, textKdName, textKdStrasse, textKdPLZ, textKdOrt, textKdLand, textKdDuty, textKdUID, textKdUSt, textKdRabatt,
	textKdZahlZiel, textLeitwegID, textMail, textPhone;
	private JButton btnDoInsert = null, btnDoUpdate = null, btnDoDelete = null, btnCancel = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {

		KDdata.clear();
		Arrays.fill(arrKunde, null);

		try {

			String sSQLStatement = "SELECT * from tblKunde ORDER BY [Id]"; //SQL Befehlszeile";

			arrKunde = sqlReadArray(sConn, sSQLStatement);

			if(arrKunde[0][0] != null) {
				AnzKunde = Integer.parseInt(arrKunde[0][0]);
			}else {
				AnzKunde = 0;
			}

		} catch (SQLException | NullPointerException | ClassNotFoundException e1) {
			logger.error("error reading customer data from database - " + e1);
		}

		try {
			JFkunde frame = new JFkunde();
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("fatal error loading gui for editing data - " + e);
		}
	}

	public JFkunde() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("config.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Kundendaten bearbeiten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 785, 400);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);

		//------------------------------------------------------------------------------
		// WindowListener für den Dialog aufschlagen
		//------------------------------------------------------------------------------
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				try {
					SQLmasterData.loadBaseData();
				} catch (ClassNotFoundException | SQLException | ParseException e1) {
					logger.error("error loading base data - " + e1);
				}
			}
		});

		//------------------------------------------------------------------------------
		// ComboBox cmbKdSelect
		//------------------------------------------------------------------------------
		KDdata.add(" ");
		for (int x = 1; (x-1) < AnzKunde; x++)
		{
			KDdata.add(arrKunde[x][2]);
		}
		JComboBox<String> cmbKdSelect = new JComboBox<>(KDdata.toArray(new String[0]));
		cmbKdSelect.setBounds(10, 10, 750, 30);
		contentPane.add(cmbKdSelect);

		//------------------------------------------------------------------------------
		// ComboBox cmbPronom
		//------------------------------------------------------------------------------
		ArrayList<String> KDpronom = new ArrayList<>();
		KDpronom.add(" ");
		KDpronom.add("Herr");
		KDpronom.add("Frau");
		KDpronom.add("Divers");
		JComboBox<String> cmbPronom = new JComboBox<>(KDpronom.toArray(new String[0]));
		cmbPronom.setBounds(130, 230, 220, 30);
		contentPane.add(cmbPronom);

		//------------------------------------------------------------------------------
		// ComboBox cmbBill
		//------------------------------------------------------------------------------
		ArrayList<String> KDbillType = new ArrayList<>();
		KDbillType.add(" ");
		KDbillType.add("ZUGFeRD");
		KDbillType.add("XRechnung");
		JComboBox<String> cmbBill = new JComboBox<>(KDbillType.toArray(new String[0]));
		cmbBill.setBounds(539, 170, 220, 30);
		contentPane.add(cmbBill);

		//------------------------------------------------------------------------------
		// Buttons anlegen
		//------------------------------------------------------------------------------
		try {
			btnDoInsert = createButton("<html>Kunde<br>anlegen</html>", "new.png");
			btnDoUpdate = createButton("<html>Kunde<br>updaten</html>", "update.png");
			btnDoDelete = createButton("<html>Kunde<br>loeschen</html>", "delete.png");
			btnCancel = createButton("cancel", "exit.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnDoInsert.setEnabled(true);
		btnCancel.setEnabled(true);
		btnDoInsert.setBounds(10, 300, 130, 50);
		btnDoUpdate.setBounds(150, 300, 130, 50);
		btnDoDelete.setBounds(290, 300, 130, 50);
		btnCancel.setBounds(630, 300, 130, 50);

		contentPane.add(btnDoInsert);
		contentPane.add(btnDoUpdate);
		contentPane.add(btnDoDelete);
		contentPane.add(btnCancel);

		//------------------------------------------------------------------------------
		// Action Listeners
		//------------------------------------------------------------------------------
		btnDoInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Prüfung der Eingaben
				//------------------------------------------------------------------------------
				if(textKdNr.getText().isEmpty() || textKdName.getText().isEmpty() || textKdStrasse.getText().isEmpty() | textKdPLZ.getText().isEmpty() || textKdOrt.getText().isEmpty()
						|| textKdLand.getText().isEmpty() || textKdDuty.getText().isEmpty()){
					JOptionPane.showMessageDialog(rootPane, "Daten unvollständig ...","Eingabefehler", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(textKdUID.getText().isEmpty() || textKdUSt.getText().isEmpty() || textKdRabatt.getText().isEmpty() | textKdZahlZiel.getText().isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Daten unvollständig ...","Eingabefehler", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				//Prüfung fertig, in Datenbank schreiben
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[16];
				tmpArray[0] = textKdNr.getText().toUpperCase();
				tmpArray[1] = textKdName.getText();
				tmpArray[2] = textKdStrasse.getText();
				tmpArray[3] = textKdPLZ.getText();
				tmpArray[4] = textKdOrt.getText();
				tmpArray[5] = textKdLand.getText().toUpperCase();
				tmpArray[6] = cmbPronom.getSelectedItem().toString();
				tmpArray[7] = textKdDuty.getText();
				tmpArray[8] = textKdUID.getText().toUpperCase();
				tmpArray[9] = textKdUSt.getText();
				tmpArray[10] = textKdRabatt.getText();
				tmpArray[11] = textKdZahlZiel.getText();
				tmpArray[12] = textLeitwegID.getText();
				tmpArray[13] = cmbBill.getSelectedItem().toString();
				tmpArray[14] = textMail.getText();
				tmpArray[15] = textPhone.getText();
				try {

					String sSQLStatement = "INSERT INTO [tblKunde] VALUES ('" + tmpArray[0] + "','" + tmpArray[1] + "','" + tmpArray[2]
							+ "','" + tmpArray[3] + "','" + tmpArray[4] + "','" + tmpArray[5] + "','" + tmpArray[6] + "','" + tmpArray[7]
									+ "','" + tmpArray[8] + "','" + tmpArray[9] + "','" + tmpArray[10] + "','" + tmpArray[11] + "','" + tmpArray[12]
											+ "','" + tmpArray[13] + "','" + tmpArray[14] + "','" + tmpArray[15] + "')"; //SQL Befehlszeile

					bResult = sqlInsert(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error inserting new customer data into database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "Neukunde schreiben OK","Bestätigung", JOptionPane.INFORMATION_MESSAGE);
					textKdNr.setText("");
					textKdName.setText("");
					textKdStrasse.setText("");
					textKdPLZ.setText("");
					textKdOrt.setText("");
					textKdLand.setText("");
					cmbPronom.setSelectedIndex(0);
					textKdDuty.setText("");
					textKdUID.setText("");
					textKdUSt.setText("");
					textKdRabatt.setText("");
					textKdZahlZiel.setText("");
					cmbBill.setSelectedIndex(0);
					textLeitwegID.setText("");
					textMail.setText("");
					textPhone.setText("");
				}else {
					JOptionPane.showMessageDialog(rootPane, "Neukunde schreiben Fehler !","Bestätigung", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dispose();
			}
		});
		btnDoUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// aufbereiten und Kunde in DB aktualisieren
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[16];
				tmpArray[0] = textKdNr.getText().toUpperCase();
				tmpArray[1] = textKdName.getText();
				tmpArray[2] = textKdStrasse.getText();
				tmpArray[3] = textKdPLZ.getText();
				tmpArray[4] = textKdOrt.getText();
				tmpArray[5] = textKdLand.getText().toUpperCase();
				tmpArray[6] = cmbPronom.getSelectedItem().toString();
				tmpArray[7] = textKdDuty.getText();
				tmpArray[8] = textKdUID.getText().toUpperCase();
				tmpArray[9] = textKdUSt.getText();
				tmpArray[10] = textKdRabatt.getText();
				tmpArray[11] = textKdZahlZiel.getText();
				tmpArray[12] = textLeitwegID.getText();
				tmpArray[13] = cmbBill.getSelectedItem().toString();
				tmpArray[14] = textMail.getText();
				tmpArray[15] = textPhone.getText();
				try {

					String sSQLStatement = "UPDATE tblKunde SET [Name] = '" + tmpArray[1] + "', [Strasse] = '" + tmpArray[2] + "', [PLZ] = '"
							+ tmpArray[3] + "', [Ort] = '" + tmpArray[4] + "', [Land] = '" + tmpArray[5] + "', [Pronomen] = '" + tmpArray[6]+ "', [Ansprechpartner] = '"
							+ tmpArray[7] + "', [UID] = '" + tmpArray[8] + "', [Steuersatz] = '" + tmpArray[9] + "', [Rabattschluessel] = '" + tmpArray[10] + "', [Zahlungsziel] = '"
							+ tmpArray[11] + "', [eBillLeitwegId] = '" + tmpArray[12] + "', [eBillTyp] = '" + tmpArray[13] + "', [eBillMail] = '" + tmpArray[14] + "', [eBillPhone] = '"
							+ tmpArray[15] + "' WHERE [Id] = '" + tmpArray[0] + "'";

					bResult = sqlUpdate(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error updating customer data in database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "updaten erolgreich", "Daten update", JOptionPane.INFORMATION_MESSAGE);
					textKdNr.setText("");
					textKdName.setText("");
					textKdStrasse.setText("");
					textKdPLZ.setText("");
					textKdOrt.setText("");
					textKdLand.setText("");
					cmbPronom.setSelectedIndex(0);
					textKdDuty.setText("");
					textKdUID.setText("");
					textKdUSt.setText("");
					textKdRabatt.setText("");
					textKdZahlZiel.setText("");
					cmbBill.setSelectedIndex(0);
					textLeitwegID.setText("");
					textMail.setText("");
					textPhone.setText("");
				}else {
					JOptionPane.showMessageDialog(rootPane, "updaten Fehler", "Daten update", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dispose();
			}
		});
		btnDoDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Datenprüfung
				//------------------------------------------------------------------------------
				if(textKdNr.getText().isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Eingabe fehlerhaft", "Daten loeschen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				// aufbereiten und Artikel in DB löschen
				//------------------------------------------------------------------------------
				String sKdNum = textKdNr.getText();
				boolean bResult = false;
				try {

					String sSQLStatement = "DELETE FROM tblKunde WHERE [Id] = '" + sKdNum + "'";

					sqlDeleteNoReturn(sConn, sSQLStatement);
					bResult = true;

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error deleting customer data from database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "loeschen erfolgreich", "Daten loeschen", JOptionPane.INFORMATION_MESSAGE);
					textKdNr.setText("");
					textKdName.setText("");
					textKdStrasse.setText("");
					textKdPLZ.setText("");
					textKdOrt.setText("");
					textKdLand.setText("");
					cmbPronom.setSelectedIndex(0);
					textKdDuty.setText("");
					textKdUID.setText("");
					textKdUSt.setText("");
					textKdRabatt.setText("");
					textKdZahlZiel.setText("");
					cmbBill.setSelectedIndex(0);
					textLeitwegID.setText("");
					textMail.setText("");
					textPhone.setText("");
				}else {
					JOptionPane.showMessageDialog(rootPane, "loeschen Fehler", "Daten loeschen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dispose();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if(cmbKdSelect.getSelectedIndex() == 0) {
					btnDoInsert.setEnabled(true);
					btnDoUpdate.setEnabled(false);
					btnDoDelete.setEnabled(false);
					textKdNr.setEditable(true);
					textKdNr.setText("");
					textKdNr.setBackground(Color.PINK);
					textKdName.setText("");
					textKdName.setBackground(Color.PINK);
					textKdStrasse.setText("");
					textKdStrasse.setBackground(Color.PINK);
					textKdPLZ.setText("");
					textKdPLZ.setBackground(Color.PINK);
					textKdOrt.setText("");
					textKdOrt.setBackground(Color.PINK);
					textKdLand.setText("");
					textKdLand.setBackground(Color.PINK);
					cmbPronom.setSelectedIndex(0);
					textKdDuty.setText("");
					textKdDuty.setBackground(Color.PINK);
					textKdUID.setText("");
					textKdUID.setBackground(Color.PINK);
					textKdUSt.setText("");
					textKdUSt.setBackground(Color.PINK);
					textKdRabatt.setText("");
					textKdRabatt.setBackground(Color.PINK);
					textKdZahlZiel.setText("");
					textKdZahlZiel.setBackground(Color.PINK);
					cmbBill.setSelectedIndex(0);
					textLeitwegID.setText("");
					textLeitwegID.setBackground(Color.PINK);
					textMail.setText("");
					textMail.setBackground(Color.PINK);
					textPhone.setText("");
					textPhone.setBackground(Color.PINK);
				}else {
					btnDoInsert.setEnabled(false);
					btnDoUpdate.setEnabled(true);
					btnDoDelete.setEnabled(true);
					textKdNr.setEditable(false);
					textKdNr.setText(arrKunde[cmbKdSelect.getSelectedIndex()][1]);
					textKdNr.setBackground(Color.WHITE);
					textKdName.setText(arrKunde[cmbKdSelect.getSelectedIndex()][2]);
					textKdName.setBackground(Color.WHITE);
					textKdStrasse.setText(arrKunde[cmbKdSelect.getSelectedIndex()][3]);
					textKdStrasse.setBackground(Color.WHITE);
					textKdPLZ.setText(arrKunde[cmbKdSelect.getSelectedIndex()][4]);
					textKdPLZ.setBackground(Color.WHITE);
					textKdOrt.setText(arrKunde[cmbKdSelect.getSelectedIndex()][5]);
					textKdOrt.setBackground(Color.WHITE);
					textKdLand.setText(arrKunde[cmbKdSelect.getSelectedIndex()][6]);
					textKdLand.setBackground(Color.WHITE);
					switch(arrKunde[cmbKdSelect.getSelectedIndex()][7]) {
					case "Herr":
						cmbPronom.setSelectedIndex(1);
						break;
					case "Frau":
						cmbPronom.setSelectedIndex(2);
						break;
					case "Divers":
						cmbPronom.setSelectedIndex(3);
						break;
					default:
						break;
					}
					textKdDuty.setText(arrKunde[cmbKdSelect.getSelectedIndex()][8]);
					textKdDuty.setBackground(Color.WHITE);
					textKdUID.setText(arrKunde[cmbKdSelect.getSelectedIndex()][9]);
					textKdUID.setBackground(Color.WHITE);
					textKdUSt.setText(arrKunde[cmbKdSelect.getSelectedIndex()][10]);
					textKdUSt.setBackground(Color.WHITE);
					textKdRabatt.setText(arrKunde[cmbKdSelect.getSelectedIndex()][11]);
					textKdRabatt.setBackground(Color.WHITE);
					textKdZahlZiel.setText(arrKunde[cmbKdSelect.getSelectedIndex()][12]);
					textKdZahlZiel.setBackground(Color.WHITE);
					textLeitwegID.setText(arrKunde[cmbKdSelect.getSelectedIndex()][13]);
					textLeitwegID.setBackground(Color.WHITE);
					switch(arrKunde[cmbKdSelect.getSelectedIndex()][14]) {
					case "ZUGFeRD":
						cmbBill.setSelectedIndex(1);
						break;
					case "XRechnung":
						cmbBill.setSelectedIndex(2);
						break;
					default:
						break;
					}
					textMail.setText(arrKunde[cmbKdSelect.getSelectedIndex()][15]);
					textMail.setBackground(Color.WHITE);
					textPhone.setText(arrKunde[cmbKdSelect.getSelectedIndex()][16]);
					textPhone.setBackground(Color.WHITE);
				}
			}
		};
		cmbKdSelect.addActionListener(actionListener);

		JLabel lblNewLabel_1 = new JLabel("Kundennummer");
		lblNewLabel_1.setBounds(10, 50, 115, 30);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Kundenname");
		lblNewLabel_2.setBounds(10, 80, 110, 30);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Straße");
		lblNewLabel_3.setBounds(10, 110, 110, 30);
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("PLZ");
		lblNewLabel_4.setBounds(10, 140, 110, 30);
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("Ort");
		lblNewLabel_5.setBounds(10, 170, 110, 30);
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_5);

		JLabel lblNewLabel_6 = new JLabel("Land");
		lblNewLabel_6.setBounds(10, 200, 110, 30);
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_6);

		JLabel lblNewLabel_7 = new JLabel("Anrede");
		lblNewLabel_7.setBounds(10, 230, 110, 30);
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_7);

		JLabel lblNewLabel_8 = new JLabel("Ansprechpartner");
		lblNewLabel_8.setBounds(10, 260, 110, 30);
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_8.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lblNewLabel_8);

		textKdNr = new JTextField();
		textKdNr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textKdNr.setText(textKdNr.getText().toUpperCase());
			}
		});
		textKdNr.setBounds(130, 50, 220, 30);
		textKdNr.setFont(new Font("Tahoma", Font.BOLD, 14));
		textKdNr.setBackground(Color.PINK);
		contentPane.add(textKdNr);
		textKdNr.setColumns(40);
		addBorderFocusListener(textKdNr);

		textKdName = new JTextField();
		textKdName.setBounds(130, 80, 220, 30);
		textKdName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdName.setBackground(Color.PINK);
		textKdName.setColumns(40);
		contentPane.add(textKdName);
		addBorderFocusListener(textKdName);

		textKdStrasse = new JTextField();
		textKdStrasse.setBounds(130, 110, 220, 30);
		textKdStrasse.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdStrasse.setBackground(Color.PINK);
		textKdStrasse.setColumns(40);
		contentPane.add(textKdStrasse);
		addBorderFocusListener(textKdStrasse);

		textKdPLZ = new JTextField();
		textKdPLZ.setBounds(130, 140, 220, 30);
		textKdPLZ.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdPLZ.setBackground(Color.PINK);
		textKdPLZ.setColumns(40);
		contentPane.add(textKdPLZ);
		addBorderFocusListener(textKdPLZ);

		textKdOrt = new JTextField();
		textKdOrt.setBounds(130, 170, 220, 30);
		textKdOrt.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdOrt.setBackground(Color.PINK);
		textKdOrt.setColumns(40);
		contentPane.add(textKdOrt);
		addBorderFocusListener(textKdOrt);

		textKdLand = new JTextField();
		textKdLand.setBounds(130, 200, 220, 30);
		textKdLand.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdLand.setBackground(Color.PINK);
		textKdLand.setColumns(40);
		contentPane.add(textKdLand);
		addBorderFocusListener(textKdLand);

		textKdDuty = new JTextField();
		textKdDuty.setBounds(130, 260, 220, 30);
		textKdDuty.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdDuty.setBackground(Color.PINK);
		textKdDuty.setColumns(40);
		contentPane.add(textKdDuty);
		addBorderFocusListener(textKdDuty);

		//------------------------------------------------------------------------------
		//rechte Spalte
		//------------------------------------------------------------------------------
		JLabel lblNewLabel_11 = new JLabel("UID");
		lblNewLabel_11.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_11.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_11.setBounds(395, 50, 130, 30);
		contentPane.add(lblNewLabel_11);

		JLabel lblNewLabel_12 = new JLabel("USt.-Satz (%)");
		lblNewLabel_12.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_12.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_12.setBounds(395, 80, 130, 30);
		contentPane.add(lblNewLabel_12);

		JLabel lblNewLabel_13 = new JLabel("Rabatt (%)");
		lblNewLabel_13.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_13.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_13.setBounds(395, 110, 130, 30);
		contentPane.add(lblNewLabel_13);

		JLabel lblNewLabel_14 = new JLabel("Zahlungsziel (Tage)");
		lblNewLabel_14.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_14.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_14.setBounds(395, 140, 130, 30);
		contentPane.add(lblNewLabel_14);

		JLabel lblNewLabel_14_1 = new JLabel("eBilling Profil");
		lblNewLabel_14_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_14_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_14_1.setBounds(395, 170, 130, 30);
		contentPane.add(lblNewLabel_14_1);

		JLabel lblNewLabel_14_2 = new JLabel("Leitweg-ID");
		lblNewLabel_14_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_14_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_14_2.setBounds(395, 200, 130, 30);
		contentPane.add(lblNewLabel_14_2);

		JLabel lblNewLabel_14_3 = new JLabel("E-Mail Adresse");
		lblNewLabel_14_3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_14_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_14_3.setBounds(395, 230, 130, 30);
		contentPane.add(lblNewLabel_14_3);

		JLabel lblNewLabel_14_3_1 = new JLabel("Kontakt-Telefon");
		lblNewLabel_14_3_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_14_3_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_14_3_1.setBounds(395, 260, 130, 30);
		contentPane.add(lblNewLabel_14_3_1);

		textKdUID = new JTextField();
		textKdUID.setFont(new Font("Tahoma", Font.BOLD, 12));
		textKdUID.setBackground(Color.PINK);
		textKdUID.setColumns(40);
		textKdUID.setBounds(540, 50, 220, 30);
		contentPane.add(textKdUID);
		addBorderFocusListener(textKdUID);

		textKdUSt = new JTextField();
		textKdUSt.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdUSt.setBackground(Color.PINK);
		textKdUSt.setColumns(40);
		textKdUSt.setBounds(540, 80, 220, 30);
		contentPane.add(textKdUSt);
		addBorderFocusListener(textKdUSt);

		textKdRabatt = new JTextField();
		textKdRabatt.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdRabatt.setBackground(Color.PINK);
		textKdRabatt.setColumns(40);
		textKdRabatt.setBounds(540, 110, 220, 30);
		contentPane.add(textKdRabatt);
		addBorderFocusListener(textKdRabatt);

		textKdZahlZiel = new JTextField();
		textKdZahlZiel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKdZahlZiel.setBackground(Color.PINK);
		textKdZahlZiel.setColumns(40);
		textKdZahlZiel.setBounds(540, 140, 220, 30);
		contentPane.add(textKdZahlZiel);
		addBorderFocusListener(textKdZahlZiel);

		textLeitwegID = new JTextField();
		textLeitwegID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textLeitwegID.setColumns(40);
		textLeitwegID.setBackground(Color.PINK);
		textLeitwegID.setBounds(540, 200, 220, 30);
		contentPane.add(textLeitwegID);
		addBorderFocusListener(textLeitwegID);

		textMail = new JTextField();
		textMail.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textMail.setColumns(40);
		textMail.setBackground(Color.PINK);
		textMail.setBounds(540, 230, 220, 30);
		contentPane.add(textMail);
		addBorderFocusListener(textMail);

		textPhone = new JTextField();
		textPhone.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textPhone.setColumns(40);
		textPhone.setBackground(Color.PINK);
		textPhone.setBounds(540, 260, 220, 30);
		contentPane.add(textPhone);
		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textKdNr, textKdName, textKdStrasse, textKdPLZ, textKdOrt, textKdLand, cmbPronom, textKdDuty, textKdUID, textKdUSt, textKdRabatt, textKdZahlZiel, cmbBill, textLeitwegID, textMail, textPhone}));
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textKdNr, textKdName, textKdStrasse, textKdPLZ, textKdOrt, textKdLand, cmbPronom, textKdDuty, textKdUID, textKdUSt, textKdRabatt, textKdZahlZiel, lblNewLabel_14_1, lblNewLabel_14_2, lblNewLabel_14_3, textLeitwegID, cmbBill, textMail, lblNewLabel_14_3_1, textPhone}));

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFkunde.sConn = sConn;
	}

}