package org.andy.gui.settings;

import static main.java.toolbox.misc.Tools.saveSettingsApp;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.main.LoadData;
import main.java.toolbox.misc.SelectFile;
import main.java.toolbox.misc.SetFrameIcon;


public class JFpathMgmt extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFsepaQR.class);

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFpathMgmt frame = new JFpathMgmt();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("fatal error loading gui for editing data - " + e);
				}
			}
		});
	}

	public JFpathMgmt() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("folder.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Speicherort-Verwaltung");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 922, 236);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					saveSettingsApp(LoadData.getPrpAppSettings());
				} catch (IOException e1) {
					logger.error("error saving settings - " + e1);
				}
				LoadData.setbFinished(true);
				dispose();
			}
		});

		//###################################################################################################################################################
		//###################################################################################################################################################

		JLabel lbl01 = new JLabel("Angebot Vorlage (Excel-Vorlage *.xlsx)");
		JLabel lbl02 = new JLabel("Angebotsbestätigung Vorlage (Excel-Vorlage *.xlsx)");
		JLabel lbl03 = new JLabel("Rechnung Vorlage (Excel-Vorlage *.xlsx)");
		JLabel lbl04 = new JLabel("Zahlungserinnerung Vorlage (Excel-Vorlage *.xlsx)");
		JLabel lbl05 = new JLabel("Mahnung Vorlage (Excel-Vorlage *.xlsx");
		JLabel lbl06 = new JLabel("Arbeitsverzeichnis");
		JLabel lbl07 = new JLabel("Sicherungsverzeichnis");

		JTextField txtPathTplOf = new JTextField(LoadData.getTplOffer());
		JTextField txtPathTplConf = new JTextField(LoadData.getTplConfirmation());
		JTextField txtPathTplBi = new JTextField(LoadData.getTplBill());
		JTextField txtPathTplRem = new JTextField(LoadData.getTplReminder());
		JTextField txtPathTplMahnung = new JTextField(LoadData.getTplMahnung());
		JTextField txtPathWork = new JTextField(LoadData.getWorkPath());
		JTextField txtPathBackup = new JTextField(LoadData.getBackupPath());

		lbl01.setBounds(10, 10, 300, 25);
		lbl02.setBounds(10, 35, 300, 25);
		lbl03.setBounds(10, 60, 300, 25);
		lbl04.setBounds(10, 85, 300, 25);
		lbl05.setBounds(10, 110, 300, 25);
		lbl06.setBounds(10, 135, 300, 25);
		lbl07.setBounds(10, 160, 300, 25);

		txtPathTplOf.setBounds(310, 10, 585, 25);
		txtPathTplConf.setBounds(310, 35, 585, 25);
		txtPathTplBi.setBounds(310, 60, 585, 25);
		txtPathTplRem.setBounds(310, 85, 585, 25);
		txtPathTplMahnung.setBounds(310, 110, 585, 25);
		txtPathWork.setBounds(310, 135, 585, 25);
		txtPathBackup.setBounds(310, 160, 585, 25);

		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04);
		contentPane.add(lbl05);
		contentPane.add(lbl06);
		contentPane.add(lbl07);

		contentPane.add(txtPathTplOf);
		contentPane.add(txtPathTplConf);
		contentPane.add(txtPathTplBi);
		contentPane.add(txtPathTplRem);
		contentPane.add(txtPathTplMahnung);
		contentPane.add(txtPathWork);
		contentPane.add(txtPathBackup);

		txtPathTplOf.setEditable(false);
		txtPathTplConf.setEditable(false);
		txtPathTplBi.setEditable(false);
		txtPathTplRem.setEditable(false);
		txtPathTplMahnung.setEditable(false);
		txtPathWork.setEditable(false);
		txtPathBackup.setEditable(false);

		txtPathTplOf.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathTplOf.getText().isEmpty()) {
					LoadData.setTplOffer(SelectFile.chooseFile("C:\\"));
				} else {
					LoadData.setTplOffer(SelectFile.chooseFile(LoadData.getTplOffer()));
				}
				LoadData.setPrpAppSettings("templateoffer", LoadData.getTplOffer());
				txtPathTplOf.setText(LoadData.getTplOffer());
			}
		});

		txtPathTplConf.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathTplConf.getText().isEmpty()) {
					LoadData.setTplConfirmation(SelectFile.chooseFile("C:\\"));
				} else {
					LoadData.setTplConfirmation(SelectFile.chooseFile(LoadData.getTplConfirmation()));
				}
				LoadData.setPrpAppSettings("templateconfirmation", LoadData.getTplConfirmation());
				txtPathTplConf.setText(LoadData.getTplConfirmation());
			}
		});

		txtPathTplBi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathTplBi.getText().isEmpty()) {
					LoadData.setTplBill(SelectFile.chooseFile("C:\\"));
				} else {
					LoadData.setTplBill(SelectFile.chooseFile(LoadData.getTplBill()));
				}
				LoadData.setPrpAppSettings("templatebill", LoadData.getTplBill());
				txtPathTplBi.setText(LoadData.getTplBill());
			}
		});

		txtPathTplRem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathTplRem.getText().isEmpty()) {
					LoadData.setTplReminder(SelectFile.chooseFile("C:\\"));
				} else {
					LoadData.setTplReminder(SelectFile.chooseFile(LoadData.getTplReminder()));
				}
				LoadData.setPrpAppSettings("templatereminder", LoadData.getTplReminder());
				txtPathTplRem.setText(LoadData.getTplReminder());
			}
		});

		txtPathTplMahnung.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathTplMahnung.getText().isEmpty()) {
					LoadData.setTplMahnung(SelectFile.chooseFile("C:\\"));
				} else {
					LoadData.setTplMahnung(SelectFile.chooseFile(LoadData.getTplMahnung()));
				}
				LoadData.setPrpAppSettings("templatemahnung", LoadData.getTplMahnung());
				txtPathTplMahnung.setText(LoadData.getTplMahnung());
			}
		});

		txtPathWork.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathWork.getText().isEmpty()) {
					LoadData.setWorkPath(SelectFile.choosePath("C:\\"));
				} else {
					LoadData.setWorkPath(SelectFile.choosePath(LoadData.getWorkPath()));
				}
				LoadData.setPrpAppSettings("work", LoadData.getWorkPath());
				txtPathWork.setText(LoadData.getWorkPath());
			}
		});

		txtPathBackup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtPathBackup.getText().isEmpty()) {
					LoadData.setBackupPath(SelectFile.choosePath("C:\\"));
				} else {
					LoadData.setBackupPath(SelectFile.choosePath(LoadData.getBackupPath()));
				}
				LoadData.setPrpAppSettings("backup", LoadData.getBackupPath());
				txtPathBackup.setText(LoadData.getBackupPath());

			}
		});

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

}
