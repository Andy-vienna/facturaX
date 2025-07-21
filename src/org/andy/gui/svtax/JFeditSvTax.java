package org.andy.gui.svtax;

import static org.andy.toolbox.misc.CreateObject.changeKomma;
import static org.andy.toolbox.misc.CreateObject.createButton;
import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.choosePath;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
import static org.andy.toolbox.sql.Read.sqlExtractFile;
import static org.andy.toolbox.sql.Read.sqlReadArray;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import org.andy.code.main.overview.LoadSvTax;
import org.andy.gui.file.JFfileView;
import org.andy.gui.misc.RoundedBorder;

public class JFeditSvTax extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFnewSvTax.class);

	private static final long serialVersionUID = 1L;
	private static final String TBL_SVTAX = "tbl_svtax";

	private static String sId = null;
	@SuppressWarnings("unused")
	private static String sEDatum;
	private static String sZZDatum;
	private static int iPayed;
	private static String sConn;
	@SuppressWarnings("unused")
	private static String FilePath;

	private JPanel contentPane;
	private static JLabel lblFileTyp;
	private static DatePicker dateEingang = new DatePicker();
	private static JTextField txtItem2;
	private static JTextField txtItem3;
	private static JTextField txtItem4;
	private static DatePicker dateZahlZiel = new DatePicker();
	private static JTextField txtItem5;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI(String sID) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFeditSvTax frame = new JFeditSvTax(sID);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFeditSvTax(String sID) {

		try (InputStream is = JFeditSvTax.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setTitle("Eingangsrechnung bearbeiten");
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFeditSvTax.class.getResource("/main/resources/icons/edit_color.png")));
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

		lblFileTyp = new JLabel();
		lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(140, 205, 50, 40);
		contentPane.add(lblFileTyp);

		DemoPanel panelDateE = new DemoPanel();
		panelDateE.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettingsE = new DatePickerSettings();
		dateSettingsE.setWeekNumbersDisplayed(true, true);
		dateSettingsE.setFormatForDatesCommonEra("dd.MM.yyyy");
		dateEingang = new DatePicker(dateSettingsE);
		dateEingang.getComponentToggleCalendarButton().setEnabled(false);
		dateEingang.getComponentDateTextField().setEnabled(false);
		dateEingang.getComponentDateTextField().setEditable(false);
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
		txtItem2.setEditable(false);
		txtItem2.setColumns(10);
		txtItem2.setBounds(140, 45, 500, 25);
		contentPane.add(txtItem2);

		txtItem3 = new JTextField();
		txtItem3.setEditable(false);
		txtItem3.setColumns(10);
		txtItem3.setBounds(140, 70, 500, 25);
		contentPane.add(txtItem3);

		txtItem4 = new JTextField();
		txtItem4.setEditable(false);
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

		JCheckBox chkMoney = new JCheckBox("Rechnung bezahlt");
		chkMoney.setForeground(Color.BLUE);
		chkMoney.setFont(new Font("Tahoma", Font.BOLD, 11));
		chkMoney.setBounds(364, 220, 140, 23);
		chkMoney.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					iPayed = 1;
				} else {
					iPayed = 0;
				};
			}
		});
		contentPane.add(chkMoney);

		JButton btnOK = null;
		try {
			btnOK = createButton("OK", "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnOK.setBounds(510, 220, 130, 50);
		btnOK.setEnabled(true);

		JButton btnSelect = new JButton("...");
		btnSelect.setEnabled(false);
		btnSelect.setToolTipText("");
		btnSelect.setIconTextGap(10);
		btnSelect.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnSelect.setBounds(80, 175, 60, 25);

		//###################################################################################################################################################
		//###################################################################################################################################################

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String arrResult[][] = new String[2][9];
		Arrays.fill(arrResult, null);
		String tblName = TBL_SVTAX.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "SELECT * FROM " + tblName + " WHERE [Id] LIKE '%" + sID + "%' ORDER BY [Id]"; //SQL Befehlszeile

		try {
			arrResult = sqlReadArray(sConn, sSQLStatement);

			sId = arrResult[1][1];

			LocalDate dateE = LocalDate.parse(arrResult[1][2], formatter);
			dateEingang.setDate(dateE); // Eingangsdatum

			txtItem2.setText(arrResult[1][3]); // Orhganisation
			txtItem3.setText(arrResult[1][4]); // Bezeichnung

			txtItem4.setText(formatValue(arrResult[1][5])); // Zahllast

			LocalDate dateZ = LocalDate.parse(arrResult[1][6], formatter);
			dateZahlZiel.setDate(dateZ);

			txtItem5.setText(arrResult[1][7]); // Dateiname

			if(arrResult[1][9].equals("1")) {
				chkMoney.setSelected(true);
			} else {
				chkMoney.setSelected(false);
			}
		} catch (ClassNotFoundException | SQLException e1) {
			logger.error("error reading data fron db - " + e1);
		}

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

		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					actionMouseClick(e, sID);
				}
			}
		});

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				writeUpdateREe();
				LoadSvTax.loadSvTax(false, null);
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

		setIcon();

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

	private static void setIcon() {
		try {
			JFfileView.setFileIcon(lblFileTyp, txtItem5.getText());
			lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}

	private static void writeUpdateREe() {

		String tblName = TBL_SVTAX.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "UPDATE " + tblName + " SET [zahlungsziel] = '" + sZZDatum + "',[status] = " + iPayed + " WHERE [Id] = '" + sId + "'";

		try {
			sqlUpdate(sConn, sSQLStatement);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error updating incoming invoice - " + e);
		}

	}

	private static String formatValue(String value) {
		DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
		BigDecimal bdtmp = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
		return df.format(bdtmp);
	}

	private void actionMouseClick(MouseEvent e, String sId) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern
			try {

				String outputPath = choosePath(LoadData.getWorkPath());
				if (outputPath.equals(getNotSelected())) {
					return;
				}

				String tblName = TBL_SVTAX.replace("_", LoadData.getStrAktGJ());
				String sSQLStatement = "SELECT [dateiname], [datei] FROM " + tblName + " WHERE [Id] = '" + sId + "'";

				sqlExtractFile(sConn, sSQLStatement, outputPath, "dateiname", "datei");

			} catch (SQLException | IOException | InterruptedException | ClassNotFoundException e1) {
				Thread.currentThread().interrupt();
				logger.error("actionMouseClick(MouseEvent e, String sId) - " + e1);
			}
		}
	}

	public static void setsConn(String sConn) {
		JFeditSvTax.sConn = sConn;
	}
}
