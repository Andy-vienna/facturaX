package org.andy.gui.settings;

import static main.java.toolbox.misc.CreateObject.addBorderFocusListener;
import static main.java.toolbox.misc.CreateObject.changeKomma;
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
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.sql.SQLmasterData;
import org.andy.org.eclipse.wb.swing.FocusTraversalOnArray;
import main.java.toolbox.misc.SetFrameIcon;

public class JFartikel extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFartikel.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static String sConn;
	private static String[][] arrArtikel = new String[100][3];
	private static ArrayList<String> ArtData = new ArrayList<>();
	private static int AnzArtikel;
	private JTextField textArtikelnummer, textArtikelText, textArtikelWert;
	private JButton btnDoInsert = null, btnDoUpdate = null, btnDoDelete = null, btnCancel = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {

		ArtData.clear();
		Arrays.fill(arrArtikel, null);

		try {

			String sSQLStatement = "SELECT * from tblArtikel ORDER BY [Id]"; //SQL Befehlszeile";

			arrArtikel = sqlReadArray(sConn, sSQLStatement);

			if(arrArtikel[0][0] != null) {
				AnzArtikel = Integer.parseInt(arrArtikel[0][0]);
			}else {
				AnzArtikel = 0;
			}

		} catch (SQLException | NullPointerException | ClassNotFoundException e1) {
			logger.error("error reading articles from database - " + e1);
		}

		try {
			JFartikel frame = new JFartikel();
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("fatal error loading gui for editing data - " + e);
		}
	}

	public JFartikel() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("config.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Artikeldaten bearbeiten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 695, 250);
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
				ArtData.clear();
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
			btnDoInsert = createButton("<html>Artikel<br>anlegen</html>", "new.png");
			btnDoUpdate = createButton("<html>Artikel<br>updaten</html>", "update.png");
			btnDoDelete = createButton("<html>Artikel<br>loeschen</html>", "delete.png");
			btnCancel = createButton("cancel", "exit.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnDoInsert.setEnabled(true);
		btnCancel.setEnabled(true);
		btnDoInsert.setBounds(10, 150, 130, 50);
		btnDoUpdate.setBounds(150, 150, 130, 50);
		btnDoDelete.setBounds(290, 150, 130, 50);
		btnCancel.setBounds(535, 150, 130, 50);

		contentPane.add(btnDoInsert);
		contentPane.add(btnDoUpdate);
		contentPane.add(btnDoDelete);
		contentPane.add(btnCancel);

		//------------------------------------------------------------------------------
		// ComboBox cmbKunde
		//------------------------------------------------------------------------------
		ArtData.add(" ");
		for (int x = 1; (x-1) < AnzArtikel; x++)
		{
			ArtData.add(arrArtikel[x][2]);
		}
		JComboBox<String> cmbArtSelect = new JComboBox<>(ArtData.toArray(new String[0]));
		cmbArtSelect.setBounds(10, 10, 656, 30);
		contentPane.add(cmbArtSelect);
		//------------------------------------------------------------------------------
		// Action Listeners
		//------------------------------------------------------------------------------
		btnDoInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Datenprüfung
				//------------------------------------------------------------------------------
				if(textArtikelnummer.getText().isEmpty() || textArtikelText.getText().isEmpty() || textArtikelWert.getText().isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Eingabe fehlerhaft", "Daten anlegen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				// aufbereiten und in DB schreiben
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[3];
				tmpArray[0] = textArtikelnummer.getText().toUpperCase();
				tmpArray[1] = textArtikelText.getText();
				tmpArray[2] = textArtikelWert.getText();
				try {

					String sSQLStatement = "INSERT INTO [tblArtikel] VALUES ('" + tmpArray[0] + "','" + tmpArray[1] + "','" + tmpArray[2] + "')"; //SQL Befehlszeile

					bResult = sqlInsert(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error inserting new article into database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "anlegen erfolgreich", "Daten anlegen", JOptionPane.INFORMATION_MESSAGE);
					textArtikelnummer.setText("");
					textArtikelText.setText("");
					textArtikelWert.setText("");
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
				// aufbereiten und Artikel in DB aktualisieren
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[3];
				tmpArray[0] = textArtikelnummer.getText().toUpperCase();
				tmpArray[1] = textArtikelText.getText();
				tmpArray[2] = textArtikelWert.getText();
				try {

					String sSQLStatement = "UPDATE tblArtikel SET [Text] = '" + tmpArray[1] + "', [Wert] = '" + tmpArray[2] + "' WHERE [Id] = '" + tmpArray[0] + "'";

					bResult = sqlUpdate(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error updating article in database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "updaten erolgreich", "Daten update", JOptionPane.INFORMATION_MESSAGE);
					textArtikelnummer.setText("");
					textArtikelText.setText("");
					textArtikelWert.setText("");
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
				if(textArtikelnummer.getText().isEmpty()) {
					JOptionPane.showMessageDialog(rootPane, "Eingabe fehlerhaft", "Daten loeschen", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				// aufbereiten und Artikel in DB löschen
				//------------------------------------------------------------------------------
				String sArtNum = textArtikelnummer.getText();
				boolean bResult = false;
				try {

					String sSQLStatement = "DELETE FROM tblArtikel WHERE [Id] = '" + sArtNum + "'";

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
					textArtikelnummer.setText("");
					textArtikelText.setText("");
					textArtikelWert.setText("");
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
				if(cmbArtSelect.getSelectedIndex() == 0) {
					btnDoInsert.setEnabled(true);
					btnDoUpdate.setEnabled(false);
					btnDoDelete.setEnabled(false);
					textArtikelnummer.setEditable(true);
					textArtikelnummer.setText("");
					textArtikelnummer.setBackground(Color.PINK);
					textArtikelText.setText("");
					textArtikelText.setBackground(Color.PINK);
					textArtikelWert.setText("");
					textArtikelWert.setBackground(Color.PINK);
				}else {
					btnDoInsert.setEnabled(false);
					btnDoUpdate.setEnabled(true);
					btnDoDelete.setEnabled(true);
					textArtikelnummer.setEditable(false);
					textArtikelnummer.setText(arrArtikel[cmbArtSelect.getSelectedIndex()][1]);
					textArtikelnummer.setBackground(Color.WHITE);
					textArtikelText.setText(arrArtikel[cmbArtSelect.getSelectedIndex()][2]);
					textArtikelText.setBackground(Color.WHITE);
					textArtikelWert.setText(arrArtikel[cmbArtSelect.getSelectedIndex()][3]);
					textArtikelWert.setBackground(Color.WHITE);
				}
			}
		};
		//------------------------------------------------------------------------------
		cmbArtSelect.addActionListener(actionListener);

		JLabel lblNewLabel_1 = new JLabel("Artikelnummer");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(10, 50, 115, 30);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Text");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(10, 80, 110, 30);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Wert");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(10, 110, 110, 30);
		contentPane.add(lblNewLabel_3);

		JLabel lblNewLabel_3_1 = new JLabel("EUR");
		lblNewLabel_3_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3_1.setBounds(250, 110, 110, 30);
		contentPane.add(lblNewLabel_3_1);

		textArtikelnummer = new JTextField();
		textArtikelnummer.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				textArtikelnummer.setText(textArtikelnummer.getText().toUpperCase());
			}
		});
		textArtikelnummer.setFont(new Font("Tahoma", Font.BOLD, 14));
		textArtikelnummer.setColumns(40);
		textArtikelnummer.setBackground(Color.PINK);
		textArtikelnummer.setBounds(130, 50, 220, 30);
		contentPane.add(textArtikelnummer);
		addBorderFocusListener(textArtikelnummer);

		textArtikelText = new JTextField();
		textArtikelText.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textArtikelText.setColumns(40);
		textArtikelText.setBackground(Color.PINK);
		textArtikelText.setBounds(130, 80, 535, 30);
		contentPane.add(textArtikelText);
		addBorderFocusListener(textArtikelText);

		textArtikelWert = new JTextField();
		textArtikelWert.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> textArtikelWert.setText(changeKomma(textArtikelWert)));
			}
		});
		textArtikelWert.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textArtikelWert.setColumns(40);
		textArtikelWert.setBackground(Color.PINK);
		textArtikelWert.setBounds(130, 110, 110, 30);
		contentPane.add(textArtikelWert);
		addBorderFocusListener(textArtikelWert);

		contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{cmbArtSelect, textArtikelnummer, textArtikelText, textArtikelWert, btnDoInsert, btnCancel, btnDoUpdate, btnDoDelete}));
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textArtikelnummer, textArtikelText, textArtikelWert}));
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFartikel.sConn = sConn;
	}

}
