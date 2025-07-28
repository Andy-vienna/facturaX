package org.andy.gui.file;

import static org.andy.toolbox.misc.SelectFile.chooseFile;
import static org.andy.toolbox.misc.SelectFile.choosePath;
import static org.andy.toolbox.misc.SelectFile.getNotSelected;
import static org.andy.toolbox.misc.Tools.cutFromRight;
import static org.andy.toolbox.misc.Tools.isLocked;
import static org.andy.toolbox.sql.Delete.sqlDeleteNoReturn;
import static org.andy.toolbox.sql.Insert.sqlInsert;
import static org.andy.toolbox.sql.Read.sqlExtractFile;
import static org.andy.toolbox.sql.Read.sqlReadSingleString;
import static org.andy.toolbox.sql.Update.sqlUpdate;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.toolbox.misc.SetFrameIcon;

public class JFfileView extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFfileView.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPane = new JPanel();

	private static final String TYP_AN = "AN", TYP_AB = "AB", TYP_BE = "BE", TYP_RE = "RE", TYP_X1 = "01", TYP_X2 = "02", TYP_X3 = "03";
	private static final String TBL_FILE = "tbl_files", COL_NAME = "xxFileName", COL_FILE = "xxpdfFile", COL_X_NAME = "AddFileNamexx", COL_X_FILE = "AddFilexx";
	private static final String UPLOAD = "upload", DOWNLOAD = "download", UPDATE = "update", DELETE = "delete", SEND = "senden";
	private static final String CSV = "csv", JPG = "jpg", MSG = "msg", PDF = "pdf", PNG = "png", RAR = "rar", XLSM = "xlsm", XLSX = "xlsx", XML = "xml", ZIP = "zip";
	private static final String UCSV = "CSV", UJPG = "JPG", UMSG = "MSG", UPDF = "PDF", UPNG = "PNG", URAR = "RAR", UXLSM = "XLSM", UXLSX = "XLSX", UXML = "XML", UZIP = "ZIP";

	private static String sConn, ColFileName, ColFile;

	private static JLabel lblContentName = new JLabel("");
	private final JLabel lbl1 = new JLabel("Angebot:"), lbl2 = new JLabel("Auftragsbestätigung:"), lbl3 = new JLabel("Bestellung:"), lbl4 = new JLabel("Rechnung:"),
			lbl5 = new JLabel("zus. Datei 1:"), lbl6 = new JLabel("zus. Datei 2:"), lbl7 = new JLabel("zus. Datei 3:");
	private static JLabel lblFileTyp1 = new JLabel(), lblFileTyp2 = new JLabel(), lblFileTyp3 = new JLabel(), lblFileTyp4 = new JLabel(),
			lblFileTyp5 = new JLabel(), lblFileTyp6 = new JLabel(), lblFileTyp7 = new JLabel();
	private static JLabel lblFileName1 = new JLabel(), lblFileName2 = new JLabel(), lblFileName3 = new JLabel(), lblFileName4 = new JLabel(),
			lblFileName5 = new JLabel(), lblFileName6 = new JLabel(), lblFileName7 = new JLabel();

	private static JButton btnDownload1, btnDownload2, btnDownload3, btnDownload4, btnDownload5, btnDownload6, btnDownload7;
	private static JButton btnUpload1, btnUpload2, btnUpload3, btnUpload4, btnUpload5, btnUpload6, btnUpload7;
	private static JButton btnUpdate1, btnUpdate2, btnUpdate3, btnUpdate4, btnUpdate5, btnUpdate6, btnUpdate7;
	private static JButton btnDelete1, btnDelete2, btnDelete3, btnDelete4, btnDelete5, btnDelete6, btnDelete7;
	private static JButton btnSendMail1, btnSendMail2, btnSendMail3, btnSendMail4, btnSendMail5, btnSendMail6, btnSendMail7;

	private static String sNummer = null;
	private static ArrayList<String> lKunde = null;
	private static int isFile = 0;

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadGUI(String sID, ArrayList<String> kunde) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					sNummer = sID;
					lKunde = kunde;
					JFfileView frame = new JFfileView();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI(String sID) fehlgeschlagen - " + e);
					Runtime.getRuntime().gc();
				}
			}
		});
	}

	public JFfileView() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("file.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				destroyWindow();
			}
		});

		setResizable(false);
		setTitle("Dateihandling - " + StartUp.APP_NAME + StartUp.APP_VERSION);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 785, 440);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);

		lblContentName.setFont(new Font("Arial", Font.BOLD, 16));
		lblContentName.setHorizontalAlignment(SwingConstants.CENTER);
		lblContentName.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		lblContentName.setBounds(5, 10, 760, 30);
		contentPane.add(lblContentName);

		lbl1.setBounds(10, 50, 120, 40);
		lbl2.setBounds(10, 100, 120, 40);
		lbl3.setBounds(10, 150, 120, 40);
		lbl4.setBounds(10, 200, 120, 40);
		lbl5.setBounds(10, 250, 120, 40);
		lbl6.setBounds(10, 300, 120, 40);
		lbl7.setBounds(10, 350, 120, 40);

		lblFileTyp1.setBounds(140, 50, 50, 40);
		lblFileTyp2.setBounds(140, 100, 50, 40);
		lblFileTyp3.setBounds(140, 150, 50, 40);
		lblFileTyp4.setBounds(140, 200, 50, 40);
		lblFileTyp5.setBounds(140, 250, 50, 40);
		lblFileTyp6.setBounds(140, 300, 50, 40);
		lblFileTyp7.setBounds(140, 350, 50, 40);

		lblFileName1.setBounds(200, 50, 350, 40);
		lblFileName2.setBounds(200, 100, 350, 40);
		lblFileName3.setBounds(200, 150, 350, 40);
		lblFileName4.setBounds(200, 200, 350, 40);
		lblFileName5.setBounds(200, 250, 350, 40);
		lblFileName6.setBounds(200, 300, 350, 40);
		lblFileName7.setBounds(200, 350, 350, 40);

		btnDownload1 = createButton(DOWNLOAD, 560, 50);
		btnDownload2 = createButton(DOWNLOAD, 560, 100);
		btnDownload3 = createButton(DOWNLOAD, 560, 150);
		btnDownload4 = createButton(DOWNLOAD, 560, 200);
		btnDownload5 = createButton(DOWNLOAD, 560, 250);
		btnDownload6 = createButton(DOWNLOAD, 560, 300);
		btnDownload7 = createButton(DOWNLOAD, 560, 350);

		btnUpload1 = createButton(UPLOAD, 560, 50);
		btnUpload2 = createButton(UPLOAD, 560, 100);
		btnUpload3 = createButton(UPLOAD, 560, 150);
		btnUpload4 = createButton(UPLOAD, 560, 200);
		btnUpload5 = createButton(UPLOAD, 560, 250);
		btnUpload6 = createButton(UPLOAD, 560, 300);
		btnUpload7 = createButton(UPLOAD, 560, 350);

		btnUpdate1 = createButton(UPDATE, 610, 50);
		btnUpdate2 = createButton(UPDATE, 610, 100);
		btnUpdate3 = createButton(UPDATE, 610, 150);
		btnUpdate4 = createButton(UPDATE, 610, 200);
		btnUpdate5 = createButton(UPDATE, 610, 250);
		btnUpdate6 = createButton(UPDATE, 610, 300);
		btnUpdate7 = createButton(UPDATE, 610, 350);

		btnDelete1 = createButton(DELETE, 660, 50);
		btnDelete2 = createButton(DELETE, 660, 100);
		btnDelete3 = createButton(DELETE, 660, 150);
		btnDelete4 = createButton(DELETE, 660, 200);
		btnDelete5 = createButton(DELETE, 660, 250);
		btnDelete6 = createButton(DELETE, 660, 300);
		btnDelete7 = createButton(DELETE, 660, 350);

		btnSendMail1 = createButton(SEND, 710, 50);
		btnSendMail2 = createButton(SEND, 710, 100);
		btnSendMail3 = createButton(SEND, 710, 150);
		btnSendMail4 = createButton(SEND, 710, 200);
		btnSendMail5 = createButton(SEND, 710, 250);
		btnSendMail6 = createButton(SEND, 710, 300);
		btnSendMail7 = createButton(SEND, 710, 350);

		contentPane.add(lbl1);
		contentPane.add(lbl2);
		contentPane.add(lbl3);
		contentPane.add(lbl4);
		contentPane.add(lbl5);
		contentPane.add(lbl6);
		contentPane.add(lbl7);

		contentPane.add(lblFileName1);
		contentPane.add(lblFileName2);
		contentPane.add(lblFileName3);
		contentPane.add(lblFileName4);
		contentPane.add(lblFileName5);
		contentPane.add(lblFileName6);
		contentPane.add(lblFileName7);

		contentPane.add(lblFileTyp1);
		contentPane.add(lblFileTyp2);
		contentPane.add(lblFileTyp3);
		contentPane.add(lblFileTyp4);
		contentPane.add(lblFileTyp5);
		contentPane.add(lblFileTyp6);
		contentPane.add(lblFileTyp7);

		contentPane.add(btnDownload1);
		contentPane.add(btnDownload2);
		contentPane.add(btnDownload3);
		contentPane.add(btnDownload4);
		contentPane.add(btnDownload5);
		contentPane.add(btnDownload6);
		contentPane.add(btnDownload7);

		contentPane.add(btnUpload1);
		contentPane.add(btnUpload2);
		contentPane.add(btnUpload3);
		contentPane.add(btnUpload4);
		contentPane.add(btnUpload5);
		contentPane.add(btnUpload6);
		contentPane.add(btnUpload7);

		contentPane.add(btnUpdate1);
		contentPane.add(btnUpdate2);
		contentPane.add(btnUpdate3);
		contentPane.add(btnUpdate4);
		contentPane.add(btnUpdate5);
		contentPane.add(btnUpdate6);
		contentPane.add(btnUpdate7);

		contentPane.add(btnDelete1);
		contentPane.add(btnDelete2);
		contentPane.add(btnDelete3);
		contentPane.add(btnDelete4);
		contentPane.add(btnDelete5);
		contentPane.add(btnDelete6);
		contentPane.add(btnDelete7);

		contentPane.add(btnSendMail1);
		contentPane.add(btnSendMail2);
		contentPane.add(btnSendMail3);
		contentPane.add(btnSendMail4);
		contentPane.add(btnSendMail5);
		contentPane.add(btnSendMail6);
		contentPane.add(btnSendMail7);

		btnDownload1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("AN", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});
		btnDownload2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("AB", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});
		btnDownload3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("BE", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});
		btnDownload4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("RE", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});
		btnDownload5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("01", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});
		btnDownload6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("02", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});
		btnDownload7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String FileName = saveFile("03", sNummer);

				File file = new File(FileName);
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					logger.error("error opening file for view - " + e1);
				}
			}
		});

		btnUpload1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("AN", sNummer);
			}
		});
		btnUpload2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("AB", sNummer);
			}
		});
		btnUpload3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("BE", sNummer);
			}
		});
		btnUpload4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("RE", sNummer);
			}
		});
		btnUpload5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("01", sNummer);
			}
		});
		btnUpload6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("02", sNummer);
			}
		});
		btnUpload7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile("03", sNummer);
			}
		});

		btnUpdate1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("AN", sNummer);
			}
		});
		btnUpdate2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("AB", sNummer);
			}
		});
		btnUpdate3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("BE", sNummer);
			}
		});
		btnUpdate4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("RE", sNummer);
			}
		});
		btnUpdate5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("01", sNummer);
			}
		});
		btnUpdate6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("02", sNummer);
			}
		});
		btnUpdate7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFile("03", sNummer);
			}
		});

		btnDelete1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("AN", sNummer);
			}
		});
		btnDelete2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("AB", sNummer);
			}
		});
		btnDelete3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("BE", sNummer);
			}
		});
		btnDelete4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("RE", sNummer);
			}
		});
		btnDelete5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("01", sNummer);
			}
		});
		btnDelete6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("02", sNummer);
			}
		});
		btnDelete7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteFile("03", sNummer);
			}
		});

		btnSendMail1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String CompleteFileName = getFileForMail("AN", sNummer, LoadData.getWorkPath());
				String FileName = null;
				try {
					FileName = cutFromRight(CompleteFileName, '\\');
				} catch (IOException e1) {
					logger.error("error cutting filename from path - " + CompleteFileName + " - " + e1);
				}

				String[] zeilen = {"Sehr geehrte(r) " + lKunde.get(6) + " " + lKunde.get(7) + ","
						, "gerne sende ich Ihnen das Angebot (" + FileName + ") zu Ihrer Anfrage."
						, ""
						, "Mit freundlichen Grüßen"
						, ""
						, "Andreas Fischer"};
				String sText = String.join(System.lineSeparator(), zeilen);

				sendMail(lKunde.get(14), FileName, sText);
			}
		});
		btnSendMail2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String CompleteFileName = getFileForMail("AB", sNummer, LoadData.getWorkPath());
				String FileName = null;
				try {
					FileName = cutFromRight(CompleteFileName, '\\');
				} catch (IOException e1) {
					logger.error("error cutting filename from path - " + CompleteFileName + " - " + e1);
				}

				String[] zeilen = {"Sehr geehrte(r) " + lKunde.get(6) + " " + lKunde.get(7) + ","
						, "gerne sende ich Ihnen die Auftragsbestätigung (" + FileName + ") zu Ihrer Bestellung."
						, ""
						, "Mit freundlichen Grüßen"
						, ""
						, "Andreas Fischer"};
				String sText = String.join(System.lineSeparator(), zeilen);

				sendMail(lKunde.get(14), FileName, sText);
			}
		});
		btnSendMail3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnSendMail4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String CompleteFileName = getFileForMail("RE", sNummer, LoadData.getWorkPath());
				String FileName = null;
				try {
					FileName = cutFromRight(CompleteFileName, '\\');
				} catch (IOException e1) {
					logger.error("error cutting filename from path - " + CompleteFileName + " - " + e1);
				}

				String[] zeilen = {"Sehr geehrte(r) " + lKunde.get(6) + " " + lKunde.get(7) + ","
						, "in der Anlage sende ich Ihnen meine Rechung (" + FileName + ") zur erfolgten Dienstleistung."
						, ""
						, "Mit freundlichen Grüßen"
						, ""
						, "Andreas Fischer"};
				String sText = String.join(System.lineSeparator(), zeilen);

				sendMail(lKunde.get(14), FileName, sText);
			}
		});
		btnSendMail5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String CompleteFileName = getFileForMail("01", sNummer, LoadData.getWorkPath());
				String FileName = null;
				try {
					FileName = cutFromRight(CompleteFileName, '\\');
				} catch (IOException e1) {
					logger.error("error cutting filename from path - " + CompleteFileName + " - " + e1);
				}

				String[] zeilen = {"Sehr geehrte(r) " + lKunde.get(6) + " " + lKunde.get(7) + ","
						, ""
						, "in der Hektik des Geschäftsalltags kann es schon mal passieren, dass etwas untergeht."
						, "Ich sende Ihnen meine Zahlungserinnerung (" + FileName + "), da die zugehörige Rechnung noch offen ist."
						, ""
						, "Mit freundlichen Grüßen"
						, ""
						, "Andreas Fischer"};
				String sText = String.join(System.lineSeparator(), zeilen);

				sendMail(lKunde.get(14), FileName, sText);
			}
		});
		btnSendMail6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String CompleteFileName = getFileForMail("02", sNummer, LoadData.getWorkPath());
				String FileName = null;
				try {
					FileName = cutFromRight(CompleteFileName, '\\');
				} catch (IOException e1) {
					logger.error("error cutting filename from path - " + CompleteFileName + " - " + e1);
				}

				String[] zeilen = {"Sehr geehrte(r) " + lKunde.get(6) + " " + lKunde.get(7) + ","
						, ""
						, "leider konnte ich trotz bereits zugesendeter Zahlungserinnerung keinen Zahlungseingang auf meinem Konto feststellen."
						, "In der Anlage sende ich Ihnen die 1. Mahnung (" + FileName + ") zur Begleichung der offenen Rechnung."
						, ""
						, "Mit freundlichen Grüßen"
						, ""
						, "Andreas Fischer"};
				String sText = String.join(System.lineSeparator(), zeilen);

				sendMail(lKunde.get(14), FileName, sText);
			}
		});
		btnSendMail7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String CompleteFileName = getFileForMail("03", sNummer, LoadData.getWorkPath());
				String FileName = null;
				try {
					FileName = cutFromRight(CompleteFileName, '\\');
				} catch (IOException e1) {
					logger.error("error cutting filename from path - " + CompleteFileName + " - " + e1);
				}

				String[] zeilen = {"Sehr geehrte(r) " + lKunde.get(6) + " " + lKunde.get(7) + ","
						, ""
						, "leider konnte ich trotz bereits zugesendeter 1. Mahnung keinen Zahlungseingang auf meinem Konto feststellen."
						, "In der Anlage sende ich Ihnen die 2. Mahung (" + FileName + ") zur Begleichung der offenen Rechnung."
						, ""
						, "Mit freundlichen Grüßen"
						, ""
						, "Andreas Fischer"};
				String sText = String.join(System.lineSeparator(), zeilen);

				sendMail(lKunde.get(14), FileName, sText);
			}
		});

		actualizeWindow();
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private void sendMail(String sAdress, String sSubject, String sBody) {

		ActiveXComponent outlook = new ActiveXComponent("Outlook.Application");

		try {
			Dispatch mail = Dispatch.call(outlook, "CreateItem", 0).toDispatch();
			Dispatch.put(mail, "To", sAdress);
			Dispatch.put(mail, "Subject", sSubject);
			Dispatch.put(mail, "Body", sBody);

			// Datei als Anhang hinzufügen
			Dispatch attachments = Dispatch.get(mail, "Attachments").toDispatch();
			Dispatch.call(attachments, "Add", LoadData.getWorkPath() + "\\" + sSubject);

			// E-Mail senden
			Dispatch.call(mail, "Send");

			JOptionPane.showMessageDialog(null, "E-Mail an [" + sAdress + "] erfolgreich versendet", "E-Mail Versand", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e1) {
			logger.error("error sending email - " + e1);
		} finally {
			outlook.safeRelease();
		}

		boolean bLocked = isLocked(LoadData.getWorkPath() + "\\" + sSubject);
		while(bLocked) {
			System.out.println("warte auf Dateien ...");
		}
		File MailFile = new File(LoadData.getWorkPath() + "\\" + sSubject);
		if(MailFile.delete()) {

		}else {
			logger.error("error deleting mail attachment from folder ...");
		}

	}

	private void destroyWindow() {
		lblContentName.setText("");
		lblFileName1.setText(getNotSelected());
		lblFileName1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName2.setText(getNotSelected());
		lblFileName2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName3.setText(getNotSelected());
		lblFileName3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName4.setText(getNotSelected());
		lblFileName4.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName5.setText(getNotSelected());
		lblFileName5.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName6.setText(getNotSelected());
		lblFileName6.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName7.setText(getNotSelected());
		lblFileName7.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileTyp1.setIcon(null);
		lblFileTyp2.setIcon(null);
		lblFileTyp3.setIcon(null);
		lblFileTyp4.setIcon(null);
		lblFileTyp5.setIcon(null);
		lblFileTyp6.setIcon(null);
		lblFileTyp7.setIcon(null);
		btnDownload1.setVisible(false);
		btnDownload2.setVisible(false);
		btnDownload3.setVisible(false);
		btnDownload4.setVisible(false);
		btnDownload5.setVisible(false);
		btnDownload6.setVisible(false);
		btnDownload7.setVisible(false);
		btnUpload1.setVisible(false);
		btnUpload2.setVisible(false);
		btnUpload3.setVisible(false);
		btnUpload4.setVisible(false);
		btnUpload5.setVisible(false);
		btnUpload6.setVisible(false);
		btnUpload7.setVisible(false);
		btnUpdate1.setVisible(false);
		btnUpdate2.setVisible(false);
		btnUpdate3.setVisible(false);
		btnUpdate4.setVisible(false);
		btnUpdate5.setVisible(false);
		btnUpdate6.setVisible(false);
		btnUpdate7.setVisible(false);
		btnDelete1.setVisible(false);
		btnDelete2.setVisible(false);
		btnDelete3.setVisible(false);
		btnDelete4.setVisible(false);
		btnDelete5.setVisible(false);
		btnDelete6.setVisible(false);
		btnDelete7.setVisible(false);
		btnSendMail1.setVisible(false);
		btnSendMail2.setVisible(false);
		btnSendMail4.setVisible(false);
		btnSendMail5.setVisible(false);
		btnSendMail6.setVisible(false);
		btnSendMail7.setVisible(false);
		Runtime.getRuntime().gc();
	}

	private void actualizeWindow() {
		String[] FileName = new String[7];
		lblContentName.setText(sNummer);
		FileName = queryFileNames(sNummer);
		setIcon(FileName);
		enableButtons(FileName);
		contentPane.revalidate();
		contentPane.repaint();
		Runtime.getRuntime().gc();
	}

	private static JButton createButton(String btnText, int xPos, int yPos) {
		JButton button = new JButton();
		button.setToolTipText(btnText);
		button.setIconTextGap(10);
		button.setBounds(xPos, yPos, 50, 40);
		button.setFont(new Font("Tahoma", Font.BOLD, 11));
		switch(btnText) {
		case UPLOAD:
			button.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/up.png")));
			break;
		case DOWNLOAD:
			button.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/down.png")));
			break;
		case UPDATE:
			button.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/update.png")));
			break;
		case DELETE:
			button.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/delete.png")));
			break;
		case SEND:
			button.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/mail.png")));
			break;
		}
		return button;
	}

	private static String[] queryFileNames(String sID) {

		String[] FileName = new String[7];

		for(int i = 0; i < FileName.length; i++) {
			FileName[i] = getNotSelected();
		}
		lblFileName1.setText(FileName[0]);
		lblFileName1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName2.setText(FileName[1]);
		lblFileName2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName3.setText(FileName[2]);
		lblFileName3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName4.setText(FileName[3]);
		lblFileName4.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName5.setText(FileName[4]);
		lblFileName5.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName6.setText(FileName[5]);
		lblFileName6.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFileName7.setText(FileName[6]);
		lblFileName7.setFont(new Font("Tahoma", Font.PLAIN, 11));
		try {
			FileName[0] = queryFileDB("AN", sID);
			FileName[1] = queryFileDB("AB", sID);
			FileName[2] = queryFileDB("BE", sID);
			FileName[3] = queryFileDB("RE", sID);
			FileName[4] = queryFileDB("01", sID);
			FileName[5] = queryFileDB("02", sID);
			FileName[6] = queryFileDB("03", sID);
		} catch (SQLException | ClassNotFoundException e) {
			logger.error("error reading filename from database - " + e);
		}
		if(FileName[0] != getNotSelected()) {
			lblFileName1.setText(FileName[0]);
			lblFileName1.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		if(FileName[1] != getNotSelected()) {
			lblFileName2.setText(FileName[1]);
			lblFileName2.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		if(FileName[2] != getNotSelected()) {
			lblFileName3.setText(FileName[2]);
			lblFileName3.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		if(FileName[3] != getNotSelected()) {
			lblFileName4.setText(FileName[3]);
			lblFileName4.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		if(FileName[4] != getNotSelected()) {
			lblFileName5.setText(FileName[4]);
			lblFileName5.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		if(FileName[5] != getNotSelected()) {
			lblFileName6.setText(FileName[5]);
			lblFileName6.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		if(FileName[6] != getNotSelected()) {
			lblFileName7.setText(FileName[6]);
			lblFileName7.setFont(new Font("Tahoma", Font.BOLD, 11));
		}
		return FileName;
	}

	private static void setIcon(String[] FileName) {
		try {
			int file1 = setFileIcon(lblFileTyp1, FileName[0]);
			int file2 = setFileIcon(lblFileTyp2, FileName[1]);
			int file3 = setFileIcon(lblFileTyp3, FileName[2]);
			int file4 = setFileIcon(lblFileTyp4, FileName[3]);
			int file5 = setFileIcon(lblFileTyp5, FileName[4]);
			int file6 = setFileIcon(lblFileTyp6, FileName[5]);
			int file7 = setFileIcon(lblFileTyp7, FileName[6]);
			lblFileTyp1.setHorizontalAlignment(SwingConstants.CENTER);
			lblFileTyp2.setHorizontalAlignment(SwingConstants.CENTER);
			lblFileTyp3.setHorizontalAlignment(SwingConstants.CENTER);
			lblFileTyp4.setHorizontalAlignment(SwingConstants.CENTER);
			lblFileTyp5.setHorizontalAlignment(SwingConstants.CENTER);
			lblFileTyp6.setHorizontalAlignment(SwingConstants.CENTER);
			lblFileTyp7.setHorizontalAlignment(SwingConstants.CENTER);
			isFile = file1+ file2+ file3 + file4 + file5 + file6 + file7;
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}

	public static int setFileIcon(JLabel lbl, String fileName) throws IOException {
		if(fileName.equals(getNotSelected()) || fileName == null || fileName.isEmpty()) {
			lbl.setIcon(null);
			return 0;
		}
		String typ = cutFromRight(fileName, '.');
		switch(typ) {
		case PDF:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/pdf.png")));
			return 1;
		case PNG:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/png.png")));
			return 1;
		case JPG:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/jpg.png")));
			return 1;
		case CSV:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/csv.png")));
			return 1;
		case MSG:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/msg.png")));
			return 1;
		case XML:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/xml.png")));
			return 1;
		case XLSX:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/xlsx.png")));
			return 1;
		case XLSM:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/xlsm.png")));
			return 1;
		case RAR:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/rar.png")));
			return 1;
		case ZIP:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/zip.png")));
			return 1;
		case UPDF:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/pdf.png")));
			return 1;
		case UPNG:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/png.png")));
			return 1;
		case UJPG:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/jpg.png")));
			return 1;
		case UCSV:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/csv.png")));
			return 1;
		case UMSG:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/msg.png")));
			return 1;
		case UXML:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/xml.png")));
			return 1;
		case UXLSX:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/xlsx.png")));
			return 1;
		case UXLSM:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/xlsm.png")));
			return 1;
		case URAR:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/rar.png")));
			return 1;
		case UZIP:
			lbl.setIcon(new ImageIcon(JFfileView.class.getResource("/org/resources/icons/zip.png")));
			return 1;
		default:
			lbl.setIcon(null);
			return 0;
		}
	}

	private static void enableButtons(String[] FileName) {
		btnDownload1.setVisible(false);
		btnDownload2.setVisible(false);
		btnDownload3.setVisible(false);
		btnDownload4.setVisible(false);
		btnDownload5.setVisible(false);
		btnDownload6.setVisible(false);
		btnDownload7.setVisible(false);
		btnUpload1.setVisible(true);
		btnUpload2.setVisible(true);
		btnUpload3.setVisible(true);
		btnUpload4.setVisible(true);
		btnUpload5.setVisible(true);
		btnUpload6.setVisible(true);
		btnUpload7.setVisible(true);
		btnUpdate1.setVisible(false);
		btnUpdate2.setVisible(false);
		btnUpdate3.setVisible(false);
		btnUpdate4.setVisible(false);
		btnUpdate5.setVisible(false);
		btnUpdate6.setVisible(false);
		btnUpdate7.setVisible(false);
		btnDelete1.setVisible(false);
		btnDelete2.setVisible(false);
		btnDelete3.setVisible(false);
		btnDelete4.setVisible(false);
		btnDelete5.setVisible(false);
		btnDelete6.setVisible(false);
		btnDelete7.setVisible(false);
		btnSendMail1.setVisible(false);
		btnSendMail2.setVisible(false);
		btnSendMail3.setVisible(false);
		btnSendMail4.setVisible(false);
		btnSendMail5.setVisible(false);
		btnSendMail6.setVisible(false);
		btnSendMail7.setVisible(false);

		if(FileName[0] != getNotSelected()) {
			btnDownload1.setVisible(true);
			btnUpload1.setVisible(false);
			btnUpdate1.setVisible(true);
			btnDelete1.setVisible(true);
			btnSendMail1.setVisible(true);
		}
		if(FileName[1] != getNotSelected()) {
			btnDownload2.setVisible(true);
			btnUpload2.setVisible(false);
			btnUpdate2.setVisible(true);
			btnDelete2.setVisible(true);
			btnSendMail2.setVisible(true);
		}
		if(FileName[2] != getNotSelected()) {
			btnDownload3.setVisible(true);
			btnUpload3.setVisible(false);
			btnUpdate3.setVisible(true);
			btnDelete3.setVisible(true);
			btnSendMail3.setVisible(false);
		}
		if(FileName[3] != getNotSelected()) {
			btnDownload4.setVisible(true);
			btnUpload4.setVisible(false);
			btnUpdate4.setVisible(true);
			btnDelete4.setVisible(true);
			btnSendMail4.setVisible(true);
		}
		if(FileName[4] != getNotSelected()) {
			btnDownload5.setVisible(true);
			btnUpload5.setVisible(false);
			btnUpdate5.setVisible(true);
			btnDelete5.setVisible(true);
			if(FileName[4].contains("Zahlungserinnerung")) {
				btnSendMail5.setVisible(true);
			}
		}
		if(FileName[5] != getNotSelected()) {
			btnDownload6.setVisible(true);
			btnUpload6.setVisible(false);
			btnUpdate6.setVisible(true);
			btnDelete6.setVisible(true);
			if(FileName[5].contains("Mahnung")) {
				btnSendMail6.setVisible(true);
			}
		}
		if(FileName[6] != getNotSelected()) {
			btnDownload7.setVisible(true);
			btnUpload7.setVisible(false);
			btnUpdate7.setVisible(true);
			btnDelete7.setVisible(true);
			if(FileName[6].contains("Mahnung")) {
				btnSendMail7.setVisible(true);
			}
		}
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	private String saveFile(String typ, String id) {

		String CompleteFileName = null;
		try {
			CompleteFileName = extractFileFromDB(typ, id);
		} catch (SQLException | IOException | InterruptedException | ClassNotFoundException e) {
			Thread.currentThread().interrupt();
			logger.error("error reading file from database - " + e);
		}
		actualizeWindow();
		return CompleteFileName;
	}

	private void loadFile(String typ, String id) {

		if(isFile == 0) {
			try {
				insertFileIntoDB(typ, id);
			} catch (SQLException | ClassNotFoundException e) {
				logger.error("error inserting new file into database - " + e);
			}
		}else {
			try {
				updateFileIntoDB(typ, id);
			} catch (SQLException | ClassNotFoundException e) {
				logger.error("error updating new file into database - " + e);
			}
		}
		actualizeWindow();
	}

	private void updateFile(String typ, String id) {
		try {
			updateFileIntoDB(typ, id);
		} catch (SQLException | ClassNotFoundException e) {
			logger.error("error updating existing file in databse - " + e);
		}
		actualizeWindow();
	}

	private void deleteFile(String typ, String id) {
		try {
			deleteFileFromDB(typ, id);
		} catch (SQLException | ClassNotFoundException e) {
			logger.error("error deleting file from database - " + e);
		}
		destroyWindow();
		actualizeWindow();
	}

	private String getFileForMail(String typ, String id, String sPath) {

		String CompleteFileName = null;
		try {
			CompleteFileName = extractFileForMail(typ, id, sPath);
		} catch (SQLException | IOException | InterruptedException | ClassNotFoundException e) {
			Thread.currentThread().interrupt();
			logger.error("error reading file from database - " + e);
		}
		actualizeWindow();
		return CompleteFileName;

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static String queryFileDB(String sTyp, String sID) throws SQLException, ClassNotFoundException {

		typeDecoder(sTyp); // Spaltennamen zusammenbauen

		String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "SELECT [" + ColFileName + "] FROM " + tblName + " WHERE [IdNummer] = '" + sID + "'";

		return sqlReadSingleString(sConn, sSQLStatement, ColFileName);

	}

	private static void insertFileIntoDB(String sTyp, String sID) throws SQLException, ClassNotFoundException {

		String FileNamePath = chooseFile(LoadData.getWorkPath());
		if(FileNamePath == getNotSelected()) {
			return;
		}
		File fn = new File(FileNamePath);
		String FileName = fn.getName();

		typeDecoder(sTyp); // Spaltennamen zusammenbauen

		String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "INSERT INTO " + tblName + " ([IdNummer],[" + ColFileName + "],[" + ColFile + "]) VALUES ('" + sID + "','" + FileName
				+ "',(SELECT * FROM OPENROWSET(BULK '" + FileNamePath + "', SINGLE_BLOB) AS DATA))";

		sqlInsert(sConn, sSQLStatement);

	}

	private static void updateFileIntoDB(String sTyp, String sID) throws SQLException, ClassNotFoundException {

		String FileNamePath = chooseFile(LoadData.getWorkPath());
		if(FileNamePath == getNotSelected()) {
			return;
		}
		File fn = new File(FileNamePath);
		String FileName = fn.getName();

		typeDecoder(sTyp); // Spaltennamen zusammenbauen

		String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "UPDATE " + tblName + " SET [" + ColFileName + "] = '" + FileName + "',[" + ColFile + "] = (SELECT * FROM OPENROWSET(BULK '"
				+ FileNamePath + "', SINGLE_BLOB) AS DATA) WHERE [IdNummer] = '" + sID + "'";

		sqlUpdate(sConn, sSQLStatement);
	}

	private static void deleteFileFromDB(String sTyp, String sId) throws SQLException, ClassNotFoundException {

		typeDecoder(sTyp); // Spaltennamen zusammenbauen

		String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "UPDATE " + tblName + " SET [" + ColFileName + "] = null,[" + ColFile + "] = null WHERE [IdNummer] = '" + sId + "'";

		sqlDeleteNoReturn(sConn, sSQLStatement);
	}

	private static String extractFileFromDB(String sTyp, String sId) throws SQLException, IOException, InterruptedException, ClassNotFoundException {

		typeDecoder(sTyp); // Spaltennamen zusammenbauen

		String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "SELECT [" + ColFileName + "], [" + ColFile + "] FROM " + tblName + " WHERE [IdNummer] = '" + sId + "'";

		// Zielpfad für die Datei auswählen
		String outputPath = choosePath(LoadData.getWorkPath());
		if (outputPath.equals(getNotSelected())) {
			return getNotSelected();
		}

		String completeFileName = sqlExtractFile(sConn, sSQLStatement, outputPath, ColFileName, ColFile);

		return completeFileName;

	}

	private static String extractFileForMail(String sTyp, String sId, String sPath) throws SQLException, IOException, InterruptedException, ClassNotFoundException {

		typeDecoder(sTyp); // Spaltennamen zusammenbauen

		String tblName = TBL_FILE.replace("_", LoadData.getStrAktGJ());
		String sSQLStatement = "SELECT [" + ColFileName + "], [" + ColFile + "] FROM " + tblName + " WHERE [IdNummer] = '" + sId + "'";

		// Zielpfad für die Datei auswählen
		String outputPath = null;
		outputPath = sPath;
		if (outputPath == null) {
			return getNotSelected();
		}

		String completeFileName = sqlExtractFile(sConn, sSQLStatement, outputPath, ColFileName, ColFile);

		return completeFileName;

	}

	private static void typeDecoder(String sTyp) {
		switch(sTyp) {
		case TYP_AN:
			ColFileName = COL_NAME.replace("xx", TYP_AN);
			ColFile = COL_FILE.replace("xx", TYP_AN);
			break;
		case TYP_AB:
			ColFileName = COL_NAME.replace("xx", TYP_AB);
			ColFile = COL_FILE.replace("xx", TYP_AB);
			break;
		case TYP_BE:
			ColFileName = COL_NAME.replace("xx", TYP_BE);
			ColFile = COL_FILE.replace("xx", TYP_BE);
			break;
		case TYP_RE:
			ColFileName = COL_NAME.replace("xx", TYP_RE);
			ColFile = COL_FILE.replace("xx", TYP_RE);
			break;
		case TYP_X1:
			ColFileName = COL_X_NAME.replace("xx", TYP_X1);
			ColFile = COL_X_FILE.replace("xx", TYP_X1);
			break;
		case TYP_X2:
			ColFileName = COL_X_NAME.replace("xx", TYP_X2);
			ColFile = COL_X_FILE.replace("xx", TYP_X2);
			break;
		case TYP_X3:
			ColFileName = COL_X_NAME.replace("xx", TYP_X3);
			ColFile = COL_X_FILE.replace("xx", TYP_X3);
			break;
		}
	}

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void setsConn(String sConn) {
		JFfileView.sConn = sConn;
	}

}
