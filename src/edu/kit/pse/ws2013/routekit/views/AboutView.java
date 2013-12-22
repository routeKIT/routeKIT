package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Zeigt das Fenster mit den Informationen über routeKIT auf dem Bildschirm an.
 */
public class AboutView extends JFrame {
	public AboutView() {
		super("Über");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 350);
		setLocationRelativeTo(getParent());
		setResizable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		JPanel text = initTextPane();
		JPanel ok = initOkPane();

		panel.add(text, BorderLayout.CENTER);
		panel.add(ok, BorderLayout.SOUTH);

		setContentPane(panel);
		setVisible(true);
	}

	private JPanel initTextPane() {
		JPanel text = new JPanel(new BorderLayout());
		JTextPane information = new JTextPane();
		information.setText("routeKIT, Version 0.1" + "\n"
				+ "PSE-Projekt WS 2013/14" + "\n"
				+ "KIT, Institut für Theoretische Informatik" + "\n"
				+ "Algorithmik II" + "\n" + "Prof. Dr. Peter Sanders" + "\n"
				+ "Julian Arz" + "\n" + "G. Veit Batz" + "\n"
				+ "Dr. Dennis Luxen" + "\n" + "Dennis Schieferdecker" + "\n"
				+ "Ⓒ 2013-2014 Kevin Birke, Felix Dörre, Fabian Hafner," + "\n"
				+ "Lucas Werkmeister, Dominic Ziegler, Anastasia Zinkina");
		Font displayFont = new Font("Serif", Font.ROMAN_BASELINE, 18);
		StyledDocument doc = information.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
        information.setEditable(false);
		information.setFont(displayFont);
		text.add(information, BorderLayout.CENTER);
		return text;
	}

	private JPanel initOkPane() {
		JPanel ok = new JPanel();
		ok.add(new JButton("OK"));
		ok.setBackground(Color.WHITE);
		return ok;
	}

	public static void main(String[] args) {
		new AboutView();
	}
}