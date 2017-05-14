/**
 * The short belt move the tagged bike
 */
public class ShortBelt extends BicycleHandlingThread {


    protected Belt belt;

    public ShortBelt(Belt belt) {
    	super();
    	this.belt = belt;
    }
	
	 public  void run() {
		 
	        while (!isInterrupted()) {
	        	try {
	        		//put a bike on the short belt
	        		belt.shortPut();
	        		//move the short belt
	        		belt.shortBeltMove();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		
	        System.out.println("Sensor terminated");
	    }

}
