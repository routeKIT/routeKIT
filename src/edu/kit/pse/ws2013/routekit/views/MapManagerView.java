package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import edu.kit.pse.ws2013.routekit.controllers.MapManagerController;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;

/**
 * Displays the window of the map management on the screen.
 */
public class MapManagerView extends JDialog {
	private JComboBox<Object> mapname;
	private DefaultListModel<String> listenModell;
	private JList<String> profile;
	private JFileChooser fileChooser = new JFileChooser();
	private JButton importButton;
	private JButton update;
	private JButton delete;

	/**
	 * A constructor that creates a new MapManagerView.
	 * <p>
	 * This constructor does <i>not</i> call {@link #setVisible(boolean)
	 * setVisible(true)}, i.&nbsp;e. it doesn’t block.
	 */
	public MapManagerView(Window parent, MapManagerController mmc) {
		super(parent, "Kartenverwaltung", ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(getParent());
		setResizable(false);

		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".osm");
			}

			@Override
			public String getDescription() {
				return "OSM(*.osm)";
			}

		});

		JPanel contentPane = new JPanel(new BorderLayout());

		JPanel north = initNorthPane();
		JPanel center = initCenterPane(mmc);
		JPanel south = initSouthPane();

		contentPane.add(north, BorderLayout.NORTH);
		contentPane.add(south, BorderLayout.SOUTH);
		contentPane.add(center, BorderLayout.CENTER);

		setContentPane(contentPane);
	}

	private JPanel initNorthPane() {
		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		north.setBackground(Color.WHITE);
		JLabel map = new JLabel("Karte auswählen:");
		mapname = new JComboBox<>();
		mapname.setMinimumSize(new Dimension(250, 26));
		mapname.setPreferredSize(new Dimension(250, 26));

		north.add(map);
		north.add(mapname);
		return north;
	}

	private JPanel initCenterPane(MapManagerController mmc) {
		JPanel center = new JPanel(new BorderLayout(10, 10));
		center.setBackground(Color.WHITE);

		JPanel buttons = initButtonsPane(mmc);
		JPanel mapProfile = initMapProfilePane(mmc);

		center.add(buttons, BorderLayout.NORTH);
		center.add(mapProfile, BorderLayout.CENTER);
		center.add(new JPanel(), BorderLayout.WEST);

		return center;
	}

	private JPanel initSouthPane() {
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		south.setBackground(Color.WHITE);

		south.add(new JButton("OK"));
		JButton cancel = new JButton("Abbrechen");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		south.add(cancel);
		return south;
	}

	private JPanel initButtonsPane(final MapManagerController mmc) {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.setBackground(Color.WHITE);

		importButton = new JButton("Importieren");
		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton pathButton = new JButton("Suche...");
				final JTextField path = new JTextField(20);
				pathButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						fileChooser.showDialog(MapManagerView.this,
								"Importieren");
						if (fileChooser.getSelectedFile() == null) {
							return;
						}
						path.setText(fileChooser.getSelectedFile()
								.getAbsolutePath());
					}
				});
				JTextField name = new JTextField(20);

				JPanel myPanel = new JPanel();
				myPanel.add(pathButton);
				myPanel.add(new JLabel("Pfad:"));
				myPanel.add(path);
				myPanel.add(Box.createHorizontalStrut(15));
				myPanel.add(new JLabel("Name der Karte:"));
				myPanel.add(name);

				int result = JOptionPane.showConfirmDialog(null, myPanel,
						"Eingabe Pfad und Name", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					if (!path.getText().equals("")
							&& !name.getText().equals("")) {
						mmc.importMap(name.getText(),
								fileChooser.getSelectedFile());
					}
				}
			}
		});
		buttons.add(importButton);
		update = new JButton("Aktualisieren");
		buttons.add(update);
		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton pathUpdateButton = new JButton("Suche...");
				final JTextField pathUpdate = new JTextField(20);
				pathUpdateButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						fileChooser.showDialog(MapManagerView.this,
								"Aktualisieren");
						if (fileChooser.getSelectedFile() == null) {
							return;
						}
						pathUpdate.setText(fileChooser.getSelectedFile()
								.getAbsolutePath());
					}
				});
				JPanel myPanel = new JPanel();
				myPanel.add(pathUpdateButton);
				myPanel.add(new JLabel("Pfad:"));
				myPanel.add(pathUpdate);
				int result = JOptionPane.showConfirmDialog(null, myPanel,
						"Eingabe Pfad", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					if (!pathUpdate.getText().equals("")) {
						mmc.importMap((String) mapname.getSelectedItem(),
								fileChooser.getSelectedFile());
					}
				}
			}
		});
		delete = new JButton("Löschen");
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mmc.deleteCurrentMap();
			}
		});
		buttons.add(delete);

		return buttons;
	}

	private JPanel initMapProfilePane(MapManagerController mmc) {
		JPanel mapProfile = new JPanel(new BorderLayout(10, 10));
		mapProfile.setBackground(Color.WHITE);
		JPanel addDelete = initAddDelete(mmc);
		listenModell = new DefaultListModel<String>();
		profile = new JList<String>(listenModell);
		profile.setBackground(Color.lightGray);

		mapProfile.add(new JLabel("Profile für diese Karte:"),
				BorderLayout.NORTH);
		mapProfile.add(addDelete, BorderLayout.EAST);
		mapProfile.add(new JScrollPane(profile), BorderLayout.CENTER);

		return mapProfile;
	}

	private JPanel initAddDelete(final MapManagerController mmc) {
		JPanel addDelete = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addDelete.setBackground(Color.WHITE);
		JButton addProfile = new JButton("Hinzufügen");
		addProfile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		addDelete.add(addProfile);
		JButton remove = new JButton("Entfernen");
		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// mmc.removeProfile(profile.getSelectedValue());
			}
		});
		addDelete.add(remove);
		addDelete.setPreferredSize(new Dimension(130, 200));
		return addDelete;
	}

	/**
	 * Sets the maps that can be currently selected.
	 * 
	 * @param maps
	 *            The available maps.
	 */
	public void setAvailableMaps(Set<StreetMap> maps) {
		mapname.removeAllItems();
		for (StreetMap m : maps) {
			mapname.addItem(m.getName());
		}

	}

	/**
	 * Sets the current map to the specified map, updates the list of profiles
	 * for the selected map, enable / disable the „Import“- und
	 * „Löschen“-Buttons depending on whether it is a default map or not.
	 * 
	 * @param map
	 *            The new map.
	 * @param profiles
	 *            The profiles for the new map.
	 */
	public void setCurrentMap(StreetMap map, Set<Profile> profiles) {
		listenModell.clear();
		for (Profile p : profiles) {
			listenModell.addElement(p.getName());
		}
		mapname.setSelectedItem(map.getName());
		if (map.isDefault()) {
			delete.setEnabled(false);
			update.setEnabled(false);
		}
	}
}
