package org.andy.gui.main;

import static org.andy.toolbox.misc.CreateObject.createButton;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.code.main.LoadData;
import org.andy.code.main.StartUp;
import org.andy.toolbox.misc.SetFrameIcon;

public class JFinfo extends JFrame {

	private static final Logger logger = LogManager.getLogger(JFinfo.class);
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private static final String TEXT_B = "<html>Copyright " + "\u00a9" + "2025 Andreas Fischer<br>"
			+ "<br>"
			+ "   Licensed under the Apache License, Version 2.0 (the \"License\");<br>"
			+ "   you may not use this file except in compliance with the License.<br>"
			+ "   You may obtain a copy of the License at<br>"
			+ "<br>"
			+ "       http://www.apache.org/licenses/LICENSE-2.0<br>"
			+ "<br>"
			+ "   Unless required by applicable law or agreed to in writing, software<br>"
			+ "   distributed under the License is distributed on an \"AS IS\" BASIS,<br>"
			+ "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>"
			+ "   See the License for the specific language governing permissions and<br>"
			+ "   limitations under the License.<br>"
			+ "</html>";
	private static final String TEXT_D = "Factur-X (fx)/ZUGFeRD (zf) is a standard family for PDF e-invoices embedding XML files for metadata";

	//###################################################################################################################################################
	//###################################################################################################################################################

	public static void loadFrame() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					LoadData.LoadProgSettings();
					JFinfo frame = new JFinfo();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI fehlgeschlagen - " + e);
				}
			}
		});
	}

	public JFinfo() {

		try {
			setIconImage(SetFrameIcon.getFrameIcon("info.png"));
		} catch (IOException e) {
			logger.error("error loading frame icon - " + e);
		}

		setTitle(StartUp.APP_NAME + StartUp.APP_VERSION + " - " + "\u00a9" + "2024-2025 Andreas Fischer");
		setResizable(false);
		setUndecorated(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 385);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lbl01 = new JLabel();
		lbl01.setIcon(new ImageIcon(JFinfo.class.getResource("/org/resources/icons/ZUGFeRD_200px.jpg")));
		lbl01.setBounds(5, 10, 200, 64);
		contentPane.add(lbl01);

		JLabel lbl02 = new JLabel();
		lbl02.setIcon(new ImageIcon(JFinfo.class.getResource("/org/resources/icons/member_partner_200px.jpg")));
		lbl02.setBounds(25, 80, 161, 200);
		contentPane.add(lbl02);

		JLabel lbl03 = new JLabel();
		lbl03.setIcon(new ImageIcon(JFinfo.class.getResource("/org/resources/icons/icon.png")));
		lbl03.setBounds(225, 12, 32, 32);
		contentPane.add(lbl03);

		JLabel lbl04 = new JLabel();
		lbl04.setBackground(Color.WHITE);
		lbl04.setForeground(new Color(173, 216, 230));
		lbl04.setText(StartUp.APP_NAME + StartUp.APP_VERSION);
		lbl04.setFont(new Font("Arial", Font.BOLD, 28));
		lbl04.setBounds(265, 10, 510, 35);
		contentPane.add(lbl04);

		JLabel lbl05 = new JLabel();
		lbl05.setBackground(Color.WHITE);
		lbl05.setText(TEXT_D);
		lbl05.setFont(new Font("Arial", Font.PLAIN, 13));
		lbl05.setBounds(15, 305, 637, 20);
		contentPane.add(lbl05);

		JLabel lbl06 = new JLabel();
		lbl06.setText(TEXT_B);
		lbl06.setFont(new Font("Arial", Font.PLAIN, 13));
		lbl06.setBackground(Color.WHITE);
		lbl06.setBounds(225, 55, 550, 225);
		contentPane.add(lbl06);

		JButton btnNewButton = null;
		try {
			btnNewButton = createButton("", "ok.png");
		} catch (RuntimeException e1) {
			logger.error("error creating button - " + e1);
		}
		btnNewButton.setEnabled(true);
		btnNewButton.setBounds(695, 287, 80, 50);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runtime.getRuntime().gc();
				dispose();
			}
		});
		contentPane.add(btnNewButton);

		JSeparator separator = new JSeparator();
		separator.setOpaque(true);
		separator.setBackground(new Color(173, 216, 230));
		separator.setBounds(210, 0, 2, 291);
		contentPane.add(separator);

	}

	//###################################################################################################################################################
	//###################################################################################################################################################

}
