/**
 * The inspecter check whether the bike is deficiency
 */
public class Inspector extends BicycleHandlingThread {
	protected Belt belt;

	
	public Inspector(Belt belt){
		 super();
	     this.belt = belt;
	}
	
	
	   public synchronized void run() {
	        while (!isInterrupted()) {
	        	try {
					belt.inspect();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	

	        }
	        
	    }
}
