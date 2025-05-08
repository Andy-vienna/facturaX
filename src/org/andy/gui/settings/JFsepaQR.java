package org.andy.gui.settings;

import static org.andy.toolbox.misc.CreateObject.createButton;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.main.LoadData;
import org.andy.toolbox.misc.SetFrameIcon;
import org.andy.toolbox.misc.Tools;

public class JFsepaQR extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFsepaQR.class);

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private static JButton btnQREdit = null, btnQROK = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI(boolean bStart) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFsepaQR frame = new JFsepaQR(bStart);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("fatal error loading gui for editing data - " + e);
				}
			}
		});
	}

	public JFsepaQR(boolean bStart) {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("qrcode.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("SEPA QR-Code Setup");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 367, 361);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		//###################################################################################################################################################
		//###################################################################################################################################################

		String[] sQRschema = LoadData.getStrQRschema().split("/"); // Properties Eintrag zerlegen

		if (sQRschema.length != 10) {
			sQRschema = new String[] { "BCD","002","1","SCT","{BIC}","{KI}","{IBAN}","EUR{SUM}","","{RENR}" };
		}

		JLabel lbl01 = new JLabel("Servicekennung");
		JLabel lbl02 = new JLabel("Version");
		JLabel lbl03 = new JLabel("Kodierung");
		JLabel lbl04 = new JLabel("Funktion");
		JLabel lbl05 = new JLabel("BIC");
		JLabel lbl06 = new JLabel("Empfänger");
		JLabel lbl07 = new JLabel("IBAN");
		JLabel lbl08 = new JLabel("Währung | Betrag");
		JLabel lbl09 = new JLabel("Zweck");
		JLabel lbl10 = new JLabel("Referenz");
		JLabel lbl11 = new JLabel("Text");
		JLabel lbl12 = new JLabel("Anzeige");

		JTextField txtQRbcd = new JTextField(sQRschema[0]);
		JTextField textQRversion = new JTextField(sQRschema[1]);
		JTextField textQRcode = new JTextField(sQRschema[2]);
		JTextField textQRsct = new JTextField(sQRschema[3]);
		JTextField txtQRbic = new JTextField(sQRschema[4]);
		JTextField txtQRki = new JTextField(sQRschema[5]);
		JTextField txtQRiban = new JTextField(sQRschema[6]);
		JTextField txtQReursum = new JTextField(sQRschema[7]);
		JTextField textQRzweck = new JTextField(sQRschema[8]);
		JTextField textQRref = new JTextField(sQRschema[9]);
		JTextField textQRtext = new JTextField();
		JTextField textQRanzeige = new JTextField();

		lbl01.setBounds(10, 10, 90, 25);
		lbl02.setBounds(10, 35, 90, 25);
		lbl03.setBounds(10, 60, 90, 25);
		lbl04.setBounds(10, 85, 90, 25);
		lbl05.setBounds(10, 110, 90, 25);
		lbl06.setBounds(10, 135, 90, 25);
		lbl07.setBounds(10, 160, 90, 25);
		lbl08.setBounds(10, 185, 90, 25);
		lbl09.setBounds(10, 210, 90, 25);
		lbl10.setBounds(10, 235, 90, 25);
		lbl11.setBounds(10, 260, 90, 25);
		lbl12.setBounds(10, 285, 90, 25);

		txtQRbcd.setBounds(110, 10, 140, 25);
		textQRversion.setBounds(110, 35, 140, 25);
		textQRcode.setBounds(110, 60, 140, 25);
		textQRsct.setBounds(110, 85, 140, 25);
		txtQRbic.setBounds(110, 110, 140, 25);
		txtQRki.setBounds(110, 135, 140, 25);
		txtQRiban.setBounds(110, 160, 140, 25);
		txtQReursum.setBounds(110, 185, 140, 25);
		textQRzweck.setBounds(110, 210, 140, 25);
		textQRref.setBounds(110, 235, 140, 25);
		textQRtext.setBounds(110, 260, 140, 25);
		textQRanzeige.setBounds(110, 285, 140, 25);

		try {
			btnQREdit = createButton(null, "edit.png");
			btnQROK = createButton(null, "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnQREdit.setEnabled(true);
		btnQREdit.setBounds(260, 220, 80, 40);
		btnQROK.setBounds(260, 270, 80, 40);

		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04);
		contentPane.add(lbl05);
		contentPane.add(lbl06);
		contentPane.add(lbl07);
		contentPane.add(lbl08);
		contentPane.add(lbl09);
		contentPane.add(lbl10);
		contentPane.add(lbl11);
		contentPane.add(lbl12);

		contentPane.add(txtQRbcd);
		contentPane.add(textQRversion);
		contentPane.add(textQRcode);
		contentPane.add(textQRsct);
		contentPane.add(txtQRbic);
		contentPane.add(txtQRki);
		contentPane.add(txtQRiban);
		contentPane.add(txtQReursum);
		contentPane.add(textQRzweck);
		contentPane.add(textQRref);
		contentPane.add(textQRtext);
		contentPane.add(textQRanzeige);

		contentPane.add(btnQREdit);
		contentPane.add(btnQROK);

		if (!bStart) {
			txtQRbcd.setEnabled(false);
			textQRversion.setEnabled(false);
			textQRcode.setEnabled(false);
			textQRsct.setEnabled(false);
			txtQRbic.setEnabled(false);
			txtQRki.setEnabled(false);
			txtQRiban.setEnabled(false);
			txtQReursum.setEnabled(false);
			textQRzweck.setEditable(false);
			textQRzweck.setEnabled(false);
			textQRref.setEnabled(false);
			textQRtext.setEditable(false);
			textQRtext.setEnabled(false);
			textQRanzeige.setEditable(false);
			textQRanzeige.setEnabled(false);
		} else {
			txtQRbcd.setEnabled(true);
			textQRversion.setEnabled(true);
			textQRcode.setEnabled(true);
			textQRsct.setEnabled(true);
			txtQRbic.setEnabled(true);
			txtQRki.setEnabled(true);
			txtQRiban.setEnabled(true);
			txtQReursum.setEnabled(true);
			textQRzweck.setEnabled(true);
			textQRref.setEnabled(true);
			textQRtext.setEnabled(true);
			textQRanzeige.setEnabled(true);
			btnQROK.setEnabled(true);
			btnQREdit.setEnabled(false);
		}

		btnQREdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtQRbcd.setEnabled(true);
				textQRversion.setEnabled(true);
				textQRcode.setEnabled(true);
				textQRsct.setEnabled(true);
				txtQRbic.setEnabled(true);
				txtQRki.setEnabled(true);
				txtQRiban.setEnabled(true);
				txtQReursum.setEnabled(true);
				textQRzweck.setEnabled(true);
				textQRref.setEnabled(true);
				textQRtext.setEnabled(true);
				textQRanzeige.setEnabled(true);
				btnQROK.setEnabled(true);
				btnQREdit.setEnabled(false);
			}
		});

		btnQROK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoadData.setStrQRschema(txtQRbcd.getText() + "/" + textQRversion.getText() + "/" + textQRcode.getText()
				+ "/" + textQRsct.getText() + "/" + txtQRbic.getText() + "/" + txtQRki.getText() + "/"
				+ txtQRiban.getText() + "/" + txtQReursum.getText() + "/" + textQRzweck.getText() + "/"
				+ textQRref.getText() + "/" + textQRtext.getText() + "/" + textQRanzeige.getText());

				LoadData.setPrpAppSettings("qrschema", LoadData.getStrQRschema());

				try {
					Tools.saveSettingsApp(LoadData.getPrpAppSettings());
				} catch (IOException e1) {
					logger.error("JFsettings() - " + e1);
				}
				txtQRbcd.setEnabled(false);
				textQRversion.setEnabled(false);
				textQRcode.setEnabled(false);
				textQRsct.setEnabled(false);
				txtQRbic.setEnabled(false);
				txtQRki.setEnabled(false);
				txtQRiban.setEnabled(false);
				txtQReursum.setEnabled(false);
				textQRzweck.setEnabled(false);
				textQRref.setEnabled(false);
				textQRtext.setEnabled(false);
				textQRanzeige.setEnabled(false);
				btnQROK.setEnabled(false);
				btnQREdit.setEnabled(true);

				LoadData.setbFinished(true);
				dispose();
			}
		});

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

}
