package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.kit.pse.ws2013.routekit.controllers.ProfileManagerController;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.profiles.VehicleType;

/**
 * Displays the window of the profile management on the screen.
 */
public class ProfileManagerView extends JDialog {
	private JComboBox<String> profilename;
	private JRadioButton bus;
	private JRadioButton motorcycle;
	private JRadioButton truck;
	private JRadioButton car;
	private JSpinner hSpeedspinner;
	private JSpinner srSpeedspinner;
	private JSpinner heightspinner;
	private JSpinner widthspinner;
	private JSpinner weightspinner;
	private JButton deleteButton;
	private int listenerCheck = 0;
	private Profile currentProfile;
	private Set<Profile> availableProfiles;
	private ProfileManagerController pmc;

	/**
	 * A constructor that creates a new ProfileManagerView.
	 * <p>
	 * This constructor does <i>not</i> call {@link #setVisible(boolean)
	 * setVisible(true)}, i.&nbsp;e. it doesn’t block.
	 */
	public ProfileManagerView(Window parent, ProfileManagerController pmc,
			Profile currentProfile, Set<Profile> availableProfiles) {
		super(parent, "Profilverwaltung", ModalityType.APPLICATION_MODAL);
		this.currentProfile = currentProfile;
		this.availableProfiles = availableProfiles;
		this.pmc = pmc;
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

		setContentPane(contentPane);

		setAvailableProfiles(availableProfiles);
		setCurrentProfile(currentProfile);
	}

	private JPanel initNorthPane() {
		JPanel north = new JPanel(new FlowLayout());
		north.setBackground(Color.WHITE);

		JLabel profile = new JLabel("Proﬁl auswählen:");
		profilename = new JComboBox<String>();

		profilename.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (listenerCheck == 0) {
					if (profilename.getSelectedItem() == null) {
						return;
					} else {
						if (!currentProfile.isDefault()) {
							Profile current = writeValues(currentProfile
									.getName());
							pmc.saveTemporaryProfile(current);
						}
						currentProfile = findProfile((String) profilename
								.getSelectedItem());
						if (currentProfile == null) {
							throw new IllegalArgumentException(
									"The current profile is null!");
						}
						pmc.changeTemporaryProfile(currentProfile.getName());

					}
				}
			}
		});

		profilename.setMinimumSize(new Dimension(250, 26));
		profilename.setPreferredSize(new Dimension(250, 26));

		north.add(profile);
		north.add(profilename);
		deleteButton = new JButton("Löschen");
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pmc.deleteCurrentTemporaryProfile();
			}
		});
		north.add(deleteButton);
		JButton neuButton = new JButton("Neu");
		neuButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String result = (String) JOptionPane.showInputDialog(
						ProfileManagerView.this,
						"Geben Sie einen Namen für das neue Profil ein:",
						"Name", JOptionPane.QUESTION_MESSAGE, null, null, null);
				if ((result != null) && (result.length() > 0)) {
					if (!currentProfile.isDefault()) {
						Profile current = writeValues(currentProfile.getName());
						pmc.saveTemporaryProfile(current);
					}
					pmc.changeTemporaryProfile(result);
					currentProfile = findProfile(result);
					if (currentProfile == null) {
						throw new IllegalArgumentException(
								"The current profile is null!");
					}

				}
			}

		});
		north.add(neuButton);

		return north;
	}

	private Profile findProfile(String name) {
		for (Profile p : availableProfiles) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	private Profile writeValues(String currentProfileName) {
		Profile current = new Profile(currentProfileName, selectedButton(),
				(int) heightspinner.getValue(), (int) widthspinner.getValue(),
				(int) weightspinner.getValue(), (int) hSpeedspinner.getValue(),
				(int) srSpeedspinner.getValue());
		return current;
	}

	private VehicleType selectedButton() {
		if (car.isSelected()) {
			return VehicleType.Car;
		}
		if (bus.isSelected()) {
			return VehicleType.Bus;
		}
		if (truck.isSelected()) {
			return VehicleType.Truck;
		}
		return VehicleType.Motorcycle;
	}

	private JPanel initSouthPane() {
		JPanel south = new JPanel(new GridLayout(1, 2));
		south.setBackground(Color.WHITE);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		buttons.setBackground(Color.WHITE);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int showOptionDialog = JOptionPane
						.showOptionDialog(
								ProfileManagerView.this,
								"Sie speichern hiermit alle \n"
										+ "vorgenommenen Änderungen für alle Profile.\n"
										+ warning()
										+ "Wollen sie die Änderungen vornehmen?",
								"Bestätigung", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null,
								new String[] { "Ja", "Nein" }, "Nein");
				if (showOptionDialog == JOptionPane.YES_OPTION) {
					if (!currentProfile.isDefault()) {
						Profile current = writeValues(currentProfile.getName());
						pmc.saveTemporaryProfile(current);
					}
					pmc.saveAllChanges();
					ProfileManagerView.this.dispose();
				}
			}
		});
		buttons.add(okButton);
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

	private String warning() {
		if (pmc.getDeletionTime() == 0) {
			return "";
		}
		return "Sie löschen " + pmc.getDeletionTime() + "Stunden an Arbeit.\n";
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
		currentProfile = profile;
		listenerCheck++;
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
		profilename.setSelectedItem(profile.getName());

		if (profile.isDefault()) {
			deleteButton.setEnabled(false);
			bus.setEnabled(false);
			car.setEnabled(false);
			truck.setEnabled(false);
			motorcycle.setEnabled(false);
			heightspinner.setEnabled(false);
			hSpeedspinner.setEnabled(false);
			weightspinner.setEnabled(false);
			widthspinner.setEnabled(false);
			srSpeedspinner.setEnabled(false);
		} else {
			deleteButton.setEnabled(true);
			bus.setEnabled(true);
			car.setEnabled(true);
			truck.setEnabled(true);
			motorcycle.setEnabled(true);
			heightspinner.setEnabled(true);
			hSpeedspinner.setEnabled(true);
			weightspinner.setEnabled(true);
			widthspinner.setEnabled(true);
			srSpeedspinner.setEnabled(true);
		}
		listenerCheck--;
	}

	/**
	 * Sets the profiles that can be currently selected.
	 * 
	 * @param profiles
	 *            The available profiles.
	 */
	public void setAvailableProfiles(Set<Profile> profiles) {
		listenerCheck++;
		profilename.removeAllItems();
		List<Profile> sortedProfiles = new ArrayList<>(profiles);
		Collections.sort(sortedProfiles, new Comparator<Profile>() {
			@Override
			public int compare(Profile o1, Profile o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Profile p : sortedProfiles) {
			profilename.addItem(p.getName());
		}
		availableProfiles = profiles;
		listenerCheck--;
	}
}
