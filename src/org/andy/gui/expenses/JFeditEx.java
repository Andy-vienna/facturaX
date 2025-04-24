package org.andy.gui.expenses;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import org.andy.code.misc.Wrapper;
import org.andy.gui.file.JFfileView;
import org.andy.gui.main.JFoverview;
import org.andy.gui.misc.RoundedBorder;

public class JFeditEx extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFeditEx.class);
	private static final long serialVersionUID = 1L;
	private static final String TBL_EXPENSES = "tbl_expenses";
	private static final String OK = "OK";
	private static final String NOK = "NOK";

	private static String sConn;
	private static String sDatum;
	private static String FilePath;
	
	private static String workID;

	private JPanel contentPane;
	private JLabel lbl01 = new JLabel("Beleg bearbeiten");
	private JLabel lbl02 = new JLabel("Belegdatum:");
	private JLabel lbl03 = new JLabel("Buchungstext des Beleges:");
	private JLabel lbl04 = new JLabel("Betrag netto (EUR):");
	private JLabel lbl05 = new JLabel("Steuersatz (%):");
	private JLabel lbl06 = new JLabel("Betrag brutto (EUR):");
	private JLabel lbl07 = new JLabel("Dateianhang:");

	private final static JLabel lblFileTyp = new JLabel();

	private static JTextField txt02 = new JTextField();
	private static JTextField txt03 = new JTextField();
	private static JTextField txt04 = new JTextField();
	private static JTextField txt05 = new JTextField();
	private static JTextField txt06 = new JTextField(getNotSelected());

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI(Wrapper<String> sId) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFeditEx frame = new JFeditEx(sId);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFeditEx(Wrapper<String> sId) {

		workID = sId.value;
		
		try (InputStream is = JFeditEx.class.getResourceAsStream("/icons/edit_color.png")) {
			if (is == null) {
				throw new RuntimeException("Icon nicht gefunden!");
			}
			setIconImage(ImageIO.read(is));
		} catch (IOException e) {
			logger.error("error loading resource icon - " + e);
		}

		setTitle(StartUp.APP_NAME + StartUp.APP_VERSION);
		//setIconImage(Toolkit.getDefaultToolkit().getImage(JFexEdit.class.getResource("/main/resources/icons/edit_color.png")));
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

		JButton btnUpdate = null;
		try {
			btnUpdate = createButton("<html>speichern</html>", "save.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnUpdate.setEnabled(true);
		btnUpdate.setBounds(520, 190, JFoverview.getButtonx(), JFoverview.getButtony());

		lbl01.setForeground(Color.BLUE);
		lbl01.setFont(new Font("Tahoma", Font.BOLD, 16));
		lbl01.setBounds(10, 10, 180, 25);
		lbl02.setBounds(10, 40, 150, 25);
		lbl03.setBounds(10, 65, 150, 25);
		lbl04.setBounds(10, 90, 150, 25);
		lbl05.setBounds(10, 115, 150, 25);
		lbl06.setBounds(10, 140, 150, 25);
		lbl07.setBounds(10, 165, 80, 25);

		lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(150, 194, 50, 40);

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

		contentPane.add(lblFileTyp);

		contentPane.add(datePicker);

		contentPane.add(txt02);
		contentPane.add(txt03);
		contentPane.add(txt04);
		contentPane.add(txt05);
		contentPane.add(txt06);

		contentPane.add(btnSelect);
		contentPane.add(btnUpdate);

		//###################################################################################################################################################
		//###################################################################################################################################################

		txt02.setText("");
		txt03.setText("");
		txt04.setText("");
		txt05.setText("");
		txt06.setText(getNotSelected());

		//###################################################################################################################################################
		//###################################################################################################################################################

		DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String arrResult[][] = new String[2][9];
		Arrays.fill(arrResult, null);
		String tblName = TBL_EXPENSES.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "SELECT * FROM " + tblName + " WHERE [Id] LIKE '%" + sId.value + "%' ORDER BY [Id]"; //SQL Befehlszeile

		try {
			arrResult = sqlReadArray(sConn, sSQLStatement);

			LocalDate datum = LocalDate.parse(arrResult[1][1], dfDate);
			datePicker.setDate(datum);

			txt02.setText(arrResult[1][2]);
			txt03.setText(arrResult[1][3]);
			txt04.setText(arrResult[1][4]);
			txt05.setText(arrResult[1][5]);

			if(arrResult[1][6].equals("")) {
				txt06.setText(getNotSelected());
			}else {
				txt06.setText(arrResult[1][6]);
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
				sId.value = null;
				workID = null;
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
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sResult = writeUpdateExpense(sId);
				if(sResult.equals(OK)) {
					JFoverview.loadExpenses(false);
					dispose();
				}
			}
		});

		// ------------------------------------------------------------------------------
		// Action Listener für Dateisymbol
		// ------------------------------------------------------------------------------
		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					actionMouseClick(e, workID);
				}
			}
		});

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

	private static String writeUpdateExpense(Wrapper<String> sId) {

		String[] arrTmp = new String[8];
		Arrays.fill(arrTmp, null);

		arrTmp[0] = sDatum; //txt01.getText();
		arrTmp[1] = txt02.getText();
		arrTmp[2] = txt03.getText();
		arrTmp[3] = txt04.getText();
		arrTmp[4] = txt05.getText();
		arrTmp[5] = txt06.getText();
		arrTmp[6] = FilePath;
		arrTmp[7] = sId.value;

		for(int x = 0; x < 8; x++) {
			if(arrTmp == null) {
				JOptionPane.showMessageDialog(null, "Dateneingabe unvollständig - bitte alle Felder ausfüllen ...", "Beleg erfassen nicht möglich", JOptionPane.INFORMATION_MESSAGE);
				return NOK;
			}
		}

		String tblName = TBL_EXPENSES.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "UPDATE " + tblName + " SET [Datum] = '" + arrTmp[0] + "',[Art] = '" + arrTmp[1] + "',[netto] = '" + arrTmp[2] + "',[Steuersatz] = '" +
				arrTmp[3] + "',[brutto] = '" + arrTmp[4] + "',[dateiname] = '" + arrTmp[5] +
				"',[datei] = (SELECT * FROM OPENROWSET(BULK '" + arrTmp[6] + "', SINGLE_BLOB) AS DATA) WHERE [Id] = '" + arrTmp[7] + "'";

		try {
			sqlUpdate(sConn, sSQLStatement);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			logger.error("error writing new expenses - " + e);
		}
		return OK;

	}

	private static void setIcon() {
		try {
			JFfileView.setFileIcon(lblFileTyp, txt06.getText());
			lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}

	//###################################################################################################################################################
	// Action Listener Methoden
	//###################################################################################################################################################

	private void actionMouseClick(MouseEvent e, String sId) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 && !e.isConsumed()) {
			e.consume(); // Event verbrauchen, um weitere Verarbeitung zu verhindern

			String outputPath;
			try {
				outputPath = choosePath(LoadData.getWorkPath());

				if (outputPath.equals(getNotSelected())) {
					return;
				}

				String tblName = TBL_EXPENSES.replace("_", LoadData.getStrAktGJ());
				String sSQLStatement = "SELECT [dateiname], [datei] FROM " + tblName + " WHERE [Id] = '" + sId + "'";

				sqlExtractFile(sConn, sSQLStatement, outputPath, "dateiname", "datei");
			} catch (InterruptedException | ClassNotFoundException | SQLException | IOException e1) {
				Thread.currentThread().interrupt();
				logger.error("error while extracting file from database - " + e1);
			}

		}
	}

	public static void setsConn(String sConn) {
		JFeditEx.sConn = sConn;
	}

}

