package views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import profiles.Profile;
/**
 * Zeigt das Fenster der Profilverwaltung auf dem Bildschirm an.
 */
public class ProfileManagerView extends JFrame {
	public ProfileManagerView() {
		super("Profilverwaltung");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 400);
		setLocationRelativeTo(getParent());
		setResizable(false);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBackground(Color.WHITE);

		setContentPane(contentPane);
		setVisible(true);
	}
	public static void main(String[] args) {
		new ProfileManagerView();
	}
	/**
	 * Setzt das aktuelle Profil auf das angegebene Profil, lädt seine Werte in
	 * die Eingabefelder und aktiviert/deaktiviert die Eingabeelemente, je
	 * nachdem, ob es sich um ein Standardprofil handelt oder nicht.
	 * 
	 * @param profile
	 *            Das neue Profil.
	 */
	public void setCurrentProfile(Profile profile) {
	}
	/**
	 * Setzt die Profil, die aktuell ausgewählt werden können.
	 * 
	 * @param profiles
	 *            Die verfügbaren Profil.
	 */
	public void setAvailableProfiles(List<Profile> profiles) {
	}
}
