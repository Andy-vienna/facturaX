package org.andy.gui.settings;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Read.sqlReadArrayList;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.sql.SQLmasterData;
import org.andy.toolbox.misc.SetFrameIcon;

public class JFowner extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFowner.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static String sConn;
	static ArrayList<ArrayList<String>> sArrOwner = null;
	static ArrayList<String> sOwnerData = new ArrayList<>();

	private JTextField txt01, txt02, txt03, txt04, txt05, txt06, txt07, txt08, txt09, txt10;
	private JButton btnDoInsert = null, btnCancel = null;

	//###################################################################################################################################################
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

	public JFowner() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("config.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Kundendaten bearbeiten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 660, 420);
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
		btnDoInsert.setBounds(10, 320, 130, 50);
		btnCancel.setBounds(500, 320, 130, 50);

		contentPane.add(btnDoInsert);
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
				if(txt01.getText().isEmpty() || txt02.getText().isEmpty() || txt03.getText().isEmpty() | txt04.getText().isEmpty() || txt05.getText().isEmpty()
						|| txt06.getText().isEmpty() || txt07.getText().isEmpty() || txt08.getText().isEmpty() || txt09.getText().isEmpty() || txt10.getText().isEmpty()){
					JOptionPane.showMessageDialog(rootPane, "Daten unvollständig ...","Eingabefehler", JOptionPane.ERROR_MESSAGE);
					return;
				}
				//------------------------------------------------------------------------------
				//Prüfung fertig, in Datenbank schreiben
				//------------------------------------------------------------------------------
				boolean bResult = false;
				String[] tmpArray = new String[10];
				tmpArray[0] = txt01.getText();
				tmpArray[1] = txt02.getText();
				tmpArray[2] = txt03.getText();
				tmpArray[3] = txt04.getText();
				tmpArray[4] = txt05.getText();
				tmpArray[5] = txt06.getText();
				tmpArray[6] = txt07.getText();
				tmpArray[7] = txt08.getText();
				tmpArray[8] = txt09.getText();
				tmpArray[9] = txt10.getText();

				try {

					String sSQLStatement = "UPDATE [tblOwner] SET [Name] = '" + tmpArray[0] + "',[Adresse] = '" + tmpArray[1] + "',[PLZ] = '" + tmpArray[2]
							+ "',[Ort] = '" + tmpArray[3] + "',[Land] = '" + tmpArray[4] + "',[UStId] = '" + tmpArray[5] + "',[KontaktName] = '" + tmpArray[6]
									+ "',[KontaktTel] = '" + tmpArray[7] + "',[KontaktMail] = '" + tmpArray[8] + "',[Currency] = '" + tmpArray[9] + "'"; //SQL Befehlszeile

					bResult = sqlUpdate(sConn, sSQLStatement);

				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error inserting owner data into database - " + e1);
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

		JLabel lbl1 = new JLabel("Firma");
		lbl1.setBounds(10, 10, 115, 30);
		lbl1.setHorizontalAlignment(SwingConstants.LEFT);
		lbl1.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(lbl1);

		JLabel lbl2 = new JLabel("Strasse");
		lbl2.setBounds(10, 40, 110, 30);
		lbl2.setHorizontalAlignment(SwingConstants.LEFT);
		lbl2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl2);

		JLabel lbl3 = new JLabel("PLZ");
		lbl3.setBounds(10, 70, 110, 30);
		lbl3.setHorizontalAlignment(SwingConstants.LEFT);
		lbl3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl3);

		JLabel lbl4 = new JLabel("Ort");
		lbl4.setBounds(10, 100, 110, 30);
		lbl4.setHorizontalAlignment(SwingConstants.LEFT);
		lbl4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl4);

		JLabel lbl5 = new JLabel("Land");
		lbl5.setBounds(10, 130, 110, 30);
		lbl5.setHorizontalAlignment(SwingConstants.LEFT);
		lbl5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl5);

		JLabel lbl6 = new JLabel("USt.ID");
		lbl6.setBounds(10, 160, 110, 30);
		lbl6.setHorizontalAlignment(SwingConstants.LEFT);
		lbl6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl6);

		JLabel lbl7 = new JLabel("Kontakt");
		lbl7.setBounds(10, 190, 110, 30);
		lbl7.setHorizontalAlignment(SwingConstants.LEFT);
		lbl7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl7);

		JLabel lbl8 = new JLabel("Telefon");
		lbl8.setBounds(10, 220, 110, 30);
		lbl8.setHorizontalAlignment(SwingConstants.LEFT);
		lbl8.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl8);

		JLabel lbl9 = new JLabel("E-Mail");
		lbl9.setBounds(10, 250, 110, 30);
		lbl9.setHorizontalAlignment(SwingConstants.LEFT);
		lbl9.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl9);

		JLabel lbl10 = new JLabel("Währung");
		lbl10.setBounds(10, 280, 110, 30);
		lbl10.setHorizontalAlignment(SwingConstants.LEFT);
		lbl10.setFont(new Font("Tahoma", Font.PLAIN, 12));
		contentPane.add(lbl10);

		txt01 = new JTextField(sOwnerData.get(0));
		txt01.setBounds(130, 10, 500, 30);
		txt01.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(txt01);
		txt01.setColumns(40);

		txt02 = new JTextField(sOwnerData.get(1));
		txt02.setBounds(130, 40, 500, 30);
		txt02.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt02.setColumns(40);
		contentPane.add(txt02);

		txt03 = new JTextField(sOwnerData.get(2));
		txt03.setBounds(130, 70, 500, 30);
		txt03.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt03.setColumns(40);
		contentPane.add(txt03);

		txt04 = new JTextField(sOwnerData.get(3));
		txt04.setBounds(130, 100, 500, 30);
		txt04.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt04.setColumns(40);
		contentPane.add(txt04);

		txt05 = new JTextField(sOwnerData.get(4));
		txt05.setBounds(130, 130, 500, 30);
		txt05.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt05.setColumns(40);
		contentPane.add(txt05);

		txt06 = new JTextField(sOwnerData.get(5));
		txt06.setBounds(130, 160, 500, 30);
		txt06.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt06.setColumns(40);
		contentPane.add(txt06);

		txt07 = new JTextField(sOwnerData.get(6));
		txt07.setBounds(130, 190, 500, 30);
		txt07.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt07.setColumns(40);
		contentPane.add(txt07);

		txt08 = new JTextField(sOwnerData.get(7));
		txt08.setBounds(130, 220, 500, 30);
		txt08.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt08.setColumns(40);
		contentPane.add(txt08);

		txt09 = new JTextField(sOwnerData.get(8));
		txt09.setBounds(130, 250, 500, 30);
		txt09.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt09.setColumns(40);
		contentPane.add(txt09);

		txt10 = new JTextField(sOwnerData.get(9));
		txt10.setBounds(130, 280, 500, 30);
		txt10.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txt10.setColumns(40);
		contentPane.add(txt10);

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFowner.sConn = sConn;
	}

}