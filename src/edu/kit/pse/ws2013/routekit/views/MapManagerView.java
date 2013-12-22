package edu.kit.pse.ws2013.routekit.views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Zeigt das Fenster der Kartenverwaltung auf dem Bildschirm an.
 */
public class MapManagerView extends JFrame {
	public MapManagerView() {
		super("Kartenverwaltung");
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
		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		north.setBackground(Color.WHITE);
		JLabel map = new JLabel("Karte auswählen:");
		JComboBox<Object> mapname = new JComboBox<>();
		mapname.setMinimumSize(new Dimension(250, 26));
		mapname.setPreferredSize(new Dimension(250, 26));

		north.add(map);
		north.add(mapname);
		return north;
	}
	private JPanel initCenterPane() {
		JPanel center = new JPanel(new BorderLayout(10, 10));
		center.setBackground(Color.WHITE);

		JPanel buttons = initButtonsPane();
		JPanel mapProfile = initMapProfilePane();

		center.add(buttons, BorderLayout.NORTH);
		center.add(mapProfile, BorderLayout.CENTER);
		center.add(new JPanel(), BorderLayout.WEST);

		return center;
	}

	private JPanel initSouthPane() {
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		south.setBackground(Color.WHITE);

		south.add(new JButton("OK"));
		south.add(new JButton("Abbrechen"));
		return south;
	}

	private JPanel initButtonsPane() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.setBackground(Color.WHITE);

		buttons.add(new JButton("Importieren"));
		buttons.add(new JButton("Aktualisieren"));
		buttons.add(new JButton("Löschen"));

		return buttons;
	}

	private JPanel initMapProfilePane() {
		JPanel mapProfile = new JPanel(new BorderLayout(10, 10));
		mapProfile.setBackground(Color.WHITE);
		JPanel addDelete = initAddDelete();
		JList<String> profile = new JList<String>(new String[]{"sdfsdfsd",
				"hallo"});
		profile.setBackground(Color.lightGray);

		mapProfile.add(new JLabel("Profile für diese Karte:"),
				BorderLayout.NORTH);
		mapProfile.add(addDelete, BorderLayout.EAST);
		mapProfile.add(new JScrollPane(profile), BorderLayout.CENTER);

		return mapProfile;
	}

	private JPanel initAddDelete() {
		JPanel addDelete = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addDelete.setBackground(Color.WHITE);
		addDelete.add(new JButton("Hinzufügen"));
		addDelete.add(new JButton("Entfernen"));
		addDelete.setPreferredSize(new Dimension(130, 200));
		return addDelete;
	}
	public static void main(String[] args) {
		new MapManagerView();
	}
	/**
	 * Setzt die Karte, die aktuell ausgewählt werden können.
	 * 
	 * @param maps
	 *            Die verfügbaren Karte.
	 */
	public void setAvailableMaps(Set<StreetMap> maps) {
	}
	/**
	 * Setzt die aktuelle Karte auf die angegebene Karte, aktualisiert die Liste
	 * der Profil für die ausgewählte Karte und aktiviert/deaktiviert die
	 * „Import“- und „Löschen“-Buttons, je nachdem, ob es sich um eine
	 * Standardkarte handelt oder nicht.
	 * 
	 * @param map
	 *            Die neue Karte.
	 * @param profiles
	 *            Die Profil für die neue Karte.
	 */
	public void setCurrentMap(StreetMap map, Set<Profile> profiles) {
	}
}
