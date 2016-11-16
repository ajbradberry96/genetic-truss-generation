import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Canvas extends JFrame{

	HashMap<String, JButton> buttons;
	TrussDrawer best_truss;
	TrussDrawer median_truss;
	TrussDrawer worst_truss;
	Controller controller;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Canvas(Controller controller){
		super("Truss Fun");
		buttons = new HashMap<>();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		makeFrame();
		this.controller = controller;
		setVisible(true);
	}
	private void quit(){
		System.exit(0);
	}
	private void singleGen(){
		controller.singleGeneration();
		//System.out.println("single");
		controller.displayTrusses();
	}
	private void tenGen(){
		for(int i = 0; i < 10; i++) controller.singleGeneration();
		
		controller.displayTrusses();
		System.out.println("finished");
	}
	private void thousandGen(){
		for(int i = 0; i < 1000; i++) controller.singleGeneration();
		
		System.out.println("finished");
		controller.displayTrusses();
	}
	private void printBest(){
		System.out.println(best_truss.getTruss());
		System.out.println("Best: " + controller.getTrusses().get(999).getPerformance_ratio());
		System.out.println("Median: " + controller.getTrusses().get(500).getPerformance_ratio());
		System.out.println("Worst: " + controller.getTrusses().get(0).getPerformance_ratio());
		System.out.println("Generation: " + controller.getGen());
	}
	private void makeFrame(){
		Container contentPane = getContentPane();
		
		contentPane.setLayout(new BorderLayout());
		
		JMenuBar menubar = new JMenuBar();
		
		setJMenuBar(menubar);
		
		JMenu menu = new JMenu("File");
		
		JMenuItem menuItem = new JMenuItem("Quit");
		menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){quit();}
			});
		
		menu.add(menuItem);
		
		menubar.add(menu);
		
		
		
		JPanel trusses = new JPanel(new GridLayout(1, 3));
		
		best_truss = new TrussDrawer();
		trusses.add(best_truss);
		median_truss = new TrussDrawer();
		trusses.add(median_truss);
		worst_truss = new TrussDrawer();
		trusses.add(worst_truss);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1,4));
		
		JButton button = new JButton("Single Generation");
		button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ singleGen();}
			});
		buttons.put("Single Generation", button);
		buttonPanel.add(button);
		
		button = new JButton("Ten Generations");
		button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ tenGen();}
			});
		buttons.put("Ten Generations", button);
		buttonPanel.add(button);
		
		button = new JButton("One Thousand Generations");
		button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ thousandGen();}
			});
		buttons.put("One Thousand Generations", button);
		buttonPanel.add(button);

		button = new JButton("Print best");
		button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){ printBest();}
			});
		buttons.put("Print best", button);
		buttonPanel.add(button);
		
		//JPanel infoPanel = new JPanel(new GridLayout(1,12));
		
		//contentPane.add(infoPanel, BorderLayout.WEST);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		contentPane.add(trusses, BorderLayout.EAST);
		pack();

	}
	
	public void setBestTruss(Truss truss){
		best_truss.setTruss(truss);
	}
	
	public void setMedianTruss(Truss truss){
		median_truss.setTruss(truss);
	}
	
	public void setWorstTruss(Truss truss){
		worst_truss.setTruss(truss);
	}
}
