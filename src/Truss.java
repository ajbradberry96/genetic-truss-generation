import java.util.ArrayList;
/**
 * Models a truss simply.
 * 
 * @author Andrew Bradberry 
 * @version 10/10/2016
 */
@SuppressWarnings("rawtypes")
public class Truss implements Comparable
{
    private ArrayList<Joint> joints;
    private ArrayList<Member> members;
    private int current_load;
    private Joint[] supports;
    private Joint loaded_joint;
    private double performance_ratio;
    
    /**
     * Constructor for objects of class Truss
     */
    public Truss(ArrayList<Joint> joints, ArrayList<Member> members, int starting_load)
    {
        this.joints = joints;
        this.members = members;
        current_load = starting_load;
        supports = new Joint[] {joints.get(0), joints.get(1)};
        loaded_joint = joints.get(2);
    }
    
    /**
     * analyzes the truss and returns the greatest tensile and compressive values in its members
     * @return [max_tensile, max_compressive]
     */
    public double analyze(){
        loaded_joint.setReaction(current_load);
        //find reactions at the supports
        supports[0].setReaction((int)(-current_load * (supports[1].get_x() - loaded_joint.get_x())/
                                                 (supports[1].get_x() - supports[0].get_x())));
        supports[1].setReaction((int)(-current_load * (loaded_joint.get_x() - supports[0].get_x())/
                                                supports[1].get_x()-supports[0].get_x()));
                                                
        int tensions_found = 0;
        
        // find tensions in all the members
        while(tensions_found < joints.size()){
            for(Joint joint : joints){
                if(joint.isFindable()){
                    joint.findTensions();
                    tensions_found = 0;
                }
                else if(joint.isFound()){
                    tensions_found += 1;
                }
            } 
        }
        
        double max_force = 1000000;
        for(Member member : members){
        	if(member.getTension() < 0){
        		if(Math.abs(member.getMaxCompressiveForce(current_load)) < max_force){
        			//System.out.println(member.toString() + Math.abs(member.getMaxCompressiveForce(current_load)));
        			max_force = Math.abs(member.getMaxCompressiveForce(current_load));
        		}
        	}
        	else{
        		if(Math.abs(member.getMaxTensileForce(current_load)) < max_force){
        			//System.out.println(member.toString() + Math.abs(member.getMaxTensileForce(current_load)));
        			max_force = Math.abs(member.getMaxTensileForce(current_load));
        		}
        	}
        }
        //System.out.println(max_force);
        findPerformance_ratio(max_force);
        return performance_ratio;
    }
    
    public ArrayList<Joint> getJoints(){
    	return joints;
    	
    }
    
    public ArrayList<Member> getMembers(){
    	return members;
    	
    }

	/**
	 * @return the performance_ratio
	 */
	public double getPerformance_ratio() {
		return performance_ratio;
	}

	/**
	 * calculate the performance ratio
	 */
	public void findPerformance_ratio(double max_force) {
		double weight = Joint.WEIGHT_PER_JOINT * joints.size();
		for(Member member : members){
			weight += member.getWeight();
		}
		//System.out.println(weight);
		performance_ratio = max_force / (weight);
	}
	
	public int getLoad(){
		return current_load;
	}
	
    public Truss getCopy(){
    	ArrayList<Joint> newJoints = new ArrayList<>();
    	ArrayList<Member> newMembers = new ArrayList<>();
    	
    	for(Joint joint : joints){
    		newJoints.add(new Joint(new double[] {joint.get_x(), joint.get_y()}));
    	}
    	
    	for(Member member : members){
    		double j1x = member.getJoints()[0].get_x();
    		double j2x = member.getJoints()[1].get_x();
    		Joint j1 = null;
    		Joint j2 = null;
    		for(Joint joint: newJoints){
    			if(j1x == joint.get_x()){
    				j1 = joint;
    			}
    			if(j2x == joint.get_x()){
    				j2 = joint;
    			}
    		}
    		newMembers.add(new Member(new Joint[] {j1, j2}, member.getWidth(), member.getThickness_multiplier(), member.isBox()));
    	}
    	
    	return new Truss(newJoints, newMembers, current_load);
    }
    
    @Override
    public int compareTo(Object arg0){
    	if(this.performance_ratio > ((Truss)arg0).getPerformance_ratio()) return 1;
    	if(this.performance_ratio < ((Truss)arg0).getPerformance_ratio()) return -1;
    	else return 0;
    }
    
    public String toString(){
    	String returnString = "";
    	for(Member member : members){
    		returnString += member + "\n";
    	}
		double weight = Joint.WEIGHT_PER_JOINT * joints.size();
		for(Member member : members){
			weight += member.getWeight();
		}
    	return returnString + "\n WEIGHT OF TRUSS: " + weight;
    }
    
}
