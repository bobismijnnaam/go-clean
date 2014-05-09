package nl.plusminos.alcleantraz.ginger.panels;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class Main {
	public final JPanel panel;
	
	private JPanel menuPanel;
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> persons = new JList<String>(listModel);
	private JScrollPane scrollPane = new JScrollPane(persons);
	
	private JButton start = new JButton("Find initial schedule");
	private JButton remove = new JButton("Remove person");
	private JButton clear = new JButton("Clear list");
	private JButton add = new JButton("Add to list");
	private JButton about = new JButton("About this program");
	private JTextField newName = new JTextField();
	
	public Main() {
		// Initialize panels & sub panels
		panel = new JPanel(new MigLayout("wrap 2, fill", // debug
				"[60%][40%]",
				"[20%, top][20%][20%][20%]"));
		menuPanel = new JPanel(new MigLayout("wrap 1",
				"[100%]",
				""));
		
		// Setup menu panel
		menuPanel.add(start, "growx");
		menuPanel.add(remove, "growx");
		menuPanel.add(clear, "growx");
		menuPanel.add(about, "growx");
		
		// Setup main panel
		panel.add(menuPanel, "span 1 3, grow");
		panel.add(scrollPane, "grow, span 1 3");
		panel.add(add, "align r");
		panel.add(newName, "growx");
		
		// Populate people list
		listModel.addElement("Bob");
		listModel.addElement("Ramon");
		listModel.addElement("Penny");
		listModel.addElement("Binky");
		listModel.addElement("Maarten");
		listModel.addElement("Granny");
		listModel.addElement("Sam");
		listModel.addElement("Junior");
		listModel.addElement("Lizzy");
		listModel.addElement("Kutguus");
		listModel.addElement("Emiel");
	}
}
