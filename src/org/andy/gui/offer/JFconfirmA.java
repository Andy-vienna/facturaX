package org.andy.gui.offer;

import static main.java.toolbox.misc.CreateObject.createButton;
import static main.java.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

import org.andy.code.dataExport.ExcelConfirmation;
import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;
import org.andy.org.eclipse.wb.swing.FocusTraversalOnArray;

public class JFconfirmA extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(JFconfirmA.class);

	private static String sConn;
	private static final String TBL_OFFER = "tbl_an";

	private JPanel contentPanel = new JPanel();

	private static JTextField txtConfNr;

	private static String sConfNr = null;
	private static String sConfDatum = null;
	private static String sConfStart = null;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void showDialog(String vZelleA) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFconfirmA frame = new JFconfirmA(vZelleA);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("showDialog fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFconfirmA(String vZelleA) {

		try (InputStream is = JFconfirmA.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Dateneingabe Auftragsbestätigung");
		setBounds(100, 100, 355, 188);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(null);

		JButton btnOK = null;
		try {
			btnOK = createButton("", "ok.png");
		} catch (RuntimeException e) {
			logger.error("error creating button - " + e);
		}
		btnOK.setEnabled(true);
		btnOK.setBounds(249, 90, 80, 50);

		JLabel lblNewLabel = new JLabel("Bestellnummer:");
		lblNewLabel.setBounds(10, 10, 120, 25);
		contentPanel.add(lblNewLabel);

		JLabel lblBestelldatum = new JLabel("Bestelldatum:");
		lblBestelldatum.setBounds(10, 35, 120, 25);
		contentPanel.add(lblBestelldatum);

		JLabel lblStartdatum = new JLabel("Startdatum:");
		lblStartdatum.setBounds(10, 60, 120, 25);
		contentPanel.add(lblStartdatum);

		txtConfNr = new JTextField();
		txtConfNr.setBounds(130, 10, 200, 25);
		contentPanel.add(txtConfNr);
		txtConfNr.setColumns(10);

		DemoPanel panelConfDatum = new DemoPanel();
		panelConfDatum.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateConfDatum = new DatePickerSettings();
		dateConfDatum.setWeekNumbersDisplayed(true, true);
		dateConfDatum.setFormatForDatesCommonEra("dd.MM.yyyy");
		DatePicker datePickerConfDatum = new DatePicker(dateConfDatum);
		datePickerConfDatum.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePickerConfDatum.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				String sDate = datePickerConfDatum.getDateStringOrEmptyString();
				if(sDate.length() > 9) {
					sConfDatum = sDate.substring(8, 10) + "." + sDate.substring(5, 7) + "." + sDate.substring(0, 4);
				}else {
					sConfDatum = null;
				}
			}
		});
		datePickerConfDatum.setBounds(132, 35, 200, 25);
		contentPanel.add(datePickerConfDatum);

		DemoPanel panelConfStart = new DemoPanel();
		panelConfStart.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateConfStart = new DatePickerSettings();
		dateConfStart.setWeekNumbersDisplayed(true, true);
		dateConfStart.setFormatForDatesCommonEra("dd.MM.yyyy");
		DatePicker datePickerConfStart = new DatePicker(dateConfStart);
		datePickerConfStart.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePickerConfStart.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				String sDate = datePickerConfStart.getDateStringOrEmptyString();
				if(sDate.length() > 9) {
					sConfStart = sDate.substring(8, 10) + "." + sDate.substring(5, 7) + "." + sDate.substring(0, 4);
				}else {
					sConfStart = null;
				}
			}
		});
		datePickerConfStart.setBounds(132, 60, 200, 25);
		contentPanel.add(datePickerConfStart);

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sConfNr = txtConfNr.getText();
				if(sConfNr == null || sConfDatum == null || sConfStart == null) {
					JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig ...", StartUp.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				try {
					ExcelConfirmation.abExport(vZelleA);
				} catch (IOException e1) {
					logger.error("JFconfirmA(String vZelleA) - " + e);
				} catch (Exception e1) {
					logger.error("JFconfirmA(String vZelleA) - " + e1);
				}

				String tblName = TBL_OFFER.replace("_", LoadData.getStrAktGJ());
				String sStatement = "UPDATE " + tblName + " SET [orderState] = '1', [Status] = '" + JFstatusA.getConfirmed() + "' WHERE [IdNummer] = '" + vZelleA + "'";

				try {
					sqlUpdate(sConn, sStatement);
				} catch (SQLException | ClassNotFoundException e2) {
					logger.error("error updating offer state to database - " + e2);
				}

				JFoverview.loadAngebot(false);
				dispose();
			}
		});
		contentPanel.add(btnOK);

		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{txtConfNr, datePickerConfDatum, datePickerConfStart, btnOK}));
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static String getsConfNr() {
		return sConfNr;
	}

	public static String getsConfDatum() {
		return sConfDatum;
	}

	public static String getsConfStart() {
		return sConfStart;
	}

	public static void setsConn(String sConn) {
		JFconfirmA.sConn = sConn;
	}
}
