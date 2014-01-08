package edu.kit.pse.ws2013.routekit.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.kit.pse.ws2013.routekit.controllers.MainController;
import edu.kit.pse.ws2013.routekit.history.History;
import edu.kit.pse.ws2013.routekit.history.HistoryEntry;
import edu.kit.pse.ws2013.routekit.util.Coordinates;

/**
 * Shows the window with the History on the screen.
 */
public class HistoryView extends JDialog {
	JList<HistoryEntry> historyvar;

	/**
	 * The constructor creates a HistoryView for the specified history. The
	 * history can not be subsequently changed.
	 * 
	 * @param history
	 *            The history that is displayed.
	 */
	public HistoryView(History history, Window parent) {
		super(parent, "Verlauf", ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(getParent());
		setResizable(false);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel south = initSouthPane(history);
		contentPane.add(initCenterPane(history), BorderLayout.CENTER);
		contentPane.add(south, BorderLayout.SOUTH);

		setContentPane(contentPane);
		setVisible(true);
	}

	private JComponent initCenterPane(History history) {
		// String[] strings = new String[100];
		// Arrays.fill(strings, "hallo");
		// strings[0] = "sdfsdfsd";
		// final JList<String> historyvar = new JList<String>(strings);
		historyvar = new JList<HistoryEntry>(history.getEntries().toArray(
				new HistoryEntry[0]));
		historyvar.setBackground(Color.lightGray);
		historyvar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		historyvar.addMouseListener(new MouseAdapter() {
			/**
			 * A listener for a double mouse click. The coordinates for the
			 * calculation of the route are passed on and the windows closed.
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (historyvar.getSelectedValue() == null)
						return;
					Coordinates start = historyvar.getSelectedValue()
							.getStart();
					Coordinates destination = historyvar.getSelectedValue()
							.getDest();
					MainController.getInstance().setStartAndDestinationPoint(
							start, destination);
					dispose();
				}
			}
		});
		final JScrollPane jScrollPane = new JScrollPane(historyvar);
		return jScrollPane;
	}

	private JPanel initSouthPane(final History history) {
		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton ok = new JButton("OK");
		south.add(ok);
		ok.addActionListener(new ActionListener() {

			/**
			 * A listener for the "OK"-button. When a row is selected, the
			 * coordinates for the calculation of the route are passed on and
			 * the windows closed. When no row is selected, the window stays
			 * open.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (historyvar.getSelectedValue() == null)
					return;
				else {
					Coordinates start = historyvar.getSelectedValue()
							.getStart();
					Coordinates destination = historyvar.getSelectedValue()
							.getDest();
					MainController.getInstance().setStartAndDestinationPoint(
							start, destination);
					dispose();
				}
			}
		});
		JButton cancel = new JButton("Abbrechen");
		cancel.addActionListener(new ActionListener() {

			/**
			 * A listener for the "Abbrechen"-button. Closes the window.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		south.add(cancel);
		return south;
	}
}
