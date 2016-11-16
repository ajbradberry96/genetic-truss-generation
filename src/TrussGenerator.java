import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;
import java.util.Random;
import java.util.Collections;
/**
 * Generates random trusses and mutates existing trusses.
 * 
 * @author Andrew Bradberry
 * @version 10/10/2016
 */
//TODO Add functionality for thickness multiplier for tabs on box beams and regular beams
public class TrussGenerator
{

	//The x-coordinate of the loaded joint in this simulation. in in.
    private final static double LOADED_X = 15;
    //The width between supports in this simulation. in in.
    private final static double SUPPORT_WIDTH = 28;
    //The maximum width and height that joints can be inside of. in in.
    private final static double MAX_WIDTH = 50;
    private final static double MAX_HEIGHT = 50;
    //Random number generator
    private Random r;
    
    /**
     * Constructor for objects of class TrussGenerator
     */
    public TrussGenerator(Random r)
    {
    	this.r = r;
    }
    
    /**
     * Generates a random, statically-determined truss
     * @return A random truss
     */
    public Truss generateRandomTruss(){
    	while(true){
	    	//ArrayLists for construction of Truss
	        ArrayList<Member> members = new ArrayList<>();
	        ArrayList<Joint> joints = new ArrayList<>();
	        //int mutability = r.nextInt(20);
	        // supports must be first two Joints, loaded must be third
	        Joint support_1 = new Joint(new double[] {0,0});
	        Joint support_2 = new Joint(new double[] {SUPPORT_WIDTH,0});
	        Joint loaded = new Joint(new double[] {LOADED_X, 2 * (r.nextDouble()) * MAX_HEIGHT});
	        
	        //System.out.println("Adding " + support_1);
	        joints.add(support_1);
	        //System.out.println("Adding " + support_2);
	        joints.add(support_2);
	        //System.out.println("Adding " + loaded);
	        joints.add(loaded);
	        
	        int total_joints = 3;
	        
	        //Add joints randomly. 
	        for(int i = 0; i < 2; i++){
	        	//Adds joints at points normally distributed from (SUPPORT_WIDTH / 2, 0)
	        	
	            joints.add(new Joint(new double[] {SUPPORT_WIDTH / 2 + r.nextGaussian() * MAX_WIDTH / 3,
	                                                r.nextGaussian() * loaded.get_y() / 2.0}));
	            //System.out.println("Adding " + joints.get(total_joints));
	            total_joints += 1;
	            }
	        
	        int total_members = 0;
	        int attempts = 0;
	        //System.out.println("Adding members");
	        while(total_members != 2 * total_joints - 3 && attempts < total_joints * total_joints + 1){
	        	Joint j1 = joints.get(r.nextInt(joints.size()));
	        	@SuppressWarnings("unchecked")
				ArrayList<Joint> shuffled =  (ArrayList<Joint>) joints.clone();
	        	Collections.copy(shuffled, joints);
	        	Collections.shuffle(shuffled);
	
	        	for(Joint joint : shuffled){
	        		//System.out.println("Checking " + j1 + " with " + joint);
	        		if(joint != j1){
	        			if(legalMember(members, j1, joint, loaded)){
	        				//System.out.println("Adding member");
	        				members.add(new Member(new Joint[] {j1, joint}, 1, 1 + r.nextInt(3), true));
	        				total_members += 1;
	        				attempts = 0;
	        				break;
	        			}
	        		}
	        		attempts += 1;
	        	}
	        }
	        if(total_members == 2 * total_joints - 3 && legal_truss(joints)){
	        	return new Truss(joints, members, -1000);
	        }
    	}
    }
    
    public Truss mutateTruss(Truss parent){
    	Truss child = parent.getCopy();
    	
    	Iterator<Member> memIt = child.getMembers().iterator();
    	while(memIt.hasNext()){
    		Member member = memIt.next();
    		if(r.nextDouble() > .9){
    			member.setBox(false);
    		}
    		else{
    			member.setBox(true);
    		}
    		if(r.nextDouble() > .9){
    			member.setWidth(.75 + .5 * r.nextDouble());
    		}
    		if(r.nextDouble() > .9){
    			member.setThickness_multiplier(1 + r.nextInt(3));
    		}
    	}
    	
    	Iterator<Joint> iterator = child.getJoints().iterator();
    	while(iterator.hasNext()){
    		Joint joint = iterator.next();
    		if(joint.get_x() != 0 && joint.get_x() != SUPPORT_WIDTH){
    			// Potentially modify y location
    			if(r.nextDouble() > .9){
	    			double old_y = joint.get_y();
	    			joint.set_y(old_y + r.nextGaussian() * 20);
	    			boolean success = true;
	    			for(Member member: joint.getMembers()){
	    				if(!legalMemberOverlap(child.getMembers(), member, child.getJoints().get(2))){
	    					success = false;
	    					break;
	    				}
	    			}
	    			if(!success){
	    				joint.set_y(old_y);
	    			}
    			}
    		}
    		if(joint.get_x() != 0 && joint.get_x() != SUPPORT_WIDTH && joint.get_x() != LOADED_X){
    			// Potentially modify x location
    			if(r.nextDouble() > .9){
	    			double old_x = joint.get_x();
	    			joint.set_x(old_x + r.nextGaussian() * 20);
	    			boolean success = true;
	    			for(Member member: joint.getMembers()){
	    				if(!legalMemberOverlap(joint.getMembers(), member, child.getJoints().get(2))){
	    					success = false;
	    					break;
	    				}
	    			}
	    			if(!success){
	    				joint.set_x(old_x);
	    			}
    			}
    		}
    	}
    	
    	for(Member member : child.getMembers()){
    		member.findLength();
            member.findTensileForce();
            member.findCompressiveForce();
            member.findWeight();
    	}
    	return child;
    }
    
    /**
     * Returns true if a member can be placed between the two given joints legally
     * @param members List of current members
     * @param j1 First joint
     * @param j2 Second joint
     * @return True if the member can be placed, false otherwise
     */
    private boolean legalMember(ArrayList<Member> members, Joint j1, Joint j2, Joint support){
    	Joint[] joints = new Joint[] {j1, j2};

        for(Member member : members){
        	if(segmentsIntersect(member, joints)){
        		return false;
        	}
        	if(segmentAboveSupportJoint(member.getJoints(), support)){
        		return false;
        	}
        }
        return true;
    }
    
    
    /**
     * Returns true if a member can be placed between the two given joints legally
     * @param members List of current members
     * @param j1 First joint
     * @param j2 Second joint
     * @return True if the member can be placed, false otherwise
     */
    private boolean legalMemberOverlap(ArrayList<Member> members,  Member new_member, Joint support){
    	Joint[] joints = new Joint[] {new_member.getJoints()[0], new_member.getJoints()[1]};

        for(Member member : members){
        	if(segmentsIntersectOverlap(member, joints)){
        		return false;
        	}
        	if(segmentAboveSupportJoint(member.getJoints(), support)){
        		return false;
        	}
        }
        return true;
    }

    /**
     * Checks to see if two line segments intersect.
     * @param member representation of one line segment
     * @param joints together makes the second line segment
     * @return true if the line segments intersect or are the same line segment. False otherwise.
     */
    private boolean segmentsIntersect(Member member1, Joint[] joints){
    	if((member1.getJoints()[0] == joints[0] || member1.getJoints()[0] == joints[1])
    			&& (member1.getJoints()[1] == joints[0] || member1.getJoints()[1] == joints[1])){
    		//System.out.println("Fail due to a member already being there.");
    		return true;
    	}
    	double x_naught = member1.getJoints()[0].get_x();
    	double y_naught = member1.getJoints()[0].get_y();
    	double slope = (y_naught - member1.getJoints()[1].get_y()) / 
    			(x_naught - member1.getJoints()[1].get_x());
    	
    	double x_0 = joints[0].get_x();
    	double y_0 = joints[0].get_y();
    	double slope_new = (y_0 - joints[1].get_y()) / 
    			(x_0 - joints[1].get_x());
    	
    	double x_intercept = (slope * x_naught - slope_new * x_0 + y_0 - y_naught) /
    						(slope - slope_new);
    	// .001 for double rounding error and because lines can intersect at their endpoints.
		return x_intercept + .001 < Math.max(x_0, joints[1].get_x()) 
				&& x_intercept - .001 > Math.min(x_0, joints[1].get_x())
				&& x_intercept +.001 < Math.max(x_naught, member1.getJoints()[1].get_x())
				&& x_intercept - .001 > Math.min(x_naught, member1.getJoints()[1].get_x());
    	
    }
    
    /**
     * Checks to see if two line segments intersect.
     * @param member representation of one line segment
     * @param joints together makes the second line segment
     * @return true if the line segments intersect or are the same line segment. False otherwise.
     */
    private boolean segmentsIntersectOverlap(Member member1, Joint[] joints){

    	double x_naught = member1.getJoints()[0].get_x();
    	double y_naught = member1.getJoints()[0].get_y();
    	double slope = (y_naught - member1.getJoints()[1].get_y()) / 
    			(x_naught - member1.getJoints()[1].get_x());
    	
    	double x_0 = joints[0].get_x();
    	double y_0 = joints[0].get_y();
    	double slope_new = (y_0 - joints[1].get_y()) / 
    			(x_0 - joints[1].get_x());
    	
    	double x_intercept = (slope * x_naught - slope_new * x_0 + y_0 - y_naught) /
    						(slope - slope_new);
    	// .001 for double rounding error and because lines can intersect at their endpoints.
		return x_intercept + .001 < Math.max(x_0, joints[1].get_x()) 
				&& x_intercept - .001 > Math.min(x_0, joints[1].get_x())
				&& x_intercept +.001 < Math.max(x_naught, member1.getJoints()[1].get_x())
				&& x_intercept - .001 > Math.min(x_naught, member1.getJoints()[1].get_x());
    	
    }
    
    private boolean segmentAboveSupportJoint(Joint[] joints, Joint support){
    	double x_0 = joints[0].get_x();
    	double y_0 = joints[0].get_y();
    	double slope_new = (y_0 - joints[1].get_y()) / 
    			(x_0 - joints[1].get_x());
    	
    	if(slope_new * (support.get_x() - x_0) + y_0 + .001 > support.get_y() &&
    			support.get_x() > Math.min(x_0, joints[1].get_x()) &&
    			support.get_x() < Math.max(x_0, joints[1].get_x())){
    		return true;
    	}
    	return false;
    }
    
    private boolean legal_truss(ArrayList<Joint> joints){
    	int num_unfindable = 0;
    	if(joints.get(0).getMembersSize() >= 3) num_unfindable += 1;
    	if(joints.get(1).getMembersSize() >= 3) num_unfindable += 1;
    	if(joints.get(2).getMembersSize() >= 3) num_unfindable += 1;
    	
    	if(num_unfindable < 2){
    		return true;
    	}
    	return false;
    }
}
