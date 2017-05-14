import java.util.Random;
/**
 * The sensor sense whether the bike is tagged
 */

public class Sensor extends BicycleHandlingThread {

	protected Belt belt;
	protected Bicycle bicycle = null ;
	protected Boolean isTaged = false;
	
	Sensor(Belt belt) {
	        super();
	        this.belt = belt;
	    }
	
	

	 public  void run() {
	        while (!isInterrupted()) {
	            try {
	            	belt.sense();	             
	            } catch (InterruptedException e) {
	                this.interrupt();
	            }
	        }
	        System.out.println("Sensor terminated");
	    }

}
