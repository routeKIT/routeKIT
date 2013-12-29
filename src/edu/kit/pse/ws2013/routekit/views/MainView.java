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
import edu.kit.pse.ws2013.routekit.history.History;
import edu.kit.pse.ws2013.routekit.map.StreetMap;
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

	/**
	 * A constructor that creates a new MainView.
	 */
	public MainView() {
		super("routeKIT");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 700);

		initMenu();

		JSplitPane contentPane = new JSplitPane();
		JPanel left = initLeftPane();

		contentPane.add(left, JSplitPane.LEFT);

		JPanel right = initRightPanel();
		contentPane.add(right, JSplitPane.RIGHT);

		setContentPane(contentPane);
		setVisible(true);
	}

	private JPanel initLeftPane() {
		JPanel left = new JPanel(new BorderLayout());
		left.setMinimumSize(new Dimension(260, 100));

		JPanel controls = new JPanel(new BorderLayout());
		JPanel hist = new JPanel();
		JButton historyButton = new JButton("Verlauf");
		hist.add(historyButton);
		historyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new HistoryView(new History(), MainView.this);
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
				// MainController.getInstance().setStartAndDestinationPoint(new
				// Coordinates(startField, ta), destination)
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
		startField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String[] strings = startField.getText().split("\\s+");
				if (strings.length != 2) {
					startField.setBackground(Color.RED);
					return;
				}
				float xcoordinate = checkCoords(strings[0], 90f);
				float ycoordinate = checkCoords(strings[1], 180f);
				if (Float.compare(xcoordinate, maxcoordinate) == 0
						|| Float.compare(ycoordinate, maxcoordinate) == 0) {
					startField.setBackground(Color.RED);
					return;
				}
				MainController.getInstance().setStartPoint(
						new Coordinates(xcoordinate, ycoordinate));
			}

			public void focusGained(FocusEvent e) {
				startField.setBackground(Color.WHITE);
			}
		});

		start.add(startField);
		target.add(targetLabel);
		targetField = new JTextField(15);
		target.add(targetField);
		targetField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String[] strings = targetField.getText().split("\\s+");
				if (strings.length != 2) {
					targetField.setBackground(Color.RED);
					return;
				}
				float xcoordinate = checkCoords(strings[0], 90f);
				float ycoordinate = checkCoords(strings[1], 180f);
				if (Float.compare(xcoordinate, maxcoordinate) == 0
						|| Float.compare(ycoordinate, maxcoordinate) == 0) {
					targetField.setBackground(Color.RED);
					return;
				}
				MainController.getInstance().setDestinationPoint(
						new Coordinates(xcoordinate, ycoordinate));
			}

			public void focusGained(FocusEvent e) {
				targetField.setBackground(Color.WHITE);
			}
		});

		coords.add(start);
		coords.add(target);
		return coords;
	}

	private float checkCoords(String str, float limit) {
		float coordinate;
		try {
			coordinate = Float.parseFloat(str);
		} catch (NumberFormatException error) {
			return maxcoordinate;
		}
		if (Float.compare(Math.abs(coordinate), limit) > 0) {
			return maxcoordinate;
		}
		return coordinate;
	}

	private JPanel initRightPanel() {
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
		mapLabel = new JLabel("Karlsruhe");
		buttons.add(mapLabel);

		JButton profileButton = new JButton("Profil");
		profileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainController.getInstance().manageProfiles();
			}
		});
		buttons.add(profileButton);
		profileLabel = new JLabel("PKW [default]");
		buttons.add(profileLabel);
		buttons.setBackground(Color.WHITE);

		right.setLayout(new BorderLayout());
		right.add(buttons, BorderLayout.NORTH);
		right.add(new MapView(null), BorderLayout.CENTER);
		return right;
	}

	private void initMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu routeKIT = new JMenu("routeKIT");

		JMenuItem history = new JMenuItem("Verlauf...");
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
		admin.add(profile);
		admin.add(map);
		admin.add(osm);

		menu.add(routeKIT);
		menu.add(export);
		menu.add(admin);

		setJMenuBar(menu);
	}

	public static void main(String[] args) {
		new MainView();
	}

	/**
	 * Updates the display of the current profile.
	 * 
	 * @param profile
	 *            The new profile.
	 */
	public void setCurrentProfile(Profile profile) {
	}

	/**
	 * Updates the display of the current map.
	 * 
	 * @param map
	 *            The new map.
	 */
	public void setCurrentMap(StreetMap map) {
	}

	@Override
	public void routeModelChanged() {
		// TODO Auto-generated method stub

	}
}
