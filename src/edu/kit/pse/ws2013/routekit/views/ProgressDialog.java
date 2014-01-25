package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.util.AbstractMap;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import edu.kit.pse.ws2013.routekit.models.ProgressListener;
import edu.kit.pse.ws2013.routekit.models.ProgressReporter;

/**
 * A modal dialog that displays a progress bar and hides itself when the task is
 * done.
 * <p>
 * Intended usage:
 * <ol>
 * <li>Initialize the {@link ProgressDialog}</li>
 * <li>Initialize a {@link ProgressReporter}</li>
 * <li>Register this dialog at the reporter via
 * {@link ProgressReporter#addProgressListener(ProgressListener)}
 * <li>Asynchronously start some task, passing it the reporter</li>
 * <li>Make this dialog visible – this blocks until the task finishes</li>
 * <li>Do what needs to be done after the task has completed</li>
 * </ol>
 * 
 * @author Lucas Werkmeister
 */
public class ProgressDialog extends JDialog implements ProgressListener {
	private static final long serialVersionUID = 1L;
	private final JProgressBar bar;
	private final JTextArea text;
	private final LinkedList<Entry<String, Long>> tasks = new LinkedList<>();
	private boolean closed = false;

	public ProgressDialog(Window owner) {
		super(owner);
		setModal(true);
		JPanel p = new JPanel(new BorderLayout());
		bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(500, 25));
		p.add(bar, BorderLayout.CENTER);
		text = new JTextArea(10, 50);
		text.setEditable(false);
		p.add(text, BorderLayout.SOUTH);
		setContentPane(p);
		pack();
		setLocationRelativeTo(owner);
		new Thread() {
			@Override
			public void run() {
				do {
					repaint();
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// don’t care
					}
				} while (!closed);
			};
		}.start();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		String indent = "";
		long currentTime = System.currentTimeMillis();
		text.setText(null);
		try {
			for (Entry<String, Long> task : tasks) {
				text.append(indent);
				indent += " ";
				text.append(task.getKey());
				int elapsed = (int) ((currentTime - task.getValue()) / 1000);
				if (elapsed > 0) {
					text.append(" (");
					text.append(Integer.toString(elapsed));
					text.append("s)");
				}
				text.append("\n");
			}
		} catch (ConcurrentModificationException e) {
			// don’t care
			repaint();
		}
		super.paint(g);
	}

	@Override
	public void startRoot(String name) {
		bar.setString(name);
	}

	@Override
	public void beginTask(String name) {
		bar.setString(name);
		tasks.addLast(new AbstractMap.SimpleEntry<>(name, System
				.currentTimeMillis()));
		repaint();
	}

	@Override
	public void progress(float progress, String name) {
		bar.setValue((int) (progress * 100));
		bar.setString(name);
	}

	@Override
	public void endTask(String name) {
		bar.setString(name);
		tasks.removeLast();
		repaint();
	}

	@Override
	public void finishRoot(String name) {
		bar.setString(name);
		setVisible(false);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// must be called on the event thread
				dispose();
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		closed = !b;
		super.setVisible(b);
	}
}
