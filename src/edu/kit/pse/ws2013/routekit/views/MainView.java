package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import edu.kit.pse.ws2013.routekit.controllers.MainController;
import edu.kit.pse.ws2013.routekit.controllers.ProfileMapManager;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.models.CurrentCombinationListener;
import edu.kit.pse.ws2013.routekit.models.ProfileMapCombination;
import edu.kit.pse.ws2013.routekit.models.RouteModel;
import edu.kit.pse.ws2013.routekit.models.RouteModelListener;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Displays the main window on the screen.
 */
public class MainView extends JFrame implements RouteModelListener {
	final float maxcoordinate = Float.MAX_VALUE;
	JTextField startField;
	JTextField targetField;
	private JLabel mapLabel;
	private JLabel profileLabel;
	JFileChooser fileChooser = new JFileChooser();
	private RouteModel routeModel;
	private MapView mapView;

	/**
	 * A constructor that creates a new MainView.
	 */
	public MainView(RouteModel routeModel) {
		super("routeKIT");
		this.routeModel = routeModel;
		routeModel.addRouteListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 700);

		initMenu();

		JSplitPane contentPane = new JSplitPane();
		JPanel left = initLeftPane(routeModel);

		contentPane.add(left, JSplitPane.LEFT);

		JPanel right = initRightPanel(routeModel);
		contentPane.add(right, JSplitPane.RIGHT);

		ProfileMapManager.getInstance().addCurrentCombinationListener(
				new CurrentCombinationListener() {

					@Override
					public void currentCombinationChanged(
							ProfileMapCombination newCombination) {
						setCurrentProfile(newCombination.getProfile());
						setCurrentMap(newCombination.getStreetMap());
						mapView.setEnabled(newCombination.isCalculated());
					}
				});

		setContentPane(contentPane);
		setVisible(true);
	}

	private JPanel initLeftPane(final RouteModel routeModel) {
		JPanel left = new JPanel(new BorderLayout());
		left.setMinimumSize(new Dimension(260, 100));

		JPanel controls = new JPanel(new BorderLayout());
		JPanel hist = new JPanel();
		JButton historyButton = new JButton("Verlauf");
		hist.add(historyButton);
		historyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new HistoryView(MainController.getInstance().getHistory(),
						MainView.this);
			}
		});
		controls.add(hist, BorderLayout.SOUTH);

		JPanel swap = new JPanel();
		JButton swapKnopf = new JButton(new ImageIcon(
				MainView.class.getResource("Knopf.png")));
		swapKnopf.setMargin(new Insets(0, 0, 0, 0));
		swap.add(swapKnopf);
		controls.add(swap, BorderLayout.WEST);
		controls.add(initCoordEntry(), BorderLayout.CENTER);
		swapKnopf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String starttemp = startField.getText();
				startField.setText(targetField.getText());
				targetField.setText(starttemp);
				if (startField.getText().equals("")
						&& targetField.getText().equals("")) {
					startField.setBackground(Color.WHITE);
					targetField.setBackground(Color.WHITE);
				}
				if (!startField.getBackground().equals(
						targetField.getBackground())) {
					Color colortemp = startField.getBackground();
					startField.setBackground(targetField.getBackground());
					targetField.setBackground(colortemp);
				}
				if (startField.getBackground().equals(Color.WHITE)
						&& targetField.getBackground().equals(Color.WHITE)
						&& !startField.getText().equals("")
						&& !targetField.getText().equals("")) {

					MainController.getInstance().setStartAndDestinationPoint(
							routeModel.getDestination(), routeModel.getStart());
				}
			}
		});

		left.add(controls, BorderLayout.NORTH);
		final JList<String> routeDescription = new JList<String>(new String[] {
				"sdfsdfsd", "dsdfsd", "sdf" });
		left.add(routeDescription, BorderLayout.CENTER);
		routeDescription.setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, false,
						false);

				return this;
			}
		});
		return left;
	}

	private JPanel initCoordEntry() {
		JPanel coords = new JPanel(new GridLayout(2, 1));
		JPanel start = new JPanel();
		JPanel target = new JPanel();

		JLabel startLabel = new JLabel("Start");
		JLabel targetLabel = new JLabel("Ziel");
		Dimension startD = startLabel.getPreferredSize();
		Dimension targetD = targetLabel.getPreferredSize();
		Dimension preffered = new Dimension((int) Math.max(startD.getWidth(),
				targetD.getWidth()), (int) Math.max(startD.getHeight(),
				targetD.getHeight()));
		startLabel.setPreferredSize(preffered);
		targetLabel.setPreferredSize(preffered);
		start.add(startLabel);
		startField = new JTextField(15);
		startField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				callController(startField);
			}
		});
		startField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				callController(startField);
			}

			@Override
			public void focusGained(FocusEvent e) {
				startField.setBackground(Color.WHITE);
			}
		});

		start.add(startField);
		target.add(targetLabel);
		targetField = new JTextField(15);
		target.add(targetField);
		targetField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				callController(targetField);

			}
		});
		targetField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				callController(targetField);
			}

			@Override
			public void focusGained(FocusEvent e) {
				targetField.setBackground(Color.WHITE);
			}
		});

		coords.add(start);
		coords.add(target);
		return coords;
	}

	private void callController(JTextField textfield) {
		try {
			Coordinates coordinates = Coordinates.fromString(textfield
					.getText()); // can throw IllegalArgumentException

			if (textfield == startField) {
				MainController.getInstance().setStartPoint(coordinates);
			} else {
				MainController.getInstance().setDestinationPoint(coordinates);
			}
		} catch (IllegalArgumentException e) {
			textfield.setBackground(Color.RED);
			// TODO show e.getMessage() in "route instructions" area
			return;
		}
	}

	private JPanel initRightPanel(RouteModel rm) {
		JPanel right = new JPanel();
		JPanel buttons = new JPanel();
		JButton mapButton = new JButton("Karte");
		mapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainController.getInstance().manageMaps();
			}
		});
		buttons.add(mapButton);
		ProfileMapCombination currentCombination = ProfileMapManager
				.getInstance().getCurrentCombination();
		mapLabel = new JLabel(currentCombination.getStreetMap().getName());
		buttons.add(mapLabel);

		JButton profileButton = new JButton("Profil");
		profileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainController.getInstance().manageProfiles();
			}
		});
		buttons.add(profileButton);
		profileLabel = new JLabel(currentCombination.getProfile().getName());
		buttons.add(profileLabel);
		buttons.setBackground(Color.WHITE);

		right.setLayout(new BorderLayout());
		right.add(buttons, BorderLayout.NORTH);
		mapView = new MapView(MainController.getInstance().getTileSource(), rm);
		right.add(mapView, BorderLayout.CENTER);
		return right;
	}

	private void updateRenderer() {
		mapView.setTileSource(MainController.getInstance().getTileSource());
	}

	private void initMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu routeKIT = new JMenu("routeKIT");

		JMenuItem history = new JMenuItem("Verlauf...");
		history.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new HistoryView(MainController.getInstance().getHistory(),
						MainView.this);
			}
		});
		JMenuItem about = new JMenuItem("Über...");
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutView(MainView.this);
			}
		});
		JMenuItem exit = new JMenuItem("Beenden");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		routeKIT.add(history);
		routeKIT.add(about);
		routeKIT.add(exit);

		JMenu export = new JMenu("Export");
		JMenuItem html = new JMenuItem("HTML...");
		html.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.showDialog(MainView.this, "Exportieren");
				MainController.getInstance().exportHTML(
						fileChooser.getSelectedFile());
			}
		});
		JMenuItem gpx = new JMenuItem("GPX...");
		gpx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.showDialog(MainView.this, "Exportieren");
				MainController.getInstance().exportGPX(
						fileChooser.getSelectedFile());
			}
		});
		export.add(html);
		export.add(gpx);

		JMenu admin = new JMenu("Verwaltung");
		JMenuItem profile = new JMenuItem("Profil...");
		profile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainController.getInstance().manageProfiles();
			}
		});
		JMenuItem map = new JMenuItem("Karte...");
		map.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainController.getInstance().manageMaps();
			}
		});
		JCheckBoxMenuItem osm = new JCheckBoxMenuItem("OSM-Renderer verwenden");
		osm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				AbstractButton aButton = (AbstractButton) event.getSource();
				boolean selected = aButton.getModel().isSelected();
				if (selected) {
					MainController.getInstance().setUseOnlineMaps(true);
				} else {
					MainController.getInstance().setUseOnlineMaps(false);
				}
				updateRenderer();
			}
		});
		admin.add(profile);
		admin.add(map);
		admin.add(osm);

		menu.add(routeKIT);
		menu.add(export);
		menu.add(admin);

		setJMenuBar(menu);
	}

	/**
	 * Updates the display of the current profile.
	 * 
	 * @param profile
	 *            The new profile.
	 */
	public void setCurrentProfile(Profile profile) {
		profileLabel.setText(profile.getName());
	}

	/**
	 * Updates the display of the current map.
	 * 
	 * @param map
	 *            The new map.
	 */
	public void setCurrentMap(StreetMap map) {
		mapLabel.setText(map.getName());
	}

	@Override
	public void routeModelChanged() {
		Coordinates start = routeModel.getStart();
		Coordinates destination = routeModel.getDestination();
		if (start != null) {
			startField
					.setText(start.getLatitude() + " " + start.getLongitude());
		}
		if (destination != null) {
			targetField.setText(destination.getLatitude() + " "
					+ destination.getLongitude());
		}
		;

	}
}
