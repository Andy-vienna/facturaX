package org.andy.gui.settings;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Insert.sqlInsert;
import static org.andy.toolbox.sql.Read.sqlReadArray;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.andy.toolbox.misc.SetFrameIcon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JFtaxValues extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFtaxValues.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static String sConn;
	private static String[][] arrTaxData = new String[100][25];
	private static ArrayList<String> TaxData = new ArrayList<>();
	private static int AnzData;
	private JButton btnDoInsert = null, btnDoUpdate = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {

		TaxData.clear();
		Arrays.fill(arrTaxData, null);

		try {

			String sSQLStatement = "SELECT * from tblTaxValue ORDER BY [id_year]"; //SQL Befehlszeile";

			arrTaxData = sqlReadArray(sConn, sSQLStatement);

			if(arrTaxData[0][0] != null) {
				AnzData = Integer.parseInt(arrTaxData[0][0]);
			}else {
				AnzData = 0;
			}

		} catch (SQLException | NullPointerException | ClassNotFoundException e1) {
			logger.error("error reading taxdata from database - " + e1);
		}

		try {
			JFtaxValues frame = new JFtaxValues();
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("fatal error loading gui for taxdata - " + e);
		}
	}

	public JFtaxValues() {
		
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		NumberFormat percentageFormat = NumberFormat.getPercentInstance(Locale.GERMANY);
		percentageFormat.setMinimumFractionDigits(1);
		percentageFormat.setMaximumFractionDigits(1);

		try {
			setIconImage(SetFrameIcon.getFrameIcon("config.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Einkommens-/Steuertabelle");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 625, 489);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);


		//------------------------------------------------------------------------------
		// Buttons anlegen
		//------------------------------------------------------------------------------
		try {
			btnDoInsert = createButton("<html>Jahr anlegen</html>", null);
			btnDoUpdate = createButton("<html>Tabelle<br>updaten</html>", "update.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnDoInsert.setEnabled(true);
		btnDoInsert.setVisible(false);
		btnDoUpdate.setEnabled(false);
		btnDoInsert.setBounds(300, 35, 300, 25);
		btnDoUpdate.setBounds(470, 380, 130, 50);

		contentPane.add(btnDoInsert);
		contentPane.add(btnDoUpdate);

		//------------------------------------------------------------------------------
		// ComboBox cmbYear
		//------------------------------------------------------------------------------
		TaxData.add(" ");
		for (int x = 1; (x-1) < AnzData; x++)
		{
			TaxData.add(arrTaxData[x][1]);
		}
		JComboBox<String> cmbYear = new JComboBox<>(TaxData.toArray(new String[0]));
		cmbYear.setBounds(130, 10, 140, 25);
		
		JLabel lbl00 = new JLabel("Jahr anlegen");
		lbl00.setHorizontalAlignment(SwingConstants.CENTER);
		lbl00.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl00.setBounds(300, 10, 150, 25);
		lbl00.setVisible(false);
				
		JLabel lbl01 = new JLabel("Jahr");
		lbl01.setHorizontalAlignment(SwingConstants.LEFT);
		lbl01.setFont(new Font("Tahoma", Font.BOLD, 14));
		lbl01.setBounds(10, 10, 115, 30);
				
		JLabel lbl02 = new JLabel("Einkommenstabelle");
		lbl02.setHorizontalAlignment(SwingConstants.LEFT);
		lbl02.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbl02.setBounds(10, 50, 261, 30);
		
		JLabel lbl03 = new JLabel("von");
		lbl03.setHorizontalAlignment(SwingConstants.CENTER);
		lbl03.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl03.setBounds(10, 90, 150, 25);
				
		JLabel lbl04 = new JLabel("bis");
		lbl04.setHorizontalAlignment(SwingConstants.CENTER);
		lbl04.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl04.setBounds(160, 90, 150, 25);
		
		JLabel lbl05 = new JLabel("Steuersatz (%)");
		lbl05.setHorizontalAlignment(SwingConstants.CENTER);
		lbl05.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl05.setBounds(310, 90, 150, 25);
		
		JLabel lbl10 = new JLabel("Pauschalen");
		lbl10.setHorizontalAlignment(SwingConstants.LEFT);
		lbl10.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbl10.setBounds(10, 350, 261, 25);
		
		JLabel lbl11 = new JLabel("Öffi-Pauschale");
		lbl11.setHorizontalAlignment(SwingConstants.LEFT);
		lbl11.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl11.setBounds(10, 380, 150, 25);
		
		JLabel lbl12 = new JLabel("großes Arbeitsplatzpausch.");
		lbl12.setHorizontalAlignment(SwingConstants.LEFT);
		lbl12.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl12.setBounds(10, 410, 150, 25);
		
		JTextField txt00 = new JTextField();
		txt00.setBounds(450, 10, 150, 25);
		txt00.setHorizontalAlignment(SwingConstants.CENTER);
		txt00.setVisible(false);
		
		JFormattedTextField txtVon01 = new JFormattedTextField(currencyFormat);
		txtVon01.setBounds(10, 120, 150, 25);
		txtVon01.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis01 = new JFormattedTextField(currencyFormat);
		txtBis01.setBounds(160, 120, 150, 25);
		txtBis01.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax01 = new JFormattedTextField(percentageFormat);
		txtTax01.setBounds(310, 120, 150, 25);
		txtTax01.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtVon02 = new JFormattedTextField(currencyFormat);
		txtVon02.setBounds(10, 150, 150, 25);
		txtVon02.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis02 = new JFormattedTextField(currencyFormat);
		txtBis02.setBounds(160, 150, 150, 25);
		txtBis02.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax02 = new JFormattedTextField(percentageFormat);
		txtTax02.setBounds(310, 150, 150, 25);
		txtTax02.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtVon03 = new JFormattedTextField(currencyFormat);
		txtVon03.setBounds(10, 180, 150, 25);
		txtVon03.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis03 = new JFormattedTextField(currencyFormat);
		txtBis03.setBounds(160, 180, 150, 25);
		txtBis03.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax03 = new JFormattedTextField(percentageFormat);
		txtTax03.setBounds(310, 180, 150, 25);
		txtTax03.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtVon04 = new JFormattedTextField(currencyFormat);
		txtVon04.setBounds(10, 210, 150, 25);
		txtVon04.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis04 = new JFormattedTextField(currencyFormat);
		txtBis04.setBounds(160, 210, 150, 25);
		txtBis04.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax04 = new JFormattedTextField(percentageFormat);
		txtTax04.setBounds(310, 210, 150, 25);
		txtTax04.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtVon05 = new JFormattedTextField(currencyFormat);
		txtVon05.setBounds(10, 240, 150, 25);
		txtVon05.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis05 = new JFormattedTextField(currencyFormat);
		txtBis05.setBounds(160, 240, 150, 25);
		txtBis05.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax05 = new JFormattedTextField(percentageFormat);
		txtTax05.setBounds(310, 240, 150, 25);
		txtTax05.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtVon06 = new JFormattedTextField(currencyFormat);
		txtVon06.setBounds(10, 270, 150, 25);
		txtVon06.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis06 = new JFormattedTextField(currencyFormat);
		txtBis06.setBounds(160, 270, 150, 25);
		txtBis06.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax06 = new JFormattedTextField(percentageFormat);
		txtTax06.setBounds(310, 270, 150, 25);
		txtTax06.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtVon07 = new JFormattedTextField(currencyFormat);
		txtVon07.setBounds(10, 300, 150, 25);
		txtVon07.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtBis07 = new JFormattedTextField(currencyFormat);
		txtBis07.setBounds(160, 300, 150, 25);
		txtBis07.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax07 = new JFormattedTextField(percentageFormat);
		txtTax07.setBounds(310, 300, 150, 25);
		txtTax07.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtOpnvP = new JFormattedTextField(currencyFormat);
		txtOpnvP.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOpnvP.setBounds(160, 380, 150, 25);
		
		JFormattedTextField txtArPlP = new JFormattedTextField(currencyFormat);
		txtArPlP.setHorizontalAlignment(SwingConstants.RIGHT);
		txtArPlP.setBounds(160, 410, 150, 25);
		
		contentPane.add(cmbYear);
		contentPane.add(lbl00);
		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04);
		contentPane.add(lbl05);
		contentPane.add(lbl10);
		contentPane.add(lbl11);
		contentPane.add(lbl12);
		contentPane.add(txt00);
		contentPane.add(txtVon01);
		contentPane.add(txtBis01);
		contentPane.add(txtTax01);
		contentPane.add(txtVon02);
		contentPane.add(txtBis02);
		contentPane.add(txtTax02);
		contentPane.add(txtVon03);
		contentPane.add(txtBis03);
		contentPane.add(txtTax03);
		contentPane.add(txtVon04);
		contentPane.add(txtBis04);
		contentPane.add(txtTax04);
		contentPane.add(txtVon05);
		contentPane.add(txtBis05);
		contentPane.add(txtTax05);
		contentPane.add(txtVon06);
		contentPane.add(txtBis06);
		contentPane.add(txtTax06);
		contentPane.add(txtVon07);
		contentPane.add(txtBis07);
		contentPane.add(txtTax07);
		contentPane.add(txtOpnvP);
		contentPane.add(txtArPlP);
		
		//------------------------------------------------------------------------------
		// Action Listeners
		//------------------------------------------------------------------------------
		btnDoInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Datenprüfung
				//------------------------------------------------------------------------------
				if(Integer.valueOf(txt00.getText()) < 2020 || Integer.valueOf(txt00.getText()) > 2050) {
					JOptionPane.showMessageDialog(rootPane, "Jahreszahl fehlerhaft", "Daten anlegen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				// aufbereiten und in DB schreiben
				//------------------------------------------------------------------------------
				
				boolean bResult = false;
				
				try {

					String sSQLStatement = "INSERT INTO [tblTaxValue] VALUES ('" + Integer.valueOf(txt00.getText()) + "','"
					+ txtVon01.getValue() + "','" + txtBis01.getValue() + "','" + txtTax01.getValue() + "','"
					+ txtVon02.getValue() + "','" + txtBis02.getValue() + "','" + txtTax02.getValue() + "','"
					+ txtVon03.getValue() + "','" + txtBis03.getValue() + "','" + txtTax03.getValue() + "','"
					+ txtVon04.getValue() + "','" + txtBis04.getValue() + "','" + txtTax04.getValue() + "','"
					+ txtVon05.getValue() + "','" + txtBis05.getValue() + "','" + txtTax05.getValue() + "','"
					+ txtVon06.getValue() + "','" + txtBis06.getValue() + "','" + txtTax06.getValue() + "','"
					+ txtVon07.getValue() + "','" + txtBis07.getValue() + "','" + txtTax07.getValue() + "','"
					+ txtOpnvP.getValue() + "','" + txtArPlP.getValue() + "')";

					bResult = sqlInsert(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error inserting new taxvalue into database - " + e1);
				}

				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "anlegen erfolgreich", "Daten anlegen", JOptionPane.INFORMATION_MESSAGE);
					
					lbl00.setVisible(false);
					txt00.setText("");
					txt00.setVisible(false);
					btnDoInsert.setVisible(false);
					
					dispose();
					loadGUI();
					
				}else {
					JOptionPane.showMessageDialog(rootPane, "anlegen Fehler", "Daten anlegen", JOptionPane.ERROR_MESSAGE);
					return;
				}

			}
		});
				
		btnDoUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// aufbereiten und Artikel in DB aktualisieren
				//------------------------------------------------------------------------------
				boolean bResult = false;
				
				try {

					String sSQLStatement = "UPDATE tblTaxValue SET "
							+ "[von_1] = '" + txtVon01.getValue() + "', [bis_1] = '" + txtBis01.getValue() + "', [tax_1] = '" + txtTax01.getValue() + "', "
							+ "[von_2] = '" + txtVon02.getValue() + "', [bis_2] = '" + txtBis02.getValue() + "', [tax_2] = '" + txtTax02.getValue() + "', "
							+ "[von_3] = '" + txtVon03.getValue() + "', [bis_3] = '" + txtBis03.getValue() + "', [tax_3] = '" + txtTax03.getValue() + "', "
							+ "[von_4] = '" + txtVon04.getValue() + "', [bis_4] = '" + txtBis04.getValue() + "', [tax_4] = '" + txtTax04.getValue() + "', "
							+ "[von_5] = '" + txtVon05.getValue() + "', [bis_5] = '" + txtBis05.getValue() + "', [tax_5] = '" + txtTax05.getValue() + "', "
							+ "[von_6] = '" + txtVon06.getValue() + "', [bis_6] = '" + txtBis06.getValue() + "', [tax_6] = '" + txtTax06.getValue() + "', "
							+ "[von_7] = '" + txtVon07.getValue() + "', [bis_7] = '" + txtBis07.getValue() + "', [tax_7] = '" + txtTax07.getValue() + "', "
							+ "[opnv_pausch] = '" + txtOpnvP.getValue() + "', [arbeitspl_pausch] = '" + txtArPlP.getValue() + "' "
							+ "WHERE [id_year] = '" + cmbYear.getSelectedItem().toString() + "'";

					bResult = sqlUpdate(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error updating taxvalue in database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "updaten erolgreich", "Daten update", JOptionPane.INFORMATION_MESSAGE);

					lbl00.setVisible(false);
					txt00.setText("");
					txt00.setVisible(false);
					btnDoInsert.setVisible(false);
					
					dispose();
					loadGUI();
					
				}else {
					JOptionPane.showMessageDialog(rootPane, "updaten Fehler", "Daten update", JOptionPane.ERROR_MESSAGE);
					return;
				}

			}
		});

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if(cmbYear.getSelectedIndex() > 0) {
					
					txtVon01.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][2].replace(",", ".")));
					txtBis01.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][3].replace(",", ".")));
					txtTax01.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][4].replace(",", ".")));
					
					txtVon02.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][5].replace(",", ".")));
					txtBis02.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][6].replace(",", ".")));
					txtTax02.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][7].replace(",", ".")));
					
					txtVon03.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][8].replace(",", ".")));
					txtBis03.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][9].replace(",", ".")));
					txtTax03.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][10].replace(",", ".")));
					
					txtVon04.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][11].replace(",", ".")));
					txtBis04.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][12].replace(",", ".")));
					txtTax04.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][13].replace(",", ".")));
					
					txtVon05.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][14].replace(",", ".")));
					txtBis05.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][15].replace(",", ".")));
					txtTax05.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][16].replace(",", ".")));
					
					txtVon06.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][17].replace(",", ".")));
					txtBis06.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][18].replace(",", ".")));
					txtTax06.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][19].replace(",", ".")));
					
					txtVon07.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][20].replace(",", ".")));
					txtBis07.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][21].replace(",", ".")));
					txtTax07.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][22].replace(",", ".")));
					
					txtOpnvP.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][23].replace(",", ".")));
					txtArPlP.setValue(Double.valueOf(arrTaxData[cmbYear.getSelectedIndex()][24].replace(",", ".")));
					
					lbl00.setVisible(false);
					txt00.setVisible(false);
					btnDoInsert.setVisible(false);
					
					btnDoUpdate.setEnabled(true);
					
				}else {
					
					txtVon01.setValue(Double.valueOf("0.00"));
					txtBis01.setValue(Double.valueOf("0.00"));
					txtTax01.setValue(Double.valueOf("0.00"));
					
					txtVon02.setValue(Double.valueOf("0.00"));
					txtBis02.setValue(Double.valueOf("0.00"));
					txtTax02.setValue(Double.valueOf("0.00"));
					
					txtVon03.setValue(Double.valueOf("0.00"));
					txtBis03.setValue(Double.valueOf("0.00"));
					txtTax03.setValue(Double.valueOf("0.00"));
					
					txtVon04.setValue(Double.valueOf("0.00"));
					txtBis04.setValue(Double.valueOf("0.00"));
					txtTax04.setValue(Double.valueOf("0.00"));
					
					txtVon05.setValue(Double.valueOf("0.00"));
					txtBis05.setValue(Double.valueOf("0.00"));
					txtTax05.setValue(Double.valueOf("0.00"));
					
					txtVon06.setValue(Double.valueOf("0.00"));
					txtBis06.setValue(Double.valueOf("0.00"));
					txtTax06.setValue(Double.valueOf("0.00"));
					
					txtVon07.setValue(Double.valueOf("0.00"));
					txtBis07.setValue(Double.valueOf("0.00"));
					txtTax07.setValue(Double.valueOf("0.00"));
					
					txtOpnvP.setValue(Double.valueOf("0.00"));
					txtArPlP.setValue(Double.valueOf("0.00"));
					
					lbl00.setVisible(true);
					txt00.setVisible(true);
					btnDoInsert.setVisible(true);
					
					btnDoUpdate.setEnabled(true);

				}
			}
		};
		//------------------------------------------------------------------------------
		cmbYear.addActionListener(actionListener);

		
	
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFtaxValues.sConn = sConn;
	}
}
