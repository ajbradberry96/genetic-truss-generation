import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;


/**
 * Controller for simulation
 * 
 * @author Andrew Bradberry 
 * @version 11/15/2016
 */
public class Controller
{
	private final int NUMBER_OF_TRUSSES = 1000;
	private ArrayList<Truss> trusses = new ArrayList<>();
	private Random r = new Random();
	private TrussGenerator generator = new TrussGenerator(r);
	private Canvas canvas;
	private int gen;
	
    public static void main(String[] args){
    	@SuppressWarnings("unused")
		Controller controller = new Controller();
		//TrussGenerator tg = new TrussGenerator(new Random());
		//System.out.println(tg.testSegmentAboveSupportJoint());
    }
    /**
     * Constructor for objects of class Controller
     */
    public Controller()
    {
    	canvas = new Canvas(this);
    	generateListOfTrusses();
    	gen = 1;
    	//generateTestTruss();
    }
    
 
    
    public void generateListOfTrusses(){
    	for(int i = 0; i < NUMBER_OF_TRUSSES; i++){
    		trusses.add(generator.generateRandomTruss());
    	}
    	analyzeTrusses(trusses);
    	sortTrusses();
    	displayTrusses();
    }

	public void singleGeneration(){
		ArrayList<Truss> newHalf = replaceHalf();
		analyzeTrusses(newHalf);
		trusses.addAll(newHalf);
		sortTrusses();
		//displayTrusses();
		gen++;
    }
	
    private ArrayList<Truss> replaceHalf() {
		// TO
    	ArrayList<Truss> newHalf = new ArrayList<>();
    	
    	int removed = 0;
    	
    	Iterator<Truss> it = trusses.iterator(); 
    	
    	while(removed < 500){
    		it.next();
    		if(r.nextInt(100) < 95){
    			it.remove();
    			removed += 1;
    		}
    		if(!it.hasNext()){
    			it = trusses.iterator();
    		}
    	}
    	for(Truss truss: trusses){
    		newHalf.add(generator.mutateTruss(truss));
    	}
    	//System.out.println("Trusses size after removing half: " + trusses.size());
		//System.out.println("New half size: " + newHalf.size());
		return newHalf;
	}
    
	@SuppressWarnings("unchecked")
	private void sortTrusses() {
		//System.out.println("0 before sorting: " + trusses.get(0).getPerformance_ratio() + "\n999 before sorting: " + trusses.get(999).getPerformance_ratio());
		Collections.sort(trusses);
		//System.out.println("0 after sorting: " + trusses.get(0).getPerformance_ratio() + "\n999 after sorting: " + trusses.get(999).getPerformance_ratio());
	}
	
	private void analyzeTrusses(ArrayList<Truss> toAnalyze) {

		for(Truss truss : toAnalyze){
			truss.analyze();
		}
	}
    
	public void displayTrusses() {

		//System.out.println("Best: " + trusses.get(999).getPerformance_ratio());
		//System.out.println("Median: " + trusses.get(500).getPerformance_ratio());
		//System.out.println("Worst: " + trusses.get(0).getPerformance_ratio());
		
        canvas.setBestTruss(trusses.get(999));
        canvas.setMedianTruss(trusses.get(500));
        canvas.setWorstTruss(trusses.get(0));
	}
	
	public ArrayList<Truss> getTrusses(){
		return trusses;
	}
	
	public int getGen(){
		return gen;
	}
	
	   /**
     * Generates a truss and prints the result of its analyze() method
     */
    public void generateRandomTestTruss(){
        Truss random = generator.generateRandomTruss();
        canvas.setBestTruss(random);
        canvas.setMedianTruss(random);
        canvas.setWorstTruss(random);
        System.out.println("Performance ratio: " + random.analyze());
        System.out.println(random);
    }
    
    public void generateTestTruss(){
    	/*ArrayList<Joint> testJoints = new ArrayList<>();
    	ArrayList<Member> testMembers = new ArrayList<>();
    	Joint supportA = new Joint(new double[] {0, 0} );
    	Joint supportC = new Joint(new double[] {15.966+65.768, 0} );
    	Joint loadedB = new Joint(new double[] {15.966, 0});
    	Joint d = new Joint(new double[] {15.966, -26.572});
    	
    	testJoints.add(supportA);
    	testJoints.add(supportC);
    	testJoints.add(loadedB);
    	testJoints.add(d);
    	
    	Member AB = new Member(new Joint[] {supportA, loadedB});
    	Member BC = new Member(new Joint[] {supportC, loadedB});
    	Member BD = new Member(new Joint[] {loadedB, d});
    	Member AD = new Member(new Joint[] {supportA, d});
    	Member CD = new Member(new Joint[] {supportC, d});
    	
    	testMembers.add(AB);
    	testMembers.add(BC);
    	testMembers.add(BD);
    	testMembers.add(AD);
    	testMembers.add(CD);
    	
    	Truss testTruss = new Truss(testJoints, testMembers, -1000);
    	System.out.println(testTruss.analyze());
    	System.out.println(testTruss);
    	
    	ArrayList<Joint> testJoints = new ArrayList<>();
    	ArrayList<Member> testMembers = new ArrayList<>();
    	Joint supportA = new Joint(new double[] {0, 0} );
    	Joint supportC = new Joint(new double[] {28, 0} );
    	Joint loadedB = new Joint(new double[] {15, 18.6333});
    	Joint d = new Joint(new double[] {28 - .3379, -2.162});
    	Joint e = new Joint(new double[] { .6293, -4.0545});
    	
    	testJoints.add(supportA);
    	testJoints.add(supportC);
    	testJoints.add(loadedB);
    	testJoints.add(d);
    	testJoints.add(e);
    	
    	Member AB = new Member(new Joint[] {supportA, loadedB});
    	Member BC = new Member(new Joint[] {supportC, loadedB});
    	Member BD = new Member(new Joint[] {loadedB, d});
    	Member AE = new Member(new Joint[] {supportA, e});
    	Member CD = new Member(new Joint[] {supportC, d});
    	Member DE = new Member(new Joint[] {d, e});
    	Member BE = new Member(new Joint[] {loadedB, e});
    	
    	testMembers.add(AB);
    	testMembers.add(BC);
    	testMembers.add(BD);
    	testMembers.add(AE);
    	testMembers.add(CD);
    	testMembers.add(DE);
    	testMembers.add(BE);
    	Truss testTruss = new Truss(testJoints, testMembers, -1000);
    	System.out.println(testTruss.analyze());
    	System.out.println(testTruss);*/
    	
    	ArrayList<Joint> testJoints = new ArrayList<>();
    	ArrayList<Member> testMembers = new ArrayList<>();
    	
    	Joint jA = new Joint(new double[] {0, 0});
    	Joint jB = new Joint(new double[] {15, 11.28});
    	Joint jC = new Joint(new double[] {28, 0});
    	Joint jD = new Joint(new double[] {15.48, -7.26});
    	Joint jE = new Joint(new double[] {25.94, 3.5});
    	
    	testJoints.add(jA);
    	testJoints.add(jC);
    	testJoints.add(jB);
    	testJoints.add(jD);
    	testJoints.add(jE);
    	
    	Member AB = new Member(new Joint[] {jA, jB}, 1.5, 2, true);
    	Member AD = new Member(new Joint[] {jA, jD}, 1.7, 2, false);
    	Member BD = new Member(new Joint[] {jB, jD}, 1.25, 2, true);
    	Member BE = new Member(new Joint[] {jB, jE}, 1, 2, true);
    	Member CD = new Member(new Joint[] {jC, jD}, 0.75, 2, true);
    	Member CE = new Member(new Joint[] {jC, jE}, 1.2, 2, true);
    	Member DE = new Member(new Joint[] {jD, jE}, 0.75, 2, false);
    	
    	testMembers.add(AB);
    	testMembers.add(AD);
    	testMembers.add(BD);
    	testMembers.add(BE);
    	testMembers.add(CD);
    	testMembers.add(CE);
    	testMembers.add(DE);
    	
    	Truss testTruss = new Truss(testJoints, testMembers, - 1000);
    	System.out.println(testTruss.analyze());
    	System.out.println(testTruss);
    }
}
