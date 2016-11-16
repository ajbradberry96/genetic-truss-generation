
/**
 * A simple model of a two force member
 * 
 * @author Andrew Bradberry
 * @version 10/10/2016
 */
public class Member
{
    // Thickness of the member. In in.
    private static final double THICKNESS = 0.125;
    // Density of the member. In lb/in^3
    private static final double DENSITY = .0376;
    // Modulus of elasticity in psi
    private static final double MOD_E = 560000;
    // The ultimate stress of the material. In psi
    private static final double STRESS_ULT = 3000;
	//Tension in the joint. If joint is in compression, this value will be negative. In lb
    private double tension;
    //The two joints this member spans
    private Joint[] joints;
    //True if the tension in this member has been found
    private boolean tension_found;
    //True if the length of this member has been found
    private boolean length_found;
    // Length of the member. In in.
    private double length;
    // Type of beam. false if normal, true if box
    private boolean box;
    // The greatest tensile force this member can withstand
    private double ultimate_tensile_force;
    // the greatest compressive force this member can withstand
    private double ultimate_compressive_force;
    // True if this member has failed
    private boolean failed;
    // width of the member. in in
    private double width;
    // weight of the member. In lbf
    private double weight;
    // How many sheets thick a regular beam is; how many sheets thick the tabs of box beams are.
    private int thickness_multiplier;

    
    /**
     * Constructor for objects of class Member
     * @param joints The two joints this member spans
     */
    public Member(Joint[] joints, double width, int thickness_multiplier, boolean box)
    {
        tension = 0;
        tension_found = false;
        this.joints = joints;
        setFailed(false);
        setBox(box);
        setWidth(width);
        setThickness_multiplier(thickness_multiplier);
        //Add this member to the list of members in each joint
        for(Joint joint : joints){
            joint.addMember(this);
        }
        findLength();
        findTensileForce();
        findCompressiveForce();
        findWeight();
    }
    
    public Member(Joint[] joints){
    	tension = 0;
        tension_found = false;
        this.joints = joints;
        setFailed(false);
        setBox(true);
        setWidth(1);
        setThickness_multiplier(1);
        //Add this member to the list of members in each joint
        for(Joint joint : joints){
            joint.addMember(this);
        }
        findLength();
        findTensileForce();
        findCompressiveForce();
        findWeight();
    }

    /**
     * Returns true if the tension of this member has been found
     * @return tension_found
     */
    public boolean isFound(){
        return tension_found;
    }
    
    /**
     * Set the tension in this member and note that the tension is set in tension_found
     * @param tension The new tension in the member
     */
    public void setTension(double tension){
        this.tension = tension;
        tension_found = true;
        //Debug
        //System.out.println("Setting member between (" + joints[0].get_x() + ", " +joints[0].get_y() +
        //") and (" + joints[1].get_x() + ", " + joints[1].get_y() + ") to " + tension);
    }
    
    /**
     * The tension currently set for this member.
     * @return The current tension
     */
    public double getTension(){
        return tension;
    }
    
    /**
     * Get the other joint that this member is connected to
     * @param joint the Joint that this member is known to have
     * @return The other Joint
     */
    public Joint getOtherJoint(Joint joint){
        if(joints[1] == joint){
            return joints[0];
        }
        return joints[1];
    }
    
    public Joint[] getJoints(){
    	
    	return  joints;
    }

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * calculate the length of the member
	 */
	public void findLength() {
		double delta_x = Math.abs(joints[0].get_x()-joints[1].get_x());
		double delta_y = Math.abs(joints[0].get_y()-joints[1].get_y());
		this.length = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
		length_found = true;
	}

	/**
	 * @return the length_found
	 */
	public boolean isLength_found() {
		return length_found;
	}

	/**
	 * @param length_found the length_found to set
	 */
	public void setLength_found(boolean length_found) {
		this.length_found = length_found;
	}
	
	public void findCompressiveForce(){
		if(box){
			// FIX TO CONSIDER TABS AT THE END OF MEMBERS
			double tabs = Math.max(-Math.PI * Math.PI * MOD_E * width * Math.pow(THICKNESS * thickness_multiplier, 3)
					/ (12 * 4), -ultimate_tensile_force);
			setUltimate_compressive_force(Math.max(-Math.PI * Math.PI * MOD_E * (Math.pow(width, 4) - Math.pow(width - 2 * THICKNESS, 4))
					/ (12 * length * length), tabs));
		}
		else{
			setUltimate_compressive_force(Math.max(-Math.PI * Math.PI * MOD_E * width * Math.pow(THICKNESS * thickness_multiplier, 3) 
					/ (12 * length * length), -ultimate_tensile_force ));
		}
	}
	
	public void findTensileForce(){
		if(!box){
			setUltimate_tensile_force(STRESS_ULT * width * THICKNESS * thickness_multiplier);
		}
		else if(thickness_multiplier < 2){
			setUltimate_tensile_force(STRESS_ULT * width* THICKNESS * 2);
		}
		else{
			setUltimate_tensile_force(STRESS_ULT * (width*width - (width-2 * THICKNESS) * (width - 2 * THICKNESS)));
		}
	}
	
	public void findWeight(){
		if(box){
			setWeight((width * width - (width - 2 * THICKNESS) * (width - 2 * THICKNESS)) * (length-2) * DENSITY 
					+ 2 * width * THICKNESS * thickness_multiplier * DENSITY);
		}
		else{
			setWeight(width * THICKNESS * thickness_multiplier * length * DENSITY);
		}
	}

	/**
	 * @return the ultimate_tensile_force
	 */
	public double getUltimate_tensile_force() {
		return ultimate_tensile_force;
	}

	/**
	 * @param ultimate_tensile_force the ultimate_tensile_force to set
	 */
	public void setUltimate_tensile_force(double ultimate_tensile_force) {
		this.ultimate_tensile_force = ultimate_tensile_force;
	}

	/**
	 * @return the ultimate_compressive_force
	 */
	public double getUltimate_compressive_force() {
		return ultimate_compressive_force;
	}

	/**
	 * @param ultimate_compressive_force the ultimate_compressive_force to set
	 */
	public void setUltimate_compressive_force(double ultimate_compressive_force) {
		this.ultimate_compressive_force = ultimate_compressive_force;
	}

	/**
	 * @return the failed
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * @param failed the failed to set
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void setBox(boolean box){
		this.box = box;
	}
	
	public boolean isBox(){
		return box;
	}
	public double getMaxCompressiveForce(double current_load){
		if(length < 2){
			return 0;
		}

		return (ultimate_compressive_force / tension) * current_load;
	}
	
	public double getMaxTensileForce(double current_load){
		if(length < 2){
			return 0;
		}

		return (ultimate_tensile_force / tension) * -current_load;
	}
	
	public void setWidth(double width){
		this.width = width;
	}
	
	public double getWidth(){
		return width;
	}
	public void setThickness_multiplier(int thickness_multiplier){
		this.thickness_multiplier = thickness_multiplier;
	}
	public int getThickness_multiplier(){
		return thickness_multiplier;
	}
	
	public String toString(){
		return "\nMember with joints " + joints[0] + " " + joints[1] +
				"\nLength: " + length + 
				"\nWidth: " + width +
				"\nWeight: " + weight + 
				"\nBox: " + box +
				"\nThickness: " + thickness_multiplier + 
				"\nTension: " + tension + 
				"\nUltimate Tensile Force: " + ultimate_tensile_force + 
				"\nUltimate Compressive Force: " + ultimate_compressive_force + 
				"\nMaximum tension: " + getMaxTensileForce(-1000) +
				"\nMaximum compression: " + getMaxCompressiveForce(-1000);
	}
}
