package views;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Zeigt das Fenster mit den Informationen über routeKIT auf dem Bildschirm an.
 */
public class AboutView extends JFrame {
	public AboutView() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 400);

		JPanel panel = new JPanel();
		setVisible(true);

	}
	public static void main(String[] args) {
		new AboutView();
	}
}