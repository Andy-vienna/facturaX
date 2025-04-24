package org.andy.gui.bill.in;

import static main.java.toolbox.misc.CreateObject.changeKomma;
import static main.java.toolbox.misc.CreateObject.createButton;
import static main.java.toolbox.misc.SelectFile.chooseFile;
import static main.java.toolbox.misc.SelectFile.choosePath;
import static main.java.toolbox.misc.SelectFile.getNotSelected;
import static main.java.toolbox.sql.Read.sqlExtractFile;
import static main.java.toolbox.sql.Read.sqlReadArray;
import static main.java.toolbox.sql.Update.sqlUpdate;

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
import org.andy.gui.file.JFfileView;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;


public class JFeditRe extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFeditRe.class);

	private static final long serialVersionUID = 1L;
	private static final String TBL_BILL_IN = "tbl_reIN";
	private static String sZZDatum;
	private static int iPayed = 0;
	private static String sConn;
	@SuppressWarnings("unused")
	private static String FilePath;

	private JPanel contentPane;

	private static JLabel lblFileTyp;

	private static JTextField txtItem1;
	private static JTextField txtItem2;
	private static JTextField txtItem3;
	private static JTextField txtItem4;
	private static JTextField txtItem5;
	private static JTextField txtItem6;
	private static JTextField txtItem7;
	private static JTextField txtItem8;
	private static JTextField txtItem9;
	private static JTextField txtItem10;
	private static JTextField txtItem11;
	private static JTextField txtItem12;
	private static JTextField txtItem13;
	private static JTextField txtItem14;
	private static DatePicker dateZahlZiel = new DatePicker();
	private static JTextField txtItem16;
	private static JTextField txtItem17;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI(String sID) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFeditRe frame = new JFeditRe(sID);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFeditRe(String sID) {

		try (InputStream is = JFeditRe.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setTitle("Eingangsrechnung bearbeiten");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 665, 610);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblItem1 = new JLabel("Rechnungsnummer");
		lblItem1.setBounds(10, 10, 130, 25);
		contentPane.add(lblItem1);

		JLabel lblItem2 = new JLabel("Rechnungsdatum");
		lblItem2.setBounds(10, 35, 130, 25);
		contentPane.add(lblItem2);

		JLabel lblItem3 = new JLabel("Kreditor Name");
		lblItem3.setBounds(10, 70, 130, 25);
		contentPane.add(lblItem3);

		JLabel lblItem4 = new JLabel("Kreditor Straße");
		lblItem4.setBounds(10, 95, 130, 25);
		contentPane.add(lblItem4);

		JLabel lblItem5 = new JLabel("Kreditor PLZ");
		lblItem5.setBounds(10, 120, 130, 25);
		contentPane.add(lblItem5);

		JLabel lblItem6 = new JLabel("Kreditor Ort");
		lblItem6.setBounds(10, 145, 130, 25);
		contentPane.add(lblItem6);

		JLabel lblItem7 = new JLabel("Kreditor Ländercode");
		lblItem7.setBounds(10, 170, 130, 25);
		contentPane.add(lblItem7);

		JLabel lblItem8 = new JLabel("Kreditor UID");
		lblItem8.setBounds(10, 195, 130, 25);
		contentPane.add(lblItem8);

		JLabel lblItem9 = new JLabel("Währung");
		lblItem9.setBounds(10, 230, 130, 25);
		contentPane.add(lblItem9);

		JLabel lblItem10 = new JLabel("USt. Satz");
		lblItem10.setBounds(10, 255, 130, 25);
		contentPane.add(lblItem10);

		JLabel lblItem11 = new JLabel("Netto");
		lblItem11.setBounds(10, 290, 130, 25);
		contentPane.add(lblItem11);

		JLabel lblItem12 = new JLabel("Anzahlung");
		lblItem12.setBounds(10, 315, 130, 25);
		contentPane.add(lblItem12);

		JLabel lblItem13 = new JLabel("USt.");
		lblItem13.setBounds(10, 340, 130, 25);
		contentPane.add(lblItem13);

		JLabel lblItem14 = new JLabel("Brutto");
		lblItem14.setBounds(10, 365, 130, 25);
		contentPane.add(lblItem14);

		JLabel lblItem15 = new JLabel("Zahlungsziel");
		lblItem15.setBounds(10, 400, 130, 25);
		contentPane.add(lblItem15);

		JLabel lblItem16 = new JLabel("Zahlungshinweis");
		lblItem16.setBounds(10, 425, 130, 25);
		contentPane.add(lblItem16);

		JLabel lblItem17 = new JLabel("Dateianhang:");
		lblItem17.setBounds(10, 460, 70, 25);
		contentPane.add(lblItem17);

		lblFileTyp = new JLabel();
		lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(140, 493, 50, 40);
		contentPane.add(lblFileTyp);

		txtItem1 = new JTextField();
		txtItem1.setEditable(false);
		txtItem1.setBounds(140, 10, 500, 25);
		contentPane.add(txtItem1);
		txtItem1.setColumns(10);

		txtItem2 = new JTextField();
		txtItem2.setEditable(false);
		txtItem2.setBounds(140, 35, 180, 25);
		contentPane.add(txtItem2);
		txtItem2.setColumns(10);

		txtItem3 = new JTextField();
		txtItem3.setEditable(false);
		txtItem3.setColumns(10);
		txtItem3.setBounds(140, 70, 500, 25);
		contentPane.add(txtItem3);

		txtItem4 = new JTextField();
		txtItem4.setEditable(false);
		txtItem4.setColumns(10);
		txtItem4.setBounds(140, 95, 500, 25);
		contentPane.add(txtItem4);

		txtItem5 = new JTextField();
		txtItem5.setEditable(false);
		txtItem5.setColumns(10);
		txtItem5.setBounds(140, 120, 500, 25);
		contentPane.add(txtItem5);

		txtItem6 = new JTextField();
		txtItem6.setEditable(false);
		txtItem6.setColumns(10);
		txtItem6.setBounds(140, 145, 500, 25);
		contentPane.add(txtItem6);

		txtItem7 = new JTextField();
		txtItem7.setEditable(false);
		txtItem7.setColumns(10);
		txtItem7.setBounds(140, 170, 500, 25);
		contentPane.add(txtItem7);

		txtItem8 = new JTextField();
		txtItem8.setEditable(false);
		txtItem8.setColumns(10);
		txtItem8.setBounds(140, 195, 500, 25);
		contentPane.add(txtItem8);

		txtItem9 = new JTextField();
		txtItem9.setEditable(false);
		txtItem9.setColumns(10);
		txtItem9.setBounds(140, 230, 500, 25);
		contentPane.add(txtItem9);

		txtItem10 = new JTextField();
		txtItem10.setEditable(false);
		txtItem10.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem10.setText(changeKomma(txtItem10)));
			}
		});
		txtItem10.setColumns(10);
		txtItem10.setBounds(140, 255, 500, 25);
		contentPane.add(txtItem10);

		txtItem11 = new JTextField();
		txtItem11.setEditable(false);
		txtItem11.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem11.setText(changeKomma(txtItem11)));
			}
		});
		txtItem11.setColumns(10);
		txtItem11.setBounds(140, 290, 500, 25);
		contentPane.add(txtItem11);

		txtItem12 = new JTextField();
		txtItem12.setEditable(false);
		txtItem12.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem12.setText(changeKomma(txtItem12)));
			}
		});
		txtItem12.setColumns(10);
		txtItem12.setBounds(140, 315, 500, 25);
		contentPane.add(txtItem12);

		txtItem13 = new JTextField();
		txtItem13.setEditable(false);
		txtItem13.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem13.setText(changeKomma(txtItem13)));
			}
		});
		txtItem13.setColumns(10);
		txtItem13.setBounds(140, 340, 500, 25);
		contentPane.add(txtItem13);

		txtItem14 = new JTextField();
		txtItem14.setEditable(false);
		txtItem14.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txtItem14.setText(changeKomma(txtItem14)));
			}
		});
		txtItem14.setColumns(10);
		txtItem14.setBounds(140, 365, 500, 25);
		contentPane.add(txtItem14);

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
		dateZahlZiel.setBounds(142, 400, 180, 25);
		contentPane.add(dateZahlZiel);

		txtItem16 = new JTextField();
		txtItem16.setColumns(10);
		txtItem16.setBounds(140, 425, 500, 25);
		contentPane.add(txtItem16);

		txtItem17 = new JTextField("------------");
		txtItem17.setEditable(false);
		txtItem17.setBounds(140, 460, 500, 25);
		contentPane.add(txtItem17);

		JCheckBox chkMoney = new JCheckBox("Rechnung bezahlt");
		chkMoney.setForeground(Color.BLUE);
		chkMoney.setFont(new Font("Tahoma", Font.BOLD, 11));
		chkMoney.setBounds(360, 510, 140, 23);
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

		JButton btnOK = null, btnSelect = null;
		try {
			btnOK = createButton("update", "update.png");
			btnSelect = createButton("...", null);
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnOK.setBounds(510, 510, 130, 50);
		btnOK.setEnabled(true);

		btnSelect.setBounds(80, 460, 60, 25);

		//###################################################################################################################################################
		//###################################################################################################################################################

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String arrResult[][] = new String[2][20];
		Arrays.fill(arrResult, null);
		String tblName = TBL_BILL_IN.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "SELECT * FROM " + tblName + " WHERE [Id] LIKE '%" + sID + "%' ORDER BY [Id]"; //SQL Befehlszeile

		try {
			arrResult = sqlReadArray(sConn, sSQLStatement);

			txtItem1.setText(arrResult[1][1]); // Rechnungsnummer

			LocalDate dateR = LocalDate.parse(arrResult[1][2], formatter);
			txtItem2.setText(dateR.toString()); // Rechnungsdatum

			txtItem3.setText(arrResult[1][3]); // Rechnungssteller Name
			txtItem4.setText(arrResult[1][4]); // Anschrift Zeile 1
			txtItem5.setText(arrResult[1][5]); // Anschrift PLZ
			txtItem6.setText(arrResult[1][6]); // Anschrift Ort
			txtItem7.setText(arrResult[1][7]); // Länderkennung
			txtItem8.setText(arrResult[1][8]); // Rechnungsempfänger USt. ID
			txtItem9.setText(arrResult[1][9]); // Währungskennung
			txtItem10.setText(formatValue(arrResult[1][10])); // Steuersatz
			txtItem11.setText(formatValue(arrResult[1][11])); // Rechnungssumme netto
			txtItem12.setText(formatValue(arrResult[1][12])); // Bezahlter Teilbetrag
			txtItem13.setText(formatValue(arrResult[1][13])); // Rechnungssumme enthaltende USt.
			txtItem14.setText(formatValue(arrResult[1][14])); // Rechnungssumme brutto

			LocalDate dateZ = LocalDate.parse(arrResult[1][15], formatter);
			dateZahlZiel.setDate(dateZ);

			txtItem16.setText(arrResult[1][16]); // Zahlungshinweis

			txtItem17.setText(arrResult[1][17]);

			if(arrResult[1][19].equals("1")) {
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
				JFoverview.loadEingangsRechnung(false);
				dispose();
			}
		});

		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fName = selectFile();
				txtItem17.setText(fName);
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
			JFfileView.setFileIcon(lblFileTyp, txtItem17.getText());
			lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}

	private static void writeUpdateREe() {

		String tblName = TBL_BILL_IN.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "UPDATE " + tblName + " SET [zahlungsziel] = '" + sZZDatum + "',[hinweis] = '" + txtItem16.getText() + "',[status] = " + iPayed + " WHERE [Id] = '" + txtItem1.getText() + "'";

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

				String tblName = TBL_BILL_IN.replace("_", LoadData.getStrAktGJ());
				String sSQLStatement = "SELECT [dateiname], [datei] FROM " + tblName + " WHERE [Id] = '" + sId + "'";

				sqlExtractFile(sConn, sSQLStatement, outputPath, "dateiname", "datei");

			} catch (SQLException | IOException | InterruptedException | ClassNotFoundException e1) {
				Thread.currentThread().interrupt();
				logger.error("actionMouseClick(MouseEvent e, String sId) - " + e1);
			}
		}
	}

	public static void setsConn(String sConn) {
		JFeditRe.sConn = sConn;
	}
}
