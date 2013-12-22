package edu.kit.pse.ws2013.routekit.views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.kit.pse.ws2013.routekit.history.History;
/**
 * Zeigt das Fenster mit dem Verlauf auf dem Bildschirm an.
 */
public class HistoryView extends JFrame{
	/**
	 * Konstruktor: Erstellt eine HistoryView für den angegebenen Verlauf. (Der
	 * Verlauf kann später nicht mehr geändert werden.)
	 * 
	 * @param history
	 *            Der Verlauf, der angezeigt wird.
	 */
	public HistoryView(History history) {
		super("Verlauf");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(getParent());
		setResizable(false);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel south = initSouthPane();
		contentPane.add(initCenterPane(), BorderLayout.CENTER);
		contentPane.add(south, BorderLayout.SOUTH);

		setContentPane(contentPane);
		setVisible(true);
	}

	private JComponent initCenterPane() {
		String[] strings = new String[100];
		Arrays.fill(strings, "hallo");
		strings[0] = "sdfsdfsd";
		JList<String> history = new JList<String>(strings);
		history.setBackground(Color.lightGray);
		return new JScrollPane(history);
	}

	private JPanel initSouthPane() {
		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		south.add(new JButton("OK"));
		south.add(new JButton("Abbrechen"));
		return south;
	}
	
	public static void main(String[] args) {
		new HistoryView(null);
	}
	

}
