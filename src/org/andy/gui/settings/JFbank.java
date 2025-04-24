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

public class JFbank extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFbank.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static String sConn;
	public static String[][] arrBank = new String[100][5];
	private static ArrayList<String> BankData = new ArrayList<>();
	private static int AnzBank;
	private JTextField textBank, textKI, textIBAN, textBIC, textId;
	private JButton btnDoInsert = null, btnDoUpdate = null, btnDoDelete = null, btnCancel = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {

		BankData.clear();
		Arrays.fill(arrBank, null);

		try {

			String sSQLStatement = "SELECT * from tblBank ORDER BY [Id]"; //SQL Befehlszeile";

			arrBank = sqlReadArray(sConn, sSQLStatement);

			if(arrBank[0][0] != null) {
				AnzBank = Integer.parseInt(arrBank[0][0]);
			}else {
				AnzBank = 0;
			}

		} catch (SQLException | NullPointerException | ClassNotFoundException e1) {
			logger.error("error reading account data from database - " + e1);
		}

		try {
			JFbank frame = new JFbank();
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("fatal error loading gui for editing data - " + e);
		}
	}

	public JFbank() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("config.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Bankdaten bearbeiten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 585, 310);
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
				BankData.clear();
				try {
					SQLmasterData.loadBaseData();
				} catch (ClassNotFoundException | SQLException | ParseException e1) {
					logger.error("error loading base data - " + e1);
				}
			}
		});

		//------------------------------------------------------------------------------
		// Buttons anlegen
		//------------------------------------------------------------------------------
		try {
			btnDoInsert = createButton("<html>Bank<br>anlegen</html>", "new.png");
			btnDoUpdate = createButton("<html>Bank<br>updaten</html>", "update.png");
			btnDoDelete = createButton("<html>Bank<br>loeschen</html>", "delete.png");
			btnCancel = createButton("cancel", "exit.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnDoInsert.setEnabled(true);
		btnCancel.setEnabled(true);
		btnDoInsert.setBounds(10, 210, 130, 50);
		btnDoUpdate.setBounds(150, 210, 130, 50);
		btnDoDelete.setBounds(290, 210, 130, 50);
		btnCancel.setBounds(430, 210, 130, 50);

		contentPane.add(btnDoInsert);
		contentPane.add(btnDoUpdate);
		contentPane.add(btnDoDelete);
		contentPane.add(btnCancel);

		//------------------------------------------------------------------------------
		// ComboBox cmbKunde
		//------------------------------------------------------------------------------
		BankData.add(" ");
		for (int x = 1; (x-1) < AnzBank; x++)
		{
			BankData.add(arrBank[x][2]);
		}
		JComboBox<String> cmbBankSelect = new JComboBox<>(BankData.toArray(new String[0]));
		cmbBankSelect.setBounds(10, 10, 550, 30);
		contentPane.add(cmbBankSelect);
		//------------------------------------------------------------------------------
		// Action Listeners
		//------------------------------------------------------------------------------
		btnDoInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Datenprüfung
				//------------------------------------------------------------------------------
				if(textId.getText().isEmpty() || textBank.getText().isEmpty() || textKI.getText().isEmpty() || textIBAN.getText().isEmpty() || textBIC.getText().isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Dateneingabe unvollständig", "Eingabefehler", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				// aufbereiten und in DB schreiben
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[5];
				tmpArray[0] = textId.getText();
				tmpArray[1] = textBank.getText();
				tmpArray[2] = textIBAN.getText().toUpperCase();
				tmpArray[3] = textBIC.getText().toUpperCase();
				tmpArray[4] = textKI.getText();
				try {

					String sSQLStatement = "INSERT INTO [tblBank] VALUES ('" + tmpArray[0] + "','" + tmpArray[1] + "','" + tmpArray[2] + "','" + tmpArray[3] + "','" + tmpArray[4] + "')"; //SQL Befehlszeile

					bResult = sqlInsert(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error inserting new account data into database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "anlegen erfolgreich", "Daten anlegen", JOptionPane.INFORMATION_MESSAGE);
					textId.setText("");
					textBank.setText("");
					textKI.setText("");
					textIBAN.setText("");
					textBIC.setText("");
				}else {
					JOptionPane.showMessageDialog(rootPane, "anlegen Fehler", "Daten anlegen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				dispose();
			}
		});
		btnDoUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// aufbereiten und Bank in DB aktualisieren
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[5];
				tmpArray[0] = textId.getText();
				tmpArray[1] = textBank.getText();
				tmpArray[2] = textIBAN.getText();
				tmpArray[3] = textBIC.getText();
				tmpArray[4] = textKI.getText();
				try {

					String sSQLStatement = "UPDATE tblBank SET [BankName] = '" + tmpArray[1] + "', [IBAN] = '" + tmpArray[2] + "', [BIC] = '" + tmpArray[3] + "', [Kontoinhaber] = '" + tmpArray[4] + "' WHERE [Id] = '" + tmpArray[0] + "'";

					bResult = sqlUpdate(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error updating account data in database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "update erfolgreich", "Daten update", JOptionPane.INFORMATION_MESSAGE);
					textId.setText("");
					textBank.setText("");
					textKI.setText("");
					textIBAN.setText("");
					textBIC.setText("");
				}else {
					JOptionPane.showMessageDialog(rootPane, "update Fehler", "Daten update", JOptionPane.ERROR_MESSAGE);
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
				if(textId.getText().isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Dateneingabe unvollständig", "Eingabefehler", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				// aufbereiten und Bank in DB löschen
				//------------------------------------------------------------------------------
				String sId = textId.getText();
				boolean bResult = false;
				try {

					String sSQLStatement = "DELETE FROM tblBank WHERE [Id] = '" + sId + "'";

					sqlDeleteNoReturn(sConn, sSQLStatement);
					bResult = true;

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error deleting article from database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "loeschen erfolgreich", "Daten loeschen", JOptionPane.INFORMATION_MESSAGE);
					textId.setText("");
					textBank.setText("");
					textKI.setText("");
					textIBAN.setText("");
					textBIC.setText("");
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
				dispose() ; //Code für Abbruch
			}
		});

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				if(cmbBankSelect.getSelectedIndex() == 0) {
					btnDoInsert.setEnabled(true);
					btnDoUpdate.setEnabled(false);
					btnDoDelete.setEnabled(false);
					textId.setEditable(true);
					textId.setText("");
					textId.setBackground(Color.PINK);
					textBank.setText("");
					textBank.setBackground(Color.PINK);
					textKI.setText("");
					textKI.setBackground(Color.PINK);
					textIBAN.setText("");
					textIBAN.setBackground(Color.PINK);
					textBIC.setText("");
					textBIC.setBackground(Color.PINK);
				}else {
					btnDoInsert.setEnabled(false);
					btnDoUpdate.setEnabled(true);
					btnDoDelete.setEnabled(true);
					textId.setEditable(false);
					textId.setText(arrBank[cmbBankSelect.getSelectedIndex()][1]);
					textId.setBackground(Color.WHITE);
					textBank.setText(arrBank[cmbBankSelect.getSelectedIndex()][2]);
					textBank.setBackground(Color.WHITE);
					textKI.setText(arrBank[cmbBankSelect.getSelectedIndex()][5]);
					textKI.setBackground(Color.WHITE);
					textIBAN.setText(arrBank[cmbBankSelect.getSelectedIndex()][3]);
					textIBAN.setBackground(Color.WHITE);
					textBIC.setText(arrBank[cmbBankSelect.getSelectedIndex()][4]);
					textBIC.setBackground(Color.WHITE);
				}
			}
		};
		//------------------------------------------------------------------------------
		cmbBankSelect.addActionListener(actionListener);

		JLabel lblNewLabel_5 = new JLabel("Id");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_5.setBounds(10, 50, 115, 30);
		contentPane.add(lblNewLabel_5);

		JLabel lblNewLabel_1 = new JLabel("Bank");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(10, 80, 115, 30);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_4 = new JLabel("Kontoinhaber");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_4.setBounds(10, 110, 115, 30);
		contentPane.add(lblNewLabel_4);

		JLabel lblNewLabel_2 = new JLabel("IBAN");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(10, 140, 110, 30);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("BIC");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_3.setBounds(10, 170, 110, 30);
		contentPane.add(lblNewLabel_3);

		textId = new JTextField();
		textId.setFont(new Font("Tahoma", Font.BOLD, 12));
		textId.setColumns(2);
		textId.setBackground(Color.PINK);
		textId.setBounds(130, 50, 50, 30);
		contentPane.add(textId);

		textBank = new JTextField();
		textBank.setFont(new Font("Tahoma", Font.BOLD, 12));
		textBank.setColumns(40);
		textBank.setBackground(Color.PINK);
		textBank.setBounds(130, 80, 430, 30);
		contentPane.add(textBank);
		addBorderFocusListener(textBank);

		textKI = new JTextField();
		textKI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textKI.setColumns(40);
		textKI.setBackground(Color.PINK);
		textKI.setBounds(130, 110, 430, 30);
		contentPane.add(textKI);
		addBorderFocusListener(textKI);

		textIBAN = new JTextField();
		textIBAN.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textIBAN.setText(textIBAN.getText().toUpperCase());
			}
		});
		textIBAN.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textIBAN.setColumns(40);
		textIBAN.setBackground(Color.PINK);
		textIBAN.setBounds(130, 140, 430, 30);
		contentPane.add(textIBAN);
		addBorderFocusListener(textIBAN);

		textBIC = new JTextField();
		textBIC.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textBIC.setText(textBIC.getText().toUpperCase());
			}
		});
		textBIC.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textBIC.setColumns(40);
		textBIC.setBackground(Color.PINK);
		textBIC.setBounds(130, 170, 230, 30);
		contentPane.add(textBIC);
		addBorderFocusListener(textBIC);

		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textId, textBank, textKI, textIBAN, textBIC}));
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFbank.sConn = sConn;
	}
}
