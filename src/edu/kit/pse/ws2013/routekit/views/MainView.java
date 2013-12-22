package edu.kit.pse.ws2013.routekit.views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import edu.kit.pse.ws2013.routekit.map.StreetMap;
import edu.kit.pse.ws2013.routekit.profiles.Profile;
/**
 * Displays the main window on the screen.
 */
public class MainView extends JFrame{
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
		hist.add(new JButton("Verlauf"));
		controls.add(hist, BorderLayout.SOUTH);

		JPanel swap = new JPanel();
		JButton swapKnopf = new JButton(new ImageIcon("Knopf.png"));
		swapKnopf.setMargin(new Insets(0, 0, 0, 0));
		swap.add(swapKnopf);
		controls.add(swap, BorderLayout.WEST);
		controls.add(initCoordEntry(), BorderLayout.CENTER);

		left.add(controls, BorderLayout.NORTH);
		JList<String> routeDescription = new JList<String>(new String[]{
				"sdfsdfsd", "dsdfsd", "sdf"});
		left.add(routeDescription, BorderLayout.CENTER);
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
		start.add(new JTextField(15));
		target.add(targetLabel);
		target.add(new JTextField(15));

		coords.add(start);
		coords.add(target);
		return coords;
	}

	private JPanel initRightPanel() {
		JPanel right = new JPanel();
		JPanel buttons = new JPanel();
		JButton mapButton = new JButton("Karte");
		buttons.add(mapButton);
		mapLabel = new JLabel("Karlsruhe");
		buttons.add(mapLabel);
		JButton profileButton = new JButton("Profil");
		profileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ProfileManagerView();
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
		JMenuItem about = new JMenuItem("�ber...");
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
				MainController.getInstance().exportHTML(fileChooser.getSelectedFile());
			}
		});
		JMenuItem gpx = new JMenuItem("GPX...");
		gpx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.showDialog(MainView.this, "Exportieren");
				MainController.getInstance().exportGPX(fileChooser.getSelectedFile());
			}
		});
		export.add(html);
		export.add(gpx);

		JMenu admin = new JMenu("Verwaltung");
		JMenuItem profile = new JMenuItem("Profil...");
		profile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ProfileManagerView();
			}
		});
		JMenuItem map = new JMenuItem("Karte...");
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
}
