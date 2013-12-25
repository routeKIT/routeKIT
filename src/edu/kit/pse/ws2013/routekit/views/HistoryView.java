package edu.kit.pse.ws2013.routekit.views;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import edu.kit.pse.ws2013.routekit.history.History;
/**
 * Shows the window with the History on the screen.
 */
public class HistoryView extends JFrame{
	int selectedElement = -1;
	/**
	 * The constructor creates a HistoryView for the specified history. 
	 * The history can not be subsequently changed.
	 * 
	 * @param history
	 *            The history that is displayed.
	 */
	public HistoryView(History history) {
		super("Verlauf");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
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
		String[] strings = new String[100];
		Arrays.fill(strings, "hallo");
		strings[0] = "sdfsdfsd";
		final JList<String> historyvar = new JList<String>(strings);
		historyvar.setBackground(Color.lightGray);
		historyvar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    final JScrollPane jScrollPane = new JScrollPane(historyvar);
		historyvar.addListSelectionListener(new ListSelectionListener() 
		  {
			  @Override
		         public void valueChanged (ListSelectionEvent e){
				  	selectedElement = historyvar.getSelectedIndex();
				}
			  });
		return jScrollPane;
	}

	private JPanel initSouthPane(History history) {
		JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton ok = new JButton("OK");
		south.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedElement == -1)
					return;
				else {
					//String historyentry = history.getEntries().get(selectedElement).;
					//setStartAndDestinationPoint(start, destination);
					dispose();
				}
			}
		});
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
	
	public static void main(String[] args) {
		new HistoryView(null);
	}
	

}
