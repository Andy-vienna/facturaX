package org.andy.gui.settings;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Read.sqlReadArrayList;
import static org.andy.toolbox.sql.Insert.sqlInsert;
import static org.andy.toolbox.sql.Delete.sqlDeleteNoReturn;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.sql.SQLmasterData;
import org.andy.toolbox.misc.SetFrameIcon;

public class JFowner extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFowner.class);

	private static final long serialVersionUID = 1L;

	private static String sConn;
	static ArrayList<ArrayList<String>> sArrOwner = null;
	static ArrayList<String> sOwnerData = new ArrayList<>();

	private JLabel[] lbl = new JLabel[11];
	private JTextField[] txt = new JTextField[11];
	private JButton btnDoInsert = null, btnCancel = null;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static void loadGUI() {

		try {

			sArrOwner = sqlReadArrayList(sConn, "tblOwner", "*", "*");
			sOwnerData = sArrOwner.get(0);

		} catch (SQLException | NullPointerException | ClassNotFoundException e1) {
			logger.error("error reading customer data from database - " + e1);
		}

		try {
			JFowner frame = new JFowner();
			frame.setVisible(true);
		} catch (Exception e) {
			logger.fatal("fatal error loading gui for editing data - " + e);
		}
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private JFowner() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("config.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Kundendaten bearbeiten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 660, 450);
		setLocationRelativeTo(null);
		setLayout(null);

		// Überschriften und Feldbeschriftungen
	    String[] labels = {"Firma", "Strasse", "PLZ", "Ort", "Land", "USt.ID", "Kontakt", "Telefon", "E-Mail", "Währung", "Steuer.ID"};
		
		//------------------------------------------------------------------------------
		// Labels anlegen
		//------------------------------------------------------------------------------
	    for (int r = 0; r < labels.length; r++) {
	        lbl[r] = new JLabel(labels[r]);
	        lbl[r].setBounds(10, 10 + r * 30, 110, 30);
	        if (r == 0) {
	            lbl[r].setFont(new Font("Tahoma", Font.BOLD, 14));
	        } else {
	            lbl[r].setFont(new Font("Tahoma", Font.PLAIN, 12));
	        }
	        add(lbl[r]);
	    }
		
		//------------------------------------------------------------------------------
		// Textfelder anlegen
		//------------------------------------------------------------------------------
	    for (int r = 0; r < txt.length; r++) {
	    	txt[r] = new JTextField(sOwnerData.get(r));
	        txt[r].setBounds(130, 10 + r * 30, 500, 30);
	        txt[r].setHorizontalAlignment(SwingConstants.LEFT);
            if (r == 0) {
	            txt[r].setFont(new Font("Tahoma", Font.BOLD, 14));
	        } else {
	        	txt[r].setFont(new Font("Tahoma", Font.PLAIN, 12));
	        }
            add(txt[r]);
        }
		
		//------------------------------------------------------------------------------
		// Buttons anlegen
		//------------------------------------------------------------------------------
		try {
			btnDoInsert = createButton("<html>Owner<br>schreiben</html>", "new.png");
			btnCancel = createButton("cancel", "exit.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnDoInsert.setEnabled(true);
		btnCancel.setEnabled(true);
		btnDoInsert.setBounds(10, 350, 130, 50);
		btnCancel.setBounds(500, 350, 130, 50);

		add(btnDoInsert);
		add(btnCancel);
		
		//###################################################################################################################################################
		// Windows Listener
		//###################################################################################################################################################
		
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

		//###################################################################################################################################################
		// Action Listeners für Buttons
		//###################################################################################################################################################

		btnDoInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------------------------------------------------------------------
				// Prüfung der Eingaben
				//------------------------------------------------------------------------------
				boolean foundEmpty = false;
				for (JTextField check : txt) {
				    if (check.getText().trim().isEmpty()) {
				        foundEmpty = true;
				        break;
				    }
				}

				if (foundEmpty) {
				    JOptionPane.showMessageDialog(null, "Daten unvollständig ...","Eingabefehler", JOptionPane.ERROR_MESSAGE);
				    return;
				}
				//------------------------------------------------------------------------------
				//Prüfung fertig, in Datenbank schreiben
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[11];
				for (int i = 0; i < txt.length; i++) {
					tmpArray[i] = txt[i].getText();
				}
				
				try { // vorhandene Datensätze entfernen
					
					String sSQLStatement = "DELETE FROM [tblOwner]"; //SQL Befehlszeile
					
					sqlDeleteNoReturn(sConn, sSQLStatement);
					
				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error deleting old owner data from database - " + e1);
				}

				try { // neuen Datensatz schreiben
					
					String sSQLStatement = "INSERT INTO [tblOwner] VALUES ('" + tmpArray[0] + "','" + tmpArray[1] + "','" + tmpArray[2] + "','"
							+ tmpArray[3] + "','" + tmpArray[4] + "','" + tmpArray[5] + "','" + tmpArray[6] + "','" + tmpArray[7] + "','"
							+ tmpArray[8] + "','" + tmpArray[9] + "','" + tmpArray[10] + "')" ; //SQL Befehlszeile

					bResult = sqlInsert(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error writing owner data into database - " + e1);
				}
				//------------------------------------------------------------------------------
				// return auswerten
				//------------------------------------------------------------------------------
				if(bResult == true) {
					JOptionPane.showMessageDialog(rootPane, "Owner schreiben OK","Bestätigung", JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(rootPane, "Owner schreiben Fehler !","Bestätigung", JOptionPane.ERROR_MESSAGE);
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
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFowner.sConn = sConn;
	}

}