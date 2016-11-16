import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.geom.*;


public class TrussDrawer extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Truss truss;
	
	public Dimension getPreferredSize(){
		return new Dimension(600,700);
	}
	public void paint(Graphics g){	
		Graphics2D g2 =(Graphics2D) g;
		setSize(700,700);
		
		g2.setColor(Color.white);
		g2.fillRect(1, 1, 699, 699);
		
		
		if(truss != null){
			g2.setColor(Color.blue);
			for(Member member : truss.getMembers()){
				g2.draw(new Line2D.Double((member.getJoints()[0].get_x() + 100) * 3, (100 - member.getJoints()[0].get_y()) * 3, 
						(member.getJoints()[1].get_x() + 100) * 3, (100 - member.getJoints()[1].get_y()) * 3));
			}
			g2.setColor(Color.red);
			int i = 0;
			for(Joint joint : truss.getJoints()){
				if(i < 3){
					g2.fill(new Ellipse2D.Double((joint.get_x() + 100) * 3 - 2.5, (100 - joint.get_y()) * 3 - 2.5, 5, 5));
					i++;
				}
				else{
					g2.setColor(Color.MAGENTA);
					g2.fill(new Ellipse2D.Double((joint.get_x() + 100) * 3 - 2.5, (100 - joint.get_y()) * 3 - 2.5, 5, 5));
				}
			}
		}
	}
	
	public TrussDrawer(){
		
	}
	
	public void setTruss(Truss truss){
		this.truss = truss;
		repaint();
	}
	
	public Truss getTruss(){
		return truss;
	}
	
}
