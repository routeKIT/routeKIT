package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * Zeigt das Fenster mit den Informationen über routeKIT auf dem Bildschirm an.
 */
public class AboutView extends JFrame {
	public AboutView() {
		super("Über");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 350);
		
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
		JList<String> information = new JList<String>(new String[]{
				"routeKIT, Version 0.1", "PSE-Projekt WS 2013/14", 
				"KIT, Institut für Theoretische Informatik", "Algorithmik II",
				"Prof. Dr. Peter Sanders", "Julian Arz", "G. Veit Batz",
				"Dr. Dennis Luxen", "Dennis Schieferdecker", 
				"Ⓒ 2013-2014 Kevin Birke, Felix Dörre, Fabian Hafner,", 
				"Lucas Werkmeister, Dominic Ziegler, Anastasia Zinkina"});
		Font displayFont = new Font("Serif", Font.LAYOUT_LEFT_TO_RIGHT, 15);
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