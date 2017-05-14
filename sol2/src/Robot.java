import java.util.Random;
/**
 * The robot move the tagged bike
 */
public class Robot extends BicycleHandlingThread {

	protected Belt belt;
	public Robot(Belt belt){
		 super();
	     this.belt = belt;
	}
	
	 public  void run() {
	        while (!isInterrupted()) {
	            try {
	            	belt.robotMove();  	
	            } catch (InterruptedException e) {
	                this.interrupt();
	            }
	        }
	        System.out.println("Sensor terminated");
	    }
		
}
