package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Zeigt das Fenster der Profilverwaltung auf dem Bildschirm an.
 */
public class ProfileManagerView extends JDialog {
	private JComboBox<Object> profilename;
	private JRadioButton bus;
	private JRadioButton motorcycle;
	private JRadioButton truck;
	private JRadioButton car;
	private JSpinner hSpeedspinner;
	private JSpinner srSpeedspinner;
	private JSpinner heightspinner;
	private JSpinner widthspinner;
	private JSpinner weightspinner;

	/**
	 * A constructor that creates a new ProfileManagerView.
	 */
	public ProfileManagerView(Window parent) {
		super(parent, "Profilverwaltung", ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

		// ......................................
		profilename.addItem((Profile.defaultCar.getName()));
		profilename.addItem((Profile.defaultTruck.getName()));
		setCurrentProfile(Profile.defaultCar);
		// .....................................

		setContentPane(contentPane);
		setVisible(true);
	}

	private JPanel initNorthPane() {
		JPanel north = new JPanel(new FlowLayout());
		north.setBackground(Color.WHITE);

		JLabel profile = new JLabel("Proﬁl auswählen:");
		profilename = new JComboBox<>();
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

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		buttons.setBackground(Color.WHITE);

		buttons.add(new JButton("OK"));
		JButton cancel = new JButton("Abbrechen");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttons.add(cancel);

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

		JLabel vehicleType = new JLabel("Fahrzeugtyp:");
		car = new JRadioButton("PKW");
		car.setBackground(Color.WHITE);
		truck = new JRadioButton("LKW");
		truck.setBackground(Color.WHITE);
		bus = new JRadioButton("Bus");
		bus.setBackground(Color.WHITE);
		motorcycle = new JRadioButton("Motorrad");
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
		JPanel highway = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		highway.setBackground(Color.WHITE);
		JLabel highwaySpeed = new JLabel(
				"Durchschnittsgeschwindigkeit auf der Autobahn:");
		hSpeedspinner = new JSpinner(new SpinnerNumberModel(60, 0, 300, 5));
		hSpeedspinner.setPreferredSize(new Dimension(50, 20));
		JLabel kmPerh = new JLabel("km/h");
		highway.add(highwaySpeed);
		highway.add(hSpeedspinner);
		highway.add(kmPerh);
		return highway;
	}

	private JPanel innerSecondaryRoadPane() {
		JPanel secondaryRoad = new JPanel(
				new FlowLayout(FlowLayout.LEFT, 15, 5));
		secondaryRoad.setBackground(Color.WHITE);
		JLabel secondaryRoadSpeed = new JLabel(
				"Durchschnittsgeschwindigkeit auf der Landstraße:");
		srSpeedspinner = new JSpinner(new SpinnerNumberModel(50, 0, 150, 5));
		srSpeedspinner.setPreferredSize(new Dimension(50, 20));
		JLabel kmPerh = new JLabel("km/h");
		secondaryRoad.add(secondaryRoadSpeed);
		secondaryRoad.add(srSpeedspinner);
		secondaryRoad.add(kmPerh);
		return secondaryRoad;
	}

	private JPanel innerHeightPane() {
		JPanel height = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		height.setBackground(Color.WHITE);
		JLabel vehicleHeight = new JLabel("Höhe des Fahrzeugs:");
		heightspinner = new JSpinner(new SpinnerNumberModel(100, 0, 400, 10));
		heightspinner.setPreferredSize(new Dimension(50, 20));
		JLabel cm = new JLabel("cm");
		height.add(vehicleHeight);
		height.add(heightspinner);
		height.add(cm);
		return height;
	}

	private JPanel innerWidthPane() {
		JPanel width = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		width.setBackground(Color.WHITE);
		JLabel vehicleWidth = new JLabel("Breite des Fahrzeugs:");
		widthspinner = new JSpinner(new SpinnerNumberModel(150, 0, 300, 10));
		widthspinner.setPreferredSize(new Dimension(50, 20));
		JLabel cm = new JLabel("cm");
		width.add(vehicleWidth);
		width.add(widthspinner);
		width.add(cm);
		return width;
	}

	private JPanel innerWeightPane() {
		JPanel weight = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		weight.setBackground(Color.WHITE);
		JLabel vehicleWeight = new JLabel("Gewicht des Fahrzeugs:");
		weightspinner = new JSpinner(
				new SpinnerNumberModel(1500, 0, 50000, 100));
		weightspinner.setPreferredSize(new Dimension(70, 20));
		JLabel kg = new JLabel("kg");
		weight.add(vehicleWeight);
		weight.add(weightspinner);
		weight.add(kg);
		return weight;
	}

	/**
	 * Sets the current profile to the specified profile, loads its values ​​in
	 * the input fields, enables / disables the input elements and the
	 * „Löschen“-Button depending on whether it is a default profile or not.
	 * 
	 * @param profile
	 *            The new Profile.
	 */
	public void setCurrentProfile(Profile profile) {
		switch (profile.getVehicleType()) {
		case Bus:
			bus.setSelected(true);
			break;
		case Car:
			car.setSelected(true);
			break;
		case Truck:
			truck.setSelected(true);
			break;
		case Motorcycle:
			motorcycle.setSelected(true);
			break;
		default:
			break;
		}
		hSpeedspinner.setValue(profile.getSpeedHighway());
		heightspinner.setValue(profile.getHeight());
		srSpeedspinner.setValue(profile.getSpeedRoad());
		widthspinner.setValue(profile.getWidth());
		weightspinner.setValue(profile.getWeight());

	}

	/**
	 * Sets the profiles that can be currently selected.
	 * 
	 * @param profiles
	 *            The available profiles.
	 */
	public void setAvailableProfiles(List<Profile> profiles) {
		profilename.removeAllItems();
		for (Profile p : profiles) {
			profilename.addItem(p.getName());
		}
	}
}
