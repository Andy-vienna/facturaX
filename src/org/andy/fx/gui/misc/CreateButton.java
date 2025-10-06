package org.andy.fx.gui.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class CreateButton {

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static JButton createButton(String btnText, ImageIcon icon, Color col) throws RuntimeException {
		return doButton(btnText, icon, col);
	}

	//###################################################################################################################################################
	// protected Teil
	//###################################################################################################################################################
	
	protected static JButton doButton(String text, ImageIcon icon, Color col) throws RuntimeException {
		JButton button = new JButton();

		if (text != null && !text.isEmpty()) {
			button.setText(text);
			button.setToolTipText(text);
		}

		if (icon != null) {
			try {
				ImageIcon buttonIcon = icon;
				if (buttonIcon.getImage() != null) {
					button.setIcon(buttonIcon);
				}
			} catch (Exception e) {
				throw new RuntimeException("doButton: Icon nicht gefunden: " + icon);
			}
		}
		
		if (col != null) {
			button.setBackground(col);
		}

		button.setIconTextGap(10);
		button.setFont(new Font("Tahoma", Font.BOLD, 11));
		button.setPreferredSize(new Dimension(130, 50));
		button.setEnabled(false);

		return button;
	}

}
