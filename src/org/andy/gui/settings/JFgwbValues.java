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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.toolbox.misc.SetFrameIcon;

public class JFgwbValues extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFgwbValues.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static String sConn;
	private static String[][] arrGwbData = new String[100][3];
	private static ArrayList<String> GwbData = new ArrayList<>();
	private static int AnzData;
	private JButton btnDoInsert = null, btnDoUpdate = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {

		GwbData.clear();
		Arrays.fill(arrGwbData, null);

		try {

			String sSQLStatement = "SELECT * from tblGwbValue ORDER BY [id_year]"; //SQL Befehlszeile";

			arrGwbData = sqlReadArray(sConn, sSQLStatement);

			if(arrGwbData[0][0] != null) {
				AnzData = Integer.parseInt(arrGwbData[0][0]);
			}else {
				AnzData = 0;
			}

		} catch (SQLException | NullPointerException | ClassNotFoundException e1) {
			logger.error("error reading gwbdata from database - " + e1);
		}

		try {
			JFgwbValues frame = new JFgwbValues();
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("fatal error loading gui for gwbdata - " + e);
		}
	}

	public JFgwbValues() {
		
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
		setTitle("Gewinnfreibetragstabelle");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 625, 280);
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
		btnDoUpdate.setBounds(470, 185, 130, 50);

		contentPane.add(btnDoInsert);
		contentPane.add(btnDoUpdate);

		//------------------------------------------------------------------------------
		// ComboBox cmbYear
		//------------------------------------------------------------------------------
		GwbData.add(" ");
		for (int x = 1; (x-1) < AnzData; x++)
		{
			GwbData.add(arrGwbData[x][1]);
		}
		JComboBox<String> cmbYear = new JComboBox<>(GwbData.toArray(new String[0]));
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
		
		JLabel lbl03 = new JLabel("bis");
		lbl03.setHorizontalAlignment(SwingConstants.CENTER);
		lbl03.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl03.setBounds(10, 120, 150, 25);
				
		JLabel lbl04a = new JLabel("weitere");
		lbl04a.setHorizontalAlignment(SwingConstants.CENTER);
		lbl04a.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl04a.setBounds(10, 150, 150, 25);
		
		JLabel lbl04b = new JLabel("weitere");
		lbl04b.setHorizontalAlignment(SwingConstants.CENTER);
		lbl04b.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl04b.setBounds(10, 180, 150, 25);
		
		JLabel lbl04c = new JLabel("weitere");
		lbl04c.setHorizontalAlignment(SwingConstants.CENTER);
		lbl04c.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl04c.setBounds(10, 210, 150, 25);
		
		JLabel lbl05 = new JLabel("Gewinnfreibetrag (%)");
		lbl05.setHorizontalAlignment(SwingConstants.CENTER);
		lbl05.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbl05.setBounds(310, 90, 150, 25);
		
		JTextField txt00 = new JTextField();
		txt00.setBounds(450, 10, 150, 25);
		txt00.setHorizontalAlignment(SwingConstants.CENTER);
		txt00.setVisible(false);
		
		JFormattedTextField txtBis01 = new JFormattedTextField(currencyFormat);
		txtBis01.setBounds(160, 120, 150, 25);
		txtBis01.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax01 = new JFormattedTextField(percentageFormat);
		txtTax01.setBounds(310, 120, 150, 25);
		txtTax01.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtWeitere02 = new JFormattedTextField(currencyFormat);
		txtWeitere02.setBounds(160, 150, 150, 25);
		txtWeitere02.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax02 = new JFormattedTextField(percentageFormat);
		txtTax02.setBounds(310, 150, 150, 25);
		txtTax02.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtWeitere03 = new JFormattedTextField(currencyFormat);
		txtWeitere03.setBounds(160, 180, 150, 25);
		txtWeitere03.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax03 = new JFormattedTextField(percentageFormat);
		txtTax03.setBounds(310, 180, 150, 25);
		txtTax03.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtWeitere04 = new JFormattedTextField(currencyFormat);
		txtWeitere04.setBounds(160, 210, 150, 25);
		txtWeitere04.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JFormattedTextField txtTax04 = new JFormattedTextField(percentageFormat);
		txtTax04.setBounds(310, 210, 150, 25);
		txtTax04.setHorizontalAlignment(SwingConstants.RIGHT);
		
		contentPane.add(cmbYear);
		contentPane.add(lbl00);
		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04a);
		contentPane.add(lbl04b);
		contentPane.add(lbl04c);
		contentPane.add(lbl05);
		contentPane.add(txt00);
		contentPane.add(txtBis01);
		contentPane.add(txtTax01);
		contentPane.add(txtWeitere02);
		contentPane.add(txtTax02);
		contentPane.add(txtWeitere03);
		contentPane.add(txtTax03);
		contentPane.add(txtWeitere04);
		contentPane.add(txtTax04);
		
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

					String sSQLStatement = "INSERT INTO [tblGwbValue] VALUES ('" + Integer.valueOf(txt00.getText()) + "','"
					+ txtBis01.getValue() + "','" + txtTax01.getValue() + "','"
					+ txtWeitere02.getValue() + "','" + txtTax02.getValue() + "','"
					+ txtWeitere03.getValue() + "','" + txtTax03.getValue() + "','"
					+ txtWeitere04.getValue() + "','" + txtTax04.getValue() + "')";

					bResult = sqlInsert(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error inserting new gwbvalue into database - " + e1);
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

					String sSQLStatement = "UPDATE tblGwbValue SET "
							+ "[bis_1] = '" + txtBis01.getValue() + "', [val_1] = '" + txtTax01.getValue() + "', "
							+ "[weitere_2] = '" + txtWeitere02.getValue() + "', [val_2] = '" + txtTax02.getValue() + "', "
							+ "[weitere_3] = '" + txtWeitere03.getValue() + "', [val_3] = '" + txtTax03.getValue() + "', "
							+ "[weitere_4] = '" + txtWeitere04.getValue() + "', [val_4] = '" + txtTax04.getValue() + "' "
							+ "WHERE [id_year] = '" + cmbYear.getSelectedItem().toString() + "'";

					bResult = sqlUpdate(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error updating gwbvalue in database - " + e1);
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
					
					txtBis01.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][2].replace(",", ".")));
					txtTax01.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][3].replace(",", ".")));
					
					txtWeitere02.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][4].replace(",", ".")));
					txtTax02.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][5].replace(",", ".")));
					
					txtWeitere03.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][6].replace(",", ".")));
					txtTax03.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][7].replace(",", ".")));
					
					txtWeitere04.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][8].replace(",", ".")));
					txtTax04.setValue(Double.valueOf(arrGwbData[cmbYear.getSelectedIndex()][9].replace(",", ".")));
					
					lbl00.setVisible(false);
					txt00.setVisible(false);
					btnDoInsert.setVisible(false);
					
					btnDoUpdate.setEnabled(true);
					
				}else {
					
					txtBis01.setValue(Double.valueOf("0.00"));
					txtTax01.setValue(Double.valueOf("0.00"));
					
					txtWeitere02.setValue(Double.valueOf("0.00"));
					txtTax02.setValue(Double.valueOf("0.00"));
					
					txtWeitere03.setValue(Double.valueOf("0.00"));
					txtTax03.setValue(Double.valueOf("0.00"));
					
					txtWeitere04.setValue(Double.valueOf("0.00"));
					txtTax04.setValue(Double.valueOf("0.00"));
					
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
		JFgwbValues.sConn = sConn;
	}
}
