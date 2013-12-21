package views;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import map.StreetMap;
import profiles.Profile;
/**
 * Zeigt das Hauptfenster auf dem Bildschirm an.
 */
public class MainView extends JFrame{
	
	public MainView() {
		super("routeKIT");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 500);
		
		initMenu();
		

		setVisible(true);
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
