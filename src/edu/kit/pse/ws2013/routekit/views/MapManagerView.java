package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

import edu.kit.pse.ws2013.routekit.controllers.ManagementActions;
import edu.kit.pse.ws2013.routekit.controllers.MapManagerController;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.TimeUtil;

/**
 * Displays the window of the map management on the screen.
 */
public class MapManagerView extends JDialog {
	private static final long serialVersionUID = 1L;
	private JComboBox<Object> mapname;
	private DefaultListModel<String> listenModell;
	private JList<String> profile;
	private JFileChooser fileChooser = new JFileChooser();
	private JButton importButton;
	private JButton update;
	private JButton delete;
	private int listenerCheck = 0;
	private MapManagerController mmc;
	private final KeyListener escEnterListener = new KeyAdapter() {
		@Override
		public void keyPressed(java.awt.event.KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ENTER:
				ok();
				break;

			case KeyEvent.VK_ESCAPE:
				dispose();
				break;
			}
		};
	};

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

		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".osm");
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

		addKeyListener(escEnterListener);
		contentPane.addKeyListener(escEnterListener);

		setAvailableMaps(maps);
		setCurrentMap(currentMap, currentMapProfiles);
	}

	private JPanel initNorthPane() {
		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		north.setBackground(Color.WHITE);
		north.addKeyListener(escEnterListener);
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
		mapname.addKeyListener(escEnterListener);

		north.add(map);
		north.add(mapname);
		return north;
	}

	private JPanel initCenterPane() {
		JPanel center = new JPanel(new BorderLayout(10, 10));
		center.setBackground(Color.WHITE);

		JPanel buttons = initButtonsPane();
		JPanel mapProfile = initMapProfilePane();
		mapProfile.addKeyListener(escEnterListener);

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
				ok();
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

				do {
					int result = JOptionPane.showConfirmDialog(
							MapManagerView.this, myPanel,
							"Eingabe Pfad und Name",
							JOptionPane.OK_CANCEL_OPTION);
					if (result != JOptionPane.OK_OPTION
							|| path.getText().isEmpty()
							|| name.getText().isEmpty()) {
						return;
					}
					try {
						File newfile = new File(path.getText());
						mmc.importMap(name.getText(), newfile);
						break;
					} catch (IllegalArgumentException ex) {
						JOptionPane
								.showMessageDialog(
										MapManagerView.this,
										"Ungültiger Name – bitte geben Sie einen anderen Namen ein.",
										"Fehler", JOptionPane.ERROR_MESSAGE);
						continue;
					}
				} while (true);
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
						File newfile = new File(pathUpdate.getText());
						mmc.importMap((String) mapname.getSelectedItem(),
								newfile);
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
		profile.addKeyListener(escEnterListener);

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
		List<StreetMap> sortedMaps = new ArrayList<>(maps);
		Collections.sort(sortedMaps, new Comparator<StreetMap>() {
			@Override
			public int compare(StreetMap o1, StreetMap o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (StreetMap m : sortedMaps) {
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
		List<Profile> sortedProfiles = new ArrayList<>(profiles);
		Collections.sort(sortedProfiles, new Comparator<Profile>() {
			@Override
			public int compare(Profile o1, Profile o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Profile p : sortedProfiles) {
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

	private class Dialog extends JDialog {
		private static final long serialVersionUID = 1L;
		private JButton ok;
		public boolean clickedOk = false;

		public Dialog(Window parent) {
			super(parent, "Kartenverwaltung", ModalityType.APPLICATION_MODAL);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setLocationRelativeTo(getParent());
			setResizable(false);

			JPanel contentPane = new JPanel(new BorderLayout());

			JPanel center = initDialogCenterPane();
			JPanel south = initDialogSouthPane();

			if (center == null) {
				// no changes, don’t show the dialog
				clickedOk = true;
				dispose();
				return;
			}

			contentPane.add(center, BorderLayout.CENTER);
			contentPane.add(south, BorderLayout.SOUTH);

			setContentPane(contentPane);
			pack();
			new Thread("MapManagerView Button Timeout Thread") {

				@Override
				public void run() {
					final int timeout;
					if (java.lang.management.ManagementFactory
							.getRuntimeMXBean().getInputArguments().toString()
							.indexOf("jdwp") >= 0) {
						timeout = 0;
					} else {
						timeout = 5;
					}
					try {
						for (int sek = timeout; sek >= 1; sek--) {
							ok.setText("OK (" + sek + ")");
							Thread.sleep(1000);
						}
					} catch (InterruptedException a) {
					}
					ok.setText("OK");
					ok.setEnabled(true);
				}
			}.start();
			setLocationRelativeTo(parent);
			setVisible(true);
		}

		private JPanel initDialogSouthPane() {
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,
					10));
			buttons.setBackground(Color.WHITE);
			ok = new JButton("OK");
			ok.setEnabled(false);
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					clickedOk = true;
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
			JTextPane textPane = new JTextPane();
			StringBuilder text = new StringBuilder(
					"Sie haben die folgenden Operationen ausgewählt:\n");
			ManagementActions diff = mmc.getChanges();
			int i = 0;
			Set<StreetMap> deletedMaps = diff.getDeletedMaps();
			Set<? extends StreetMap> newOrUpdatedMaps = diff
					.getNewOrUpdatedMaps();
			Set<ProfileMapCombination> deletedPrecalculations = diff
					.getDeletedPrecalculations();
			Set<ProfileMapCombination> newPrecalculations = diff
					.getNewPrecalculations();
			if (!deletedMaps.isEmpty()) {
				text.append(++i);
				text.append(". Löschen der folgenden Karten:\n");
				for (StreetMap map : deletedMaps) {
					text.append("    • ");
					text.append(map.getName());
					text.append('\n');
				}
			}
			if (!newOrUpdatedMaps.isEmpty()) {
				text.append(++i);
				text.append(". Hinzufügen oder Aktualisieren der folgenden Karten:\n");
				for (StreetMap map : newOrUpdatedMaps) {
					text.append("    • ");
					text.append(map.getName());
					text.append('\n');
				}
			}
			if (!deletedPrecalculations.isEmpty()) {
				text.append(++i);
				text.append(". Löschen der folgenden Vorberechnungen:\n");
				int interval = 0;
				for (ProfileMapCombination precalculation : deletedPrecalculations) {
					text.append("    • ");
					text.append(precalculation.getStreetMap().getName());
					text.append(" + ");
					text.append(precalculation.getProfile().getName());
					text.append('\n');
					interval += precalculation.getCalculationTime();
				}
				if (interval > 0) {
					text.append("   (diese Vorberechnungen benötigten insgesamt");
					TimeUtil.timeSpanString(text, interval);
					text.append(")\n");
				}
			}
			if (!newPrecalculations.isEmpty()) {
				text.append(++i);
				text.append(". Neu Berechnen der folgenden Vorberechnungen:\n");
				for (ProfileMapCombination precalculation : newPrecalculations) {
					text.append("    • ");
					text.append(precalculation.getStreetMap().getName());
					text.append(" + ");
					text.append(precalculation.getProfile().getName());
					text.append('\n');
				}
			}
			if (i == 0) {
				// diff is completely empty, dispose immediately
				return null;
			}
			text.append("Sind Sie sicher, dass Sie diese Operationen durchführen wollen?");
			textPane.setText(text.toString());
			Font displayFont = new Font("Serif", Font.ROMAN_BASELINE, 14);
			StyledDocument doc = textPane.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_LEFT);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
			textPane.setEditable(false);
			textPane.setFont(displayFont);
			information.add(textPane);
			return information;
		}
	}

	private void ok() {
		if (new Dialog(MapManagerView.this).clickedOk) {
			ProgressDialog p = new ProgressDialog(MapManagerView.this, true);
			ProgressReporter reporter = new ProgressReporter();
			reporter.addProgressListener(p);
			reporter.pushTask("Speichere Änderungen");
			mmc.saveAllChanges(reporter);
			p.setVisible(true);
			MapManagerView.this.dispose();
		}
	}
}
