package views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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

		JList<String> profile = new JList<String>(
				new String[] { "Proﬁl auswählen:" });
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
		JPanel south = new JPanel(new GridLayout(1, 2));
		south.setBackground(Color.WHITE);

		JPanel buttons = new JPanel(new FlowLayout());
		buttons.setBackground(Color.WHITE);

		buttons.add(new JButton("OK"));
		buttons.add(new JButton("Abbrechen"));

		south.add(buttons);
		return south;
	}

	private JPanel initCenterPane() {
		JPanel center = new JPanel(new BorderLayout());

		JPanel type = innerTypePane();
		JPanel dimensions = innerDimentionsPane();
		JPanel velocity = innerVelocityPane();

		center.add(type, BorderLayout.NORTH);
		center.add(dimensions, BorderLayout.CENTER);
		center.add(velocity, BorderLayout.SOUTH);

		return center;
	}

	private JPanel innerTypePane() {
		JPanel type = new JPanel(new FlowLayout());
		type.setBackground(Color.WHITE);

		JList<String> vehicleType = new JList<String>(
				new String[] { "Fahrzeugtyp:" });
		JRadioButton car = new JRadioButton("PKW");
		car.setBackground(Color.WHITE);
		JRadioButton truck = new JRadioButton("LKW");
		truck.setBackground(Color.WHITE);
		JRadioButton bus = new JRadioButton("Bus");
		bus.setBackground(Color.WHITE);
		JRadioButton motorcycle = new JRadioButton("Motorrad");
		motorcycle.setBackground(Color.WHITE);

		type.add(vehicleType);
		type.add(car);
		type.add(truck);
		type.add(bus);
		type.add(motorcycle);

		ButtonGroup group = new ButtonGroup();
		group.add(car);
		group.add(truck);
		group.add(bus);
		group.add(motorcycle);

		return type;
	}

	private JPanel innerDimentionsPane() {
		JPanel dimentions = new JPanel(new GridLayout(4, 1));
		dimentions.setBackground(Color.WHITE);
		JPanel height = innerHeightPane();
		JPanel width = innerWidthPane();
		JPanel weight = innerWeightPane();

		dimentions.add(height);
		dimentions.add(width);
		dimentions.add(weight);
		return dimentions;
	}

	private JPanel innerVelocityPane() {
		JPanel velocity = new JPanel(new GridLayout(3, 1));
		velocity.setBackground(Color.WHITE);
		JPanel highway = innerHighwayPane();
		JPanel secondaryRoad = innerSecondaryRoadPane();

		velocity.add(highway);
		velocity.add(secondaryRoad);

		return velocity;
	}

	private JPanel innerHighwayPane() {
		JPanel highway = new JPanel(new FlowLayout(FlowLayout.LEFT));
		highway.setBackground(Color.WHITE);
		JList<String> highwaySpeed = new JList<String>(
				new String[] { "Durchschnittsgeschwindigkeit auf der Autobahn:" });
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(60, 0, 300, 5));
		spinner.setPreferredSize(new Dimension(50, 20));
		JList<String> kmPerh = new JList<String>(new String[] { "km/h" });
		highway.add(highwaySpeed);
		highway.add(spinner);
		highway.add(kmPerh);
		return highway;
	}

	private JPanel innerSecondaryRoadPane() {
		JPanel secondaryRoad = new JPanel(new FlowLayout(FlowLayout.LEFT));
		secondaryRoad.setBackground(Color.WHITE);
		JList<String> secondaryRoadSpeed = new JList<String>(
				new String[] { "Durchschnittsgeschwindigkeit auf der Landstraße:" });
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(50, 0, 150, 5));
		spinner.setPreferredSize(new Dimension(50, 20));
		JList<String> kmPerh = new JList<String>(new String[] { "km/h" });
		secondaryRoad.add(secondaryRoadSpeed);
		secondaryRoad.add(spinner);
		secondaryRoad.add(kmPerh);
		return secondaryRoad;
	}

	private JPanel innerHeightPane() {
		JPanel height = new JPanel(new FlowLayout(FlowLayout.LEFT));
		height.setBackground(Color.WHITE);
		JList<String> vehicleHeight = new JList<String>(
				new String[] { "Höhe des Fahrzeugs:" });
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 4, 0.1));
		spinner.setPreferredSize(new Dimension(50, 20));
		JList<String> meter = new JList<String>(new String[] { "m" });
		height.add(vehicleHeight);
		height.add(spinner);
		height.add(meter);
		return height;
	}

	private JPanel innerWidthPane() {
		JPanel width = new JPanel(new FlowLayout(FlowLayout.LEFT));
		width.setBackground(Color.WHITE);
		JList<String> vehicleWidth = new JList<String>(
				new String[] { "Breite des Fahrzeugs:" });
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(1.5, 0, 3, 0.1));
		spinner.setPreferredSize(new Dimension(50, 20));
		JList<String> ton = new JList<String>(new String[] { "m" });
		width.add(vehicleWidth);
		width.add(spinner);
		width.add(ton);
		return width;
	}

	private JPanel innerWeightPane() {
		JPanel weight = new JPanel(new FlowLayout(FlowLayout.LEFT));
		weight.setBackground(Color.WHITE);
		JList<String> vehicleWeight = new JList<String>(
				new String[] { "Gewicht des Fahrzeugs:" });
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(1.5, 0, 50, 0.5));
		spinner.setPreferredSize(new Dimension(50, 20));
		JList<String> meter = new JList<String>(new String[] { "t" });
		weight.add(vehicleWeight);
		weight.add(spinner);
		weight.add(meter);
		return weight;
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
