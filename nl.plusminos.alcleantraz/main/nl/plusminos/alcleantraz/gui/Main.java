package nl.plusminos.alcleantraz.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class Main implements ActionListener, KeyListener{
	public final JPanel panel;
	
	private JPanel menuPanel;
	private JPanel counterPanel;
	
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> persons = new JList<String>(listModel);
	private JScrollPane scrollPane = new JScrollPane(persons);
	
	private JButton start = new JButton("Find initial schedule");
	private JButton remove = new JButton("Remove person");
	private JButton clear = new JButton("Clear list");
	private JButton about = new JButton("About this program");
	
	private JTextField newName = new JTextField();
	private JButton add = new JButton("Add to list");
	
	private JLabel threeWeeksCaption = new JLabel("Amount of weeks", SwingConstants.HORIZONTAL);
	private JTextField threeWeeksAmount = new JTextField("12");
	private JButton increase = new JButton("+");
	private JButton decrease = new JButton("-");
	
	public Main() {
		// Initialize panels & sub panels
		panel = new JPanel(new MigLayout("wrap 2, fill", // , debug 
				"[60%][40%]",
				"[20%, top][20%][20%][20%]"));
		menuPanel = new JPanel(new MigLayout("wrap 1",
				"[100%]",
				"[][][][]20[]"));
		counterPanel = new JPanel(new MigLayout("fill", // , debug
				"[10][80][10]",
				"[50%][50%]"));
		
		// Setup counter panel
		threeWeeksAmount.setEditable(false);
		threeWeeksAmount.setHorizontalAlignment(JTextField.CENTER);
		counterPanel.add(threeWeeksCaption, "wrap, span 3, growx");
		counterPanel.add(decrease, "growx");
		counterPanel.add(threeWeeksAmount, "growx");
		counterPanel.add(increase, "growx");
		
		// Setup menu panel
		menuPanel.add(start, "growx");
		menuPanel.add(remove, "growx");
		menuPanel.add(clear, "growx");
		menuPanel.add(counterPanel, "growx");
		
		// Setup main panel
		panel.add(menuPanel, "span 1 3, grow");
		panel.add(scrollPane, "grow, span 1 3");
		panel.add(add, "growx");
		panel.add(newName, "growx");
		panel.add(about, "growx, span 2");
		
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
		
		// Register at the buttons for events
		start.addActionListener(this);
		remove.addActionListener(this);
		clear.addActionListener(this);
		add.addActionListener(this);
		about.addActionListener(this);
		increase.addActionListener(this);
		decrease.addActionListener(this);
		
		newName.addKeyListener(this);
		persons.addKeyListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start) {
			// Retrieve list items
			String[] names = Arrays.copyOf(listModel.toArray(), listModel.toArray().length, String[].class);
			int threeWeeks = Integer.parseInt(threeWeeksAmount.getText()) / 3;
			
			// Setup finding schedule screen
			AlcleantrazGui.inst.screen.setVisible(false);
			AlcleantrazGui.inst.screen.remove(panel);
			AlcleantrazGui.inst.screen.add(AlcleantrazGui.inst.schedulePanel.panel);
			
			AlcleantrazGui.inst.schedulePanel.start(names, threeWeeks);
			AlcleantrazGui.inst.screen.setVisible(true);
		} else if (e.getSource() == remove) {
			int[] selection = persons.getSelectedIndices();
			if (selection.length != 0) {
				for (int i = selection.length - 1; i >= 0; i--) {
					listModel.remove(selection[i]);
				}
			}
		} else if (e.getSource() == clear) {
			listModel.clear();
		} else if (e.getSource() == add) {
			String toAdd = newName.getText();
			newName.setText("");
			if (!listModel.contains(toAdd) && !toAdd.equals("")) {
				listModel.addElement(toAdd);
			}
		} else if (e.getSource() == about) {
			JOptionPane.showMessageDialog(AlcleantrazGui.inst.screen,
					"Alcleantraz by Bob Rubbens - http://www.plusminos.nl - @broervanlisa\nLicensed under WTFPL 2.0.\nMay be harmful if swallowed.",
					"About",
					JOptionPane.INFORMATION_MESSAGE);			
		} else if (e.getSource() == decrease) {
			int currentThreeWeeks = Integer.parseInt(threeWeeksAmount.getText());
			if (currentThreeWeeks > 3) {
				currentThreeWeeks -= 3;
				threeWeeksAmount.setText(""+currentThreeWeeks);
			}
		} else if (e.getSource() == increase) {
			int currentThreeWeeks = Integer.parseInt(threeWeeksAmount.getText());
			currentThreeWeeks += 3;
			threeWeeksAmount.setText(""+currentThreeWeeks);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == newName && e.getKeyCode() == KeyEvent.VK_ENTER) {
			String toAdd = newName.getText();
			newName.setText("");
			if (!listModel.contains(toAdd) && !toAdd.equals("")) {
				listModel.addElement(toAdd);
			}
		} else if (e.getSource() == persons && e.getKeyCode() == KeyEvent.VK_DELETE) {
			int[] selection = persons.getSelectedIndices();
			if (selection.length != 0) {
				for (int i = selection.length - 1; i >= 0; i--) {
					listModel.remove(selection[i]);
				}
			}
		}
	}
	
	
}
