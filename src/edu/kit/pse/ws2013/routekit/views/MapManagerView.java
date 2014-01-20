package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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
	private int listenerCheck = 0;
	private MapManagerController mmc;

	/**
	 * A constructor that creates a new MapManagerView.
	 * <p>
	 * This constructor does <i>not</i> call {@link #setVisible(boolean)
	 * setVisible(true)}, i.&nbsp;e. it doesn’t block.
	 */
	public MapManagerView(Window parent, MapManagerController mmc,
			StreetMap currentMap, Set<StreetMap> maps,
			Set<Profile> currentMapProfiles) {
		super(parent, "Kartenverwaltung", ModalityType.APPLICATION_MODAL);
		this.mmc = mmc;
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
				return "OSM (*.osm)";
			}

		});

		JPanel contentPane = new JPanel(new BorderLayout());

		JPanel north = initNorthPane();
		JPanel center = initCenterPane();
		JPanel south = initSouthPane();

		contentPane.add(north, BorderLayout.NORTH);
		contentPane.add(south, BorderLayout.SOUTH);
		contentPane.add(center, BorderLayout.CENTER);

		setContentPane(contentPane);

		setAvailableMaps(maps);
		setCurrentMap(currentMap, currentMapProfiles);
	}

	private JPanel initNorthPane() {
		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		north.setBackground(Color.WHITE);
		JLabel map = new JLabel("Karte auswählen:");
		mapname = new JComboBox<>();
		mapname.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (listenerCheck == 0) {
					if (mapname.getSelectedItem() == null) {
						return;
					} else {
						mmc.changeMap((String) mapname.getSelectedItem());
					}
				}
			}
		});
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
		JPanel space = new JPanel();
		space.setBackground(Color.WHITE);
		center.add(space, BorderLayout.WEST);

		return center;
	}

	private JPanel initSouthPane() {
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		south.setBackground(Color.WHITE);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Dialog(MapManagerView.this);
				setVisible(false);
			}
		});
		south.add(okButton);
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

	private JPanel initButtonsPane() {
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

				int result = JOptionPane.showConfirmDialog(MapManagerView.this,
						myPanel, "Eingabe Pfad und Name",
						JOptionPane.OK_CANCEL_OPTION);
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
				int result = JOptionPane.showConfirmDialog(MapManagerView.this,
						myPanel, "Eingabe Pfad", JOptionPane.OK_CANCEL_OPTION);
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

	private JPanel initMapProfilePane() {
		JPanel mapProfile = new JPanel(new BorderLayout(10, 10));
		mapProfile.setBackground(Color.WHITE);
		JPanel addDelete = initAddDelete();
		listenModell = new DefaultListModel<String>();
		profile = new JList<String>(listenModell);
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
		JButton addProfile = new JButton("Hinzufügen");
		addProfile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mmc.addProfile();
			}
		});
		addDelete.add(addProfile);
		JButton remove = new JButton("Entfernen");
		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (profile.getSelectedValue() != null) {
					mmc.removeProfile(profile.getSelectedValue());
				}
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
		listenerCheck++;
		mapname.removeAllItems();
		for (StreetMap m : maps) {
			mapname.addItem(m.getName());
		}
		listenerCheck--;
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
		listenerCheck++;
		listenModell.clear();
		for (Profile p : profiles) {
			listenModell.addElement(p.getName());
		}
		mapname.setSelectedItem(map.getName());
		if (map.isDefault()) {
			delete.setEnabled(false);
			update.setEnabled(false);
		} else {
			delete.setEnabled(true);
			update.setEnabled(true);
		}
		listenerCheck--;
	}

	public void addProfile(Profile p) {
		listenModell.addElement(p.getName());
	}

	private class Dialog extends JDialog {
		public Dialog(Window parent) {
			super(parent, "Kartenverwaltung", ModalityType.APPLICATION_MODAL);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(400, 200);
			setLocationRelativeTo(getParent());
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());

			JPanel center = initDialogCenterPane();
			JPanel south = initDialogSouthPane();

			contentPane.add(center, BorderLayout.CENTER);
			contentPane.add(south, BorderLayout.SOUTH);

			setContentPane(contentPane);
			setVisible(true);
		}

		private JPanel initDialogSouthPane() {
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,
					10));
			buttons.setBackground(Color.WHITE);
			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					mmc.saveAllChanges();
					dispose();
				}
			});
			JButton cancel = new JButton("Abbrechen");
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			buttons.add(ok);
			buttons.add(cancel);
			return buttons;
		}

		private JPanel initDialogCenterPane() {
			JPanel information = new JPanel();
			information.setBackground(Color.WHITE);
			JTextPane text = new JTextPane();
			text.setText("Sie haben die folgenden Operationen ausgewählt: \n"
					+ "Sind Sie sicher, dass Sie diese Operationen durchführen wollen?");
			Font displayFont = new Font("Serif", Font.ROMAN_BASELINE, 14);
			StyledDocument doc = text.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
			text.setEditable(false);
			text.setFont(displayFont);
			information.add(text);
			return information;
		}
	}
}
