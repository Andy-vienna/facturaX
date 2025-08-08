package org.andy.gui.reminder;

import static org.andy.toolbox.misc.CreateObject.createButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.dataExport.ExcelMahnung;
import org.andy.code.dataExport.ExcelReminder;
import org.andy.gui.main.JFoverview;
import org.andy.toolbox.misc.SetFrameIcon;

public class JFreminder extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFreminder.class);

	private static final long serialVersionUID = 1L;

	private JPanel contentPanel = new JPanel();

	private static JButton btnRem = null;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static void showGUI(String sId) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFreminder frame = new JFreminder(sId);
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("fatal error loading gui for editing data - " + e);
				}
			}
		});
	}

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private JFreminder(String sId) {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("rufzeichen.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setResizable(false);
		setTitle("Mahnstufe einleiten");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(null);
		getContentPane().add(contentPanel);
		setLocationRelativeTo(null);

		JLabel lblReason = new JLabel("Mahnstufe einleiten für: " + sId);
		lblReason.setForeground(Color.RED);
		lblReason.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblReason.setHorizontalAlignment(SwingConstants.CENTER);
		lblReason.setBounds(10, 10, 415, 30);
		contentPanel.add(lblReason);

		JRadioButton radio1 = new JRadioButton("<html>Mahnstufe 0<br>Zahlungserinnerung</html>");
		radio1.setBounds(60, 40, 120, 40);
		contentPanel.add(radio1);

		JRadioButton radio2 = new JRadioButton("<html>Mahnstufe 1</html>");
		radio2.setBounds(180, 40, 100, 40);
		contentPanel.add(radio2);

		JRadioButton radio3 = new JRadioButton("<html>Mahnstufe 2</html>");
		radio3.setBounds(280, 40, 100, 40);
		contentPanel.add(radio3);

		ButtonGroup group = new ButtonGroup();
		group.add(radio1);
		group.add(radio2);
		group.add(radio3);

		radio1.setSelected(true);

		//------------------------------------------------------------------------------
		// Buttons anlegen
		//------------------------------------------------------------------------------
		try {
			btnRem = createButton("<html>Mahnstufe drucken</html>", "print.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}

		btnRem.setEnabled(true);
		btnRem.setBounds(50, 90, 340, 50);

		contentPanel.add(btnRem);

		//------------------------------------------------------------------------------
		// Action Listeners
		//------------------------------------------------------------------------------
		btnRem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (radio1.isSelected()) {
					try {
						try {
							ExcelReminder.reminderExport(sId);
						} catch (IOException e1) {
							logger.error("error creating payment reminder - " + e1);
						}

					}catch (Exception e3) {
						logger.error("error writing payment reminder stage 0 - " + e3);
					}
				} else if (radio2.isSelected()) {
					try {
						try {
							ExcelMahnung.mahnungExport(sId, 1);
						} catch (IOException e1) {
							logger.error("error creating payment reminder - " + e1);
						}

					}catch (Exception e3) {
						logger.error("error writing payment reminder stage 1 - " + e3);
					}
				} else if (radio3.isSelected()) {
					try {
						try {
							ExcelMahnung.mahnungExport(sId, 2);
						} catch (IOException e1) {
							logger.error("error creating payment reminder - " + e1);
						}

					}catch (Exception e3) {
						logger.error("error writing payment reminder stage 2 - " + e3);
					}
				}

				JFoverview.actScreen();
				dispose();
			}
		});
	}
}
