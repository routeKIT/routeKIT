package views;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import mapDisplay.TileCache;
import mapDisplay.TileSource;
/**
 * Zeigt einen Kartenausschnitt auf dem Bildschirm an.
 * 
 * Als Kartenprojektion wird die Mercator-Projektion verwendet.
 */
public class MapView extends JPanel
		implements
			MouseListener,
			MouseMotionListener,
			MouseWheelListener {
	float x;
	float y;
	int zoom = 16;
	/**
	 * Konstruktor: Erzeugt eine neue {@link MapView}. Die angegebene
	 * {@link TileSource} wird zum Rendern verwendet.
	 * 
	 * Da die Kacheln bei jedem {@link MapView#paint} synchron angefragt werden,
	 * sollte {@code source} ein {@link TileCache} sein.
	 * 
	 * @param source
	 *            Ein Objekt, das die Kartenkacheln liefert, die dann angezeigt
	 *            werden.
	 */
	public MapView(TileSource source) {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	/**
	 * Zeichnet den aktuell sichtbaren Kartenausschnitt. Alle sichtbaren Kacheln
	 * werden von {@code source} synchron angefordert.
	 * 
	 * @param graphics
	 *            Die Java {@code Graphics}, auf welche die Karte gezeichnet
	 *            wird.
	 */
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.translate((int) (x * 256), (int) (y * 256));
		g.translate(getWidth() / 2, getHeight() / 2);
		((Graphics2D) g).rotate(45);
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, 0, 20));
		g.drawString("Ich bin ein Karte", -100, 0);
	}

	int dx = 0;
	int dy = 0;
	float orgX;
	float orgY;

	private void applyDrag(MouseEvent e) {
		x = orgX + (e.getX() - dx) / 256f;
		y = orgY + (e.getY() - dy) / 256f;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		dx = e.getX();
		dy = e.getY();
		orgX = x;
		orgY = y;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		applyDrag(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		applyDrag(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int xp = e.getX();
		int yp = e.getY();
		int klick = e.getWheelRotation();
		if (klick == 0) {
			return;
		}
		while (klick > 0) {
			klick--;
			// zoomIn;
		}
		while (klick < 0) {
			klick++;
			// zoomOut;
		}
		repaint();
	}

}
