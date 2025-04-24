package org.andy.gui.expenses;

import static main.java.toolbox.misc.CreateObject.changeKomma;
import static main.java.toolbox.misc.CreateObject.createButton;
import static main.java.toolbox.misc.SelectFile.chooseFile;
import static main.java.toolbox.misc.SelectFile.getNotSelected;
import static main.java.toolbox.sql.Insert.sqlInsert;

import java.awt.Color;
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

public class JFnewEx extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFnewEx.class);
	private static final long serialVersionUID = 1L;
	private static final String TBL_EXPENSES = "tbl_expenses";
	private static final String OK = "OK";
	private static final String NOK = "NOK";

	private static String sConn;
	private static String sDatum;
	private static String FilePath;

	private JPanel contentPane;
	private JLabel lbl01 = new JLabel("neuen Beleg anlegen");
	private JLabel lbl02 = new JLabel("Belegdatum:");
	private JLabel lbl03 = new JLabel("Buchungstext des Beleges:");
	private JLabel lbl04 = new JLabel("Betrag netto (EUR):");
	private JLabel lbl05 = new JLabel("Steuersatz (%):");
	private JLabel lbl06 = new JLabel("Betrag brutto (EUR):");
	private JLabel lbl07 = new JLabel("Dateianhang:");

	private static JTextField txt02 = new JTextField();
	private static JTextField txt03 = new JTextField();
	private static JTextField txt04 = new JTextField();
	private static JTextField txt05 = new JTextField();
	private static JTextField txt06 = new JTextField(getNotSelected());

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFnewEx frame = new JFnewEx();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFnewEx() {

		try (InputStream is = JFnewEx.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setTitle(StartUp.APP_NAME + StartUp.APP_VERSION);
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFoverview.class.getResource("/main/resources/icons/edit_color.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 678, 284);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//###################################################################################################################################################
		//###################################################################################################################################################

		JButton btnSelect = new JButton("select");
		btnSelect.setToolTipText("");
		btnSelect.setBounds(85, 165, 65, 25);

		JButton btnSave = null;
		try {
			btnSave = createButton("<html>speichern</html>", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnSave.setEnabled(true);
		btnSave.setBounds(520, 190, JFoverview.getButtonx(), JFoverview.getButtony());

		lbl01.setForeground(Color.BLUE);
		lbl01.setFont(new Font("Tahoma", Font.BOLD, 16));
		lbl01.setBounds(10, 10, 180, 25);
		lbl02.setBounds(10, 40, 150, 25);
		lbl03.setBounds(10, 65, 150, 25);
		lbl04.setBounds(10, 90, 150, 25);
		lbl05.setBounds(10, 115, 150, 25);
		lbl06.setBounds(10, 140, 150, 25);
		lbl07.setBounds(10, 165, 80, 25);

		DemoPanel panelDate = new DemoPanel();
		panelDate.scrollPaneForButtons.setEnabled(false);
		DatePickerSettings dateSettings = new DatePickerSettings();
		dateSettings.setWeekNumbersDisplayed(true, true);
		dateSettings.setFormatForDatesCommonEra("dd.MM.yyyy");
		DatePicker datePicker = new DatePicker(dateSettings);
		datePicker.getComponentDateTextField().setBorder(new RoundedBorder(10));
		datePicker.addDateChangeListener(new DateChangeListener() {
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				LocalDate selectedDate = datePicker.getDate();
				if (selectedDate != null) {
					sDatum = selectedDate.format(StartUp.getDfdate());
				} else {
					sDatum = null;
				}
			}
		});
		datePicker.setBounds(152, 40, 180, 25);

		//txt01.setBounds(150, 40, 180, 25);
		txt02.setBounds(150, 65, 500, 25);
		txt03.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txt03.setText(changeKomma(txt03)));
			}
		});
		txt03.setBounds(150, 90, 180, 25);
		txt04.setBounds(150, 115, 180, 25);
		txt05.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.invokeLater(() -> txt05.setText(changeKomma(txt05)));
			}
		});
		txt05.setBounds(150, 140, 180, 25);
		txt06.setEditable(false);
		txt06.setBounds(150, 165, 500, 25);

		contentPane.add(lbl01);
		contentPane.add(lbl02);
		contentPane.add(lbl03);
		contentPane.add(lbl04);
		contentPane.add(lbl05);
		contentPane.add(lbl06);
		contentPane.add(lbl07);

		contentPane.add(datePicker);

		contentPane.add(txt02);
		contentPane.add(txt03);
		contentPane.add(txt04);
		contentPane.add(txt05);
		contentPane.add(txt06);

		contentPane.add(btnSelect);
		contentPane.add(btnSave);

		//###################################################################################################################################################
		//###################################################################################################################################################

		txt02.setText("");
		txt03.setText("");
		txt04.setText("");
		txt05.setText("");
		txt06.setText(getNotSelected());

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

		// ------------------------------------------------------------------------------
		// Action Listener für Buttons
		// ------------------------------------------------------------------------------
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fName = selectFile();
				txt06.setText(fName);
			}
		});
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sResult = writeNewExpense();
				if(sResult.equals(OK)) {
					JFoverview.loadExpenses(false);
					dispose();
				}
			}
		});
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

	private static String writeNewExpense() {

		String[] arrTmp = new String[8];
		Arrays.fill(arrTmp, null);

		int iId = JFoverview.getAnzExpenses() + 1;

		arrTmp[0] = sDatum; //txt01.getText();
		arrTmp[1] = txt02.getText();
		arrTmp[2] = txt03.getText();
		arrTmp[3] = txt04.getText();
		arrTmp[4] = txt05.getText();
		arrTmp[5] = txt06.getText();
		arrTmp[6] = FilePath;
		arrTmp[7] = String.valueOf(iId);

		for(int x = 0; x < 8; x++) {
			if(arrTmp == null) {
				JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Beleg erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
				return NOK;
			}
		}

		String tblName = TBL_EXPENSES.replace("_", LoadData.getStrAktGJ());

		String sSQLStatement = "INSERT INTO " + tblName + " ([Datum],[Art],[netto],[Steuersatz],[brutto],[dateiname],[datei],[Id]) VALUES ('" + arrTmp[0] + "','" + arrTmp[1]
				+ "','" + arrTmp[2]  + "','" + arrTmp[3]  + "','" + arrTmp[4]  + "','" + arrTmp[5] + "',(SELECT * FROM OPENROWSET(BULK '" + arrTmp[6] + "', SINGLE_BLOB) AS DATA),'"
				+ arrTmp[7]  + "')";

		try {
			sqlInsert(sConn, sSQLStatement);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error writing new expenses - " + e);
		}
		return OK;
	}

	public static void setsConn(String sConn) {
		JFnewEx.sConn = sConn;
	}

}
