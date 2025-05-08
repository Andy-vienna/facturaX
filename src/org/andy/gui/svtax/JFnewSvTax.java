package org.andy.gui.svtax;

import static org.andy.toolbox.misc.CreateObject.changeKomma;
import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
import static org.andy.toolbox.sql.Insert.sqlInsert;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;

public class JFnewSvTax extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFnewSvTax.class);

	private static final long serialVersionUID = 1L;
	private static final String TBL_SVTAX = "tbl_svtax";

	private static String sEDatum;
	private static String sZZDatum;
	private static String sConn;
	private static String FilePath;

	private JPanel contentPane;
	private static DatePicker dateEingang = new DatePicker();
	private static JTextField txtItem2;
	private static JTextField txtItem3;
	private static JTextField txtItem4;
	private static DatePicker dateZahlZiel = new DatePicker();
	private static JTextField txtItem5;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFnewSvTax frame = new JFnewSvTax();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFnewSvTax() {

		try (InputStream is = JFnewSvTax.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setTitle("neue Zahlung anlegen");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFnewSvTax.class.getResource("/main/resources/icons/edit_color.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 665, 319);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblItem1 = new JLabel("Eingangsdatum");
		lblItem1.setBounds(10, 10, 130, 25);
		contentPane.add(lblItem1);

		JLabel lblItem2 = new JLabel("Organisation");
		lblItem2.setBounds(10, 45, 130, 25);
		contentPane.add(lblItem2);

		JLabel lblItem3 = new JLabel("Bezeichnung");
		lblItem3.setBounds(10, 70, 130, 25);
		contentPane.add(lblItem3);

		JLabel lblItem4 = new JLabel("Zahllast");
		lblItem4.setBounds(10, 105, 130, 25);
		contentPane.add(lblItem4);

		JLabel lblItem5 = new JLabel("Zahlungsziel");
		lblItem5.setBounds(10, 140, 130, 25);
		contentPane.add(lblItem5);

		JLabel lblItem6 = new JLabel("Dateianhang:");
		lblItem6.setBounds(10, 175, 70, 25);
		contentPane.add(lblItem6);

		DemoPanel panelDateE = new DemoPanel();
		panelDateE.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsE = new DatePickerSettings();
		dateSettingsE.setWeekNumbersDisplayed(true, true);
		dateSettingsE.setFormatForDatesCommonEra("dd.MM.yyyy");
		dateEingang = new DatePicker(dateSettingsE);
		dateEingang.getComponentDateTextField().setBorder(new RoundedBorder(10));
		dateEingang.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = dateEingang.getDate();
				if (selectedDate != null) {
					sEDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sEDatum = null;
				}
			}
		});
		dateEingang.setBounds(142, 10, 180, 25);
		contentPane.add(dateEingang);

		txtItem2 = new JTextField();
		txtItem2.setColumns(10);
		txtItem2.setBounds(140, 45, 500, 25);
		contentPane.add(txtItem2);

		txtItem3 = new JTextField();
		txtItem3.setColumns(10);
		txtItem3.setBounds(140, 70, 500, 25);
		contentPane.add(txtItem3);

		txtItem4 = new JTextField();
		txtItem4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem4.setText(changeKomma(txtItem4)));
			}
		});
		txtItem4.setColumns(10);
		txtItem4.setBounds(140, 105, 500, 25);
		contentPane.add(txtItem4);

		DemoPanel panelDateZ = new DemoPanel();
		panelDateZ.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsZ = new DatePickerSettings();
		dateSettingsZ.setWeekNumbersDisplayed(true, true);
		dateSettingsZ.setFormatForDatesCommonEra("dd.MM.yyyy");
		dateZahlZiel = new DatePicker(dateSettingsZ);
		dateZahlZiel.getComponentDateTextField().setBorder(new RoundedBorder(10));
		dateZahlZiel.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = dateZahlZiel.getDate();
				if (selectedDate != null) {
					sZZDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sZZDatum = null;
				}
			}
		});
		dateZahlZiel.setBounds(142, 140, 180, 25);
		contentPane.add(dateZahlZiel);

		txtItem5 = new JTextField("------------");
		txtItem5.setEditable(false);
		txtItem5.setBounds(140, 175, 500, 25);
		contentPane.add(txtItem5);

		JButton btnOK = null;
		try {
			btnOK = createButton("OK", "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnOK.setBounds(510, 220, 130, 50);
		btnOK.setEnabled(true);

		JButton btnSelect = new JButton("...");
		btnSelect.setToolTipText("");
		btnSelect.setIconTextGap(10);
		btnSelect.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSelect.setBounds(80, 175, 60, 25);

		//###################################################################################################################################################
		//###################################################################################################################################################

		// ------------------------------------------------------------------------------
		// Action Listener für JFrame
		// ------------------------------------------------------------------------------
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				dispose();
			}
		});

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				writeNewSvTax();
				JFoverview.loadSvTax(false);
				dispose();
			}
		});

		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fName = selectFile();
				txtItem5.setText(fName);
			}
		});
		contentPane.add(btnOK);
		contentPane.add(btnSelect);

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private static String selectFile() {
		String FileNamePath = chooseFile(LoadData.getWorkPath());
		if(FileNamePath == getNotSelected()) {
			return getNotSelected();
		}
		File fn = new File(FileNamePath);
		FilePath = fn.getPath();
		String FileName = fn.getName();
		return FileName;
	}

	private static void writeNewSvTax() {

		BigDecimal Tmp01 = new BigDecimal("0.00");
		int Tmp02 = 0;
		String[] arrTmp = new String[19];
		Arrays.fill(arrTmp, null);

		try {
			int iId = JFoverview.getAnzSvTax() + 1;

			arrTmp[0] = String.valueOf(iId);
			arrTmp[1] = sEDatum;
			arrTmp[2] = txtItem2.getText();
			arrTmp[3] = txtItem3.getText();

			Tmp01 = new BigDecimal(txtItem4.getText());

			arrTmp[5] = sZZDatum;
			arrTmp[6] = txtItem5.getText();
			arrTmp[7] = FilePath;

			Tmp02 = 0;

			for(int x = 0; x < 8; x++) {
				if(arrTmp == null) {
					JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Zahlung erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Zahlung erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
			return;
		}


		String tblName = TBL_SVTAX.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "INSERT INTO " + tblName + " ([Id],[datum],[organisation],[bezeichnung],[zahllast],[zahlungsziel],[dateiname],[datei],[status]) VALUES ('"
				+ arrTmp[0] + "','" + arrTmp[1]	+ "','" + arrTmp[2] + "','" + arrTmp[3] + "'," + Tmp01 + ",'" + arrTmp[5] + "','" + arrTmp[6] + "',"
				+ "(SELECT * FROM OPENROWSET(BULK '" + arrTmp[7] + "', SINGLE_BLOB) AS DATA),"
				+ Tmp02 + ")";

		try {
			sqlInsert(sConn, sSQLStatement);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error writing new incoming invoice - " + e);
		}

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFnewSvTax.sConn = sConn;
	}

}
