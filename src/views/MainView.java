package views;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import map.StreetMap;
import profiles.Profile;
/**
 * Zeigt das Hauptfenster auf dem Bildschirm an.
 */
public class MainView extends JFrame{
	private JLabel mapLabel;
	private JLabel profileLabel;

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
		left.setMinimumSize(new Dimension(200, 100));

		JPanel controls = new JPanel(new BorderLayout());
		JPanel hist = new JPanel();
		hist.add(new JButton("Verlauf"));
		controls.add(hist, BorderLayout.SOUTH);

		JPanel swap = new JPanel();
		JButton swapKnopf = new JButton(new ImageIcon(MainView.class.getResource("Knopf.png")));
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
		buttons.add(new JButton("Karte"));
		mapLabel = new JLabel("Karlsruhe");
		buttons.add(mapLabel);
		buttons.add(new JButton("Profil"));
		profileLabel = new JLabel("PKW [default]");
		buttons.add(profileLabel);
		buttons.setBackground(Color.WHITE);

		JPanel map = new JPanel();
		map.add(new JButton("Ich bin eine Karte"));
		right.setLayout(new BorderLayout());
		right.add(buttons, BorderLayout.NORTH);
		right.add(map, BorderLayout.CENTER);
		return right;
	}
	private void initMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu routeKIT = new JMenu("routeKIT");

		JMenuItem history = new JMenuItem("Verlauf...");
		JMenuItem about = new JMenuItem("Über...");
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
		JMenuItem gpx = new JMenuItem("GPX...");
		export.add(html);
		export.add(gpx);

		JMenu admin = new JMenu("Verwaltung");
		JMenuItem profile = new JMenuItem("Profil...");
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
	 * Aktualisiert die Anzeige des aktuellen Profils.
	 * 
	 * @param profile
	 *            Das neue Profil.
	 */
	public void setCurrentProfile(Profile profile) {
	}
	/**
	 * Aktualisiert die Anzeige der aktuellen Karte.
	 * 
	 * @param map
	 *            Die neue Karte.
	 */
	public void setCurrentMap(StreetMap map) {
	}
}
