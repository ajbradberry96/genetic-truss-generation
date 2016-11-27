import java.util.ArrayList;
import java.lang.Math;
/**
 * Simple model of a joint
 * 
 * @author Andrew Bradberry
 * @version 10/10/2016
 */
public class Joint
{
    // The weight cost per joint. Assumes 6 in of 1/4 in allthread.
	// Assumes 4 1/4" nuts. In lbf ((.12 lb / ft) / 2 + 4 * .0088) .06 +.0352  
    public static final double WEIGHT_PER_JOINT = .0952 / 2;
	// A reaction force or other external force. In lbf. Always vertical
    private int external_force;
    // A list of members that contact this joint
    private ArrayList<Member> members = new ArrayList<>();
    // (x,y) coordinates of the joint. In in
    private double[] coordinates;


    
    /**
     * Constructor for objects of class Joint
     * @param The coordinates of the joint
     */
    public Joint(double[] coordinates){
        this.coordinates = coordinates;
    }

    /**
     * Find the tensions of the members at the joint that have not yet been found.
     */
    public void findTensions(){
    	// Count members without found tensions
        int unfound_members = 0;
        for(Member member : members){
            if(! member.isFound()){
                unfound_members += 1;
            }
        }
        
        if(unfound_members == 1){
            findOneTension();
        }
        else{
            findTwoTensions();
        }
    }
    
    /**
     * Finds the tension in the last remaining member
     */
    private void findOneTension(){
    	// Find the member without a tension
        Member finding = null;
        for(Member member : members){
            if(!member.isFound()){
                finding = member;
                break;
            }    
        }
        
        Joint other_joint = finding.getOtherJoint(this);
        
        // If the member you're looking for has an x-component
        if(!(get_x() - other_joint.get_x() < 0.001  && get_x() - other_joint.get_x() > -0.001)){
            double sum_x = 0;
            //Find the sum of forces in the x direction
            for(Member member : members){
                if(member.isFound()){
                    Joint ojoint = member.getOtherJoint(this);
                    sum_x += member.getTension() * findScaler(ojoint, 'x');
                }
            }
            // Set the tension of the last remaining member
            finding.setTension((-sum_x / findScaler(other_joint, 'x')));
        }
        else{// no x component
            double y_comp = external_force;
            //Find the sum of forces in the y direction
            for(Member member : members){
                if(member.isFound()){
                    Joint ojoint = member.getOtherJoint(this);
                    y_comp += member.getTension() * findScaler(ojoint, 'y');
                }
            }
            // Set the tension of the last remaining member
            finding.setTension((-y_comp / findScaler(other_joint, 'y')));
        }
    }
    
    /**
     * Find the tensions of the two members that have yet to be found
     */
    private void findTwoTensions(){
        double sum_x = 0;
        double sum_y = external_force;
        // Sum the forces in x and y directions
        for(Member member : members){
                if(member.isFound()){
                    Joint ojoint = member.getOtherJoint(this);
                    sum_x += member.getTension() * findScaler(ojoint, 'x');
                    sum_y += member.getTension() * findScaler(ojoint, 'y');
                }
        }
        // Scalers of each tension given by direction.
        double xSc1 = 0;
        double ySc1 = 0;
        double xSc2 = 0;
        double ySc2 = 0;
        
        Member mem1 = null;
        Member mem2 = null;
        // Find the two members without found tensions
        for(Member member : members){
            if(!member.isFound() && mem1 == null){
                mem1 = member;
            }
            if(!member.isFound() && member != mem1){
                mem2 = member;
                break;
            }
        }
        
        Joint oj1 = mem1.getOtherJoint(this);
        Joint oj2 = mem2.getOtherJoint(this);
        // Find the scalers
        xSc1 = findScaler(oj1, 'x');
        ySc1 = findScaler(oj1, 'y');
        xSc2 = findScaler(oj2, 'x');
        ySc2 = findScaler(oj2, 'y');
        
        
        // If there is given information in the y direction, set the tensions in each member
        if(ySc2 != 0){
            mem1.setTension((sum_y * xSc2 - sum_x * ySc2) / (xSc1*ySc2 - ySc1 * xSc2));
            mem2.setTension((-mem1.getTension() * ySc1 - sum_y) / ySc2);
        }
        else{ //use x information and set the tension in each member.
            mem1.setTension((sum_x * ySc2 - sum_y * xSc2) / (ySc1*xSc2 - xSc1 * ySc2));
            mem2.setTension((-mem1.getTension() * xSc1 - sum_x) / xSc2);
        }
    }
    
    /**
     * Adds a member to the ArrayList<> members
     * @param member Member to be added
     */
    public void addMember(Member member){
        members.add(member);
    }

    /**
     * Returns the x coordinate of the joint
     * @return x coordinate of joint
     */
    public double get_x(){
        return coordinates[0];
    }
    
    /**
     * Returns the y coordinate of the joint
     * @return y coordinate of joint
     */
    public double get_y(){
        return coordinates[1];
    }
    
    /**
     * Returns true if the tensions of the unfound members can be found, 
     * returns false if there are no unfound members or if there is not enough 
     * information to solve for the tensions in all of the remaining members.
     * @return true if the unfound members can be found
     */
    public boolean isFindable(){
    	//count the members whose tension has yet to be found
        int unfound_members = 0;
        for(Member member : members){
            if(! member.isFound()){
                unfound_members += 1;
            }
        }
        
        // If there are too many members without found tensions, or are none that need to be, return false
        if(unfound_members > 2 
        	|| (members.size() == 2 && external_force == 0 && unfound_members == 2)
            || unfound_members == 0){
            return false;
        }
        // Else return true
        return true;
    }
    
    /**
     * @return true if all members have been found
     */
    public boolean isFound(){
    	//count the members whose tension has yet to be found
        int unfound_members = 0;
        for(Member member : members){
            if(! member.isFound()){
                unfound_members += 1;
            }
        }
        
        // If there are too many members without found tensions, or are none that need to be, return false
        if(unfound_members == 0){
            return true;
        }
        // Else return true
        return false;
    }
    
    /**
     * Set the y-reaction force at the joint in lbf. 
     * @param reaction The new reaction force
     */
    public void setReaction(int reaction){
        this.external_force = reaction;
    }
    
    /**
     * @return The coordinates of the joint
     */
    public String toString(){
    	
    	return "Joint at (" + get_x() + "," + get_y() + ")"; 
    }
    
    /**
     * Finds the scaler for a tension in a particular direction
     * @param other_joint The other joint the member is connected to
     * @param dir Either in the x or y direction
     * @return the scaler multiplier in that direction
     */
    private double findScaler(Joint other_joint, char dir){
    	if(dir == 'x'){
    		return (other_joint.get_x()-get_x()) 
    				/ Math.sqrt(Math.pow(other_joint.get_x() - get_x(),2) + Math.pow(other_joint.get_y() - get_y(),2));
    	}
    	else{
    		return (other_joint.get_y()-get_y()) 
    				/ Math.sqrt(Math.pow(other_joint.get_x() - get_x(),2) + Math.pow(other_joint.get_y() - get_y(),2));
    	}
    }
    
    public void set_x(double x){
    	coordinates[0] = x;
    }
    
    public void set_y(double y){
    	coordinates[1] = y;
    }
    
    /**
     * @return The numebr of members connected to this joint. 
     */
    public int getMembersSize(){
    	return members.size();
    }
    
    public ArrayList<Member> getMembers(){
    	return members;
    }
    

}
