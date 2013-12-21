package views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import profiles.Profile;
/**
 * Zeigt das Fenster der Profilverwaltung auf dem Bildschirm an.
 */
public class ProfileManagerView extends JFrame {
	public ProfileManagerView() {
		super("Profilverwaltung");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(getParent());
		setResizable(false);

		JPanel contentPane = new JPanel(new BorderLayout());
		
		JPanel north = initNorthPane();
		JPanel center = initCenterPane();
		JPanel south = initSouthPane();
		
		contentPane.add(north, BorderLayout.NORTH);
		contentPane.add(south, BorderLayout.SOUTH);
		contentPane.add(center, BorderLayout.CENTER);
		
		setContentPane(contentPane);
		setVisible(true);
	}
	private JPanel initNorthPane() {
		JPanel north = new JPanel(new FlowLayout());
		north.setBackground(Color.WHITE);
		
		JList<String> profile = new JList<String>(new String[]{"Proﬁl auswählen:"});
		JComboBox<Object> profilename = new JComboBox<>();
		profilename.setMinimumSize(new Dimension(250, 26));
		profilename.setPreferredSize(new Dimension(250, 26));
		
		north.add(profile);
		north.add(profilename);
		north.add(new JButton("Löschen"));
		north.add(new JButton("Neu"));
		
		return north;
	}
	
	private JPanel initSouthPane() {
		JPanel south = new JPanel();
		return south;
	}
	
	private JPanel initCenterPane() {
		JPanel center = new JPanel();
		return center;
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
