package org.andy.gui.offer;

import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.gui.main.JFoverview;

public class JFstatusA extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(JFstatusA.class);

	private static String sConn;
	private static final String TBL_OFFER = "tbl_an";

	private static final String NOTACTIVE = "storniert";
	private static final String WRITTEN = "erstellt";
	private static final String PRINTED = "gedruckt";
	private static final String ORDERED = "bestellt";
	private static final String CONFIRMED = "bestellt und bestätigt";

	private JPanel contentPanel = new JPanel();
	private JTextField textAnNr;
	private JTextField textStatus;
	private static String bCheckedActive;
	private static String bCheckedOrder;
	private static String bCheckedPrint;
	private static String sText;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void showDialog(String sAnNr, String sStatus) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFstatusA frame = new JFstatusA(sAnNr, sStatus);
					frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("showDialog fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFstatusA(String sAnNr, String sStatus) {

		try (InputStream is = JFstatusA.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				JFoverview.loadAngebot(false);
			}
		});
		setBounds(100, 100, 401, 141);
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFstatusA.class.getResource("/main/resources/icons/edit_color.png")));
		setTitle(StartUp.APP_NAME + StartUp.APP_VERSION);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(null);

		JButton okButton = null;
		try {
			okButton = createButton("OK", "ok.png");
		} catch (RuntimeException e) {
			logger.error("error creating button - " + e);
		}
		okButton.setEnabled(true);
		okButton.setBounds(236, 37, 140, 55);

		JCheckBox chkStorno = new JCheckBox("Angebot stornieren");
		JCheckBox chkOrder = new JCheckBox("Angebot ist bestellt");

		JLabel lblNewLabel = new JLabel("bearbeiten:");
		lblNewLabel.setBounds(10, 10, 70, 20);
		contentPanel.add(lblNewLabel);

		textAnNr = new JTextField(sAnNr);
		textAnNr.setEditable(false);
		textAnNr.setFont(new Font("Tahoma", Font.BOLD, 11));
		textAnNr.setHorizontalAlignment(SwingConstants.CENTER);
		textAnNr.setBounds(80, 10, 120, 20);
		contentPanel.add(textAnNr);
		textAnNr.setColumns(10);

		textStatus = new JTextField();
		textStatus.setEditable(false);
		textStatus.setHorizontalAlignment(SwingConstants.CENTER);
		textStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		textStatus.setBounds(200, 10, 90, 20);
		contentPanel.add(textStatus);
		switch(sStatus) {
		case WRITTEN:
			textStatus.setText(WRITTEN);
			textStatus.setBackground(Color.WHITE);
			textStatus.setForeground(Color.BLACK);
			bCheckedActive = "1";
			bCheckedPrint = "0";
			bCheckedOrder = "0";
			chkStorno.setSelected(false);
			chkStorno.setEnabled(true);
			chkOrder.setSelected(false);
			chkOrder.setEnabled(false);
			break;
		case NOTACTIVE:
			textStatus.setText(NOTACTIVE);
			textStatus.setBackground(Color.PINK);
			textStatus.setForeground(Color.BLACK);
			bCheckedActive = "0";
			bCheckedPrint = "0";
			bCheckedOrder = "0";
			chkStorno.setSelected(true);
			chkStorno.setEnabled(true);
			chkOrder.setSelected(false);
			chkOrder.setEnabled(false);
			break;
		case PRINTED:
			textStatus.setText(PRINTED);
			textStatus.setBackground(Color.BLUE);
			textStatus.setForeground(Color.WHITE);
			bCheckedActive = "1";
			bCheckedPrint = "1";
			bCheckedOrder = "0";
			chkStorno.setSelected(false);
			chkStorno.setEnabled(true);
			chkOrder.setSelected(false);
			chkOrder.setEnabled(true);
			break;
		case ORDERED:
			textStatus.setText(ORDERED);
			textStatus.setBackground(Color.GREEN);
			textStatus.setForeground(Color.BLACK);
			bCheckedActive = "1";
			bCheckedPrint = "1";
			bCheckedOrder = "1";
			chkStorno.setSelected(false);
			chkStorno.setEnabled(false);
			chkOrder.setSelected(true);
			chkOrder.setEnabled(true);
			break;
		}
		textStatus.setColumns(10);

		chkStorno.setBounds(80, 37, 150, 23);
		chkStorno.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					bCheckedActive = "0";
					bCheckedPrint = "0";
					bCheckedOrder = "0";
					sText = NOTACTIVE;
					chkOrder.setEnabled(false);
				} else {
					bCheckedActive = "1";
					bCheckedPrint = "0";
					bCheckedOrder = "0";
					sText = WRITTEN;
					chkOrder.setEnabled(true);
				};
			}
		});
		contentPanel.add(chkStorno);

		chkOrder.setBounds(80, 63, 150, 23);
		chkOrder.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					bCheckedActive = "1";
					bCheckedPrint = "1";
					bCheckedOrder = "1";
					sText = ORDERED;
					chkStorno.setEnabled(false);
				} else {
					bCheckedActive = "1";
					bCheckedPrint = "1";
					bCheckedOrder = "0";
					sText = PRINTED;
					chkStorno.setEnabled(true);
				};
			}
		});
		contentPanel.add(chkOrder);

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String tblName = TBL_OFFER.replace("_", LoadData.getStrAktGJ());
				String sStatement = "UPDATE " + tblName + " SET [activeState] = '" + bCheckedActive + "', [printState] = '" + bCheckedPrint
						+ "', [orderState] = '" + bCheckedOrder	+ "', [Status] = '" + sText + "' WHERE [IdNummer] = '" + sAnNr + "'";

				try {
					sqlUpdate(sConn, sStatement);
				} catch (SQLException | ClassNotFoundException e1) {
					logger.error("error updating offer state to database - " + e1);
				}

				JFoverview.loadAngebot(false);
				dispose(); // Dialog-Fenster loswerden ...
			}
		});
		contentPanel.add(okButton);
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static final String getNotactive() {
		return NOTACTIVE;
	}


	public static final String getWritten() {
		return WRITTEN;
	}


	public static final String getPrinted() {
		return PRINTED;
	}


	public static final String getOrdered() {
		return ORDERED;
	}


	public static final String getConfirmed() {
		return CONFIRMED;
	}

	public static void setsConn(String sConn) {
		JFstatusA.sConn = sConn;
	}



}
