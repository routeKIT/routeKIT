package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

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
	private final JProgressBar bar;

	public ProgressDialog(Window owner) {
		super(owner);
		setModal(true);
		JPanel p = new JPanel(new BorderLayout());
		bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(500, 25));
		p.add(bar);
		setContentPane(p);
		pack();
		setLocationRelativeTo(owner);
	}

	@Override
	public void startRoot(String name) {
		bar.setString(name);
	}

	@Override
	public void beginTask(String name) {
		bar.setString(name);
	}

	@Override
	public void progress(float progress, String name) {
		bar.setValue((int) (progress * 100));
		bar.setString(name);
	}

	@Override
	public void endTask(String name) {
		bar.setString(name);
	}

	@Override
	public void finishRoot(String name) {
		bar.setString(name);
		setVisible(false);
		dispose();
	}
}
