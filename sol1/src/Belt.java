import java.util.Random;

/**
 * The bicycle quality control belt
 */
public class Belt {

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength = 5;

    // to help format output trace
    final private static String indentation = "                  ";

    //whether the bike in segment 3 is tagged
    protected Boolean isTaged = false;
    //the bike who will be inspected
    protected Bicycle inspectBicy = null;
    
    /**
     * Create a new, empty belt, initialised as empty
     */
    public Belt() {
        segment = new Bicycle[beltLength];
        for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }
    }

    /**
     * Put a bicycle on the belt.
     * 
     * @param bicycle
     *            the bicycle to put onto the belt.
     * @param index
     *            the place to put the bicycle
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void put(Bicycle bicycle, int index)
            throws InterruptedException {

    	// while there is another bicycle in the way, block this thread
        while (segment[index] != null) {
            wait();
        }

        // insert the element at the specified location
        segment[index] = bicycle;

        // make a note of the event in output trace
        System.out.println(bicycle + " arrived");

        // notify any waiting threads that the belt has changed
        notifyAll();
    }
    
    /**
     * Sense a bicycle on the e sgment.
     *      
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    
    public synchronized void sense()
    		throws InterruptedException{
    	
    	while(segment[2] == null){
    		wait();
    	}
    	Bicycle bike = segment[2];
    	
    	//check whether the bike is inspected firstly
    	if(bike.inspected == false){
    		if(bike.tagged == true){
    		 System.out.print( indentation + indentation);
       		 System.out.println(bike + " is taged");
       		 this.isTaged = true;
       		       		 
       	}else{
       		System.out.print( indentation + indentation);
       		System.out.println(bike + " is not taged");
       		this.isTaged = false;
       	}
    	}
    	

    	notifyAll();
    	//wait for new bike
    	while(bike == segment[2]){
    		wait();
    	}
    	
    }
    
    /**
     * Move a bicycle by robot arm.
     *     
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    
    public synchronized void robotMove()
             throws InterruptedException{
    	while(this.isTaged == false && this.inspectBicy == null){
    		
    		wait();
    	}
    	
    	Random random = new Random();
        int sleepTime = random.nextInt(Params.ROBOT_MOVE_TIME);       
        Thread.sleep(sleepTime);
    	
    	Bicycle bike;
    	
    	//check whether a bike is being inspected
    	if(this.inspectBicy == null){
    		 bike = segment[2];
    		 segment[2] = null;
    		 System.out.print( indentation + indentation);
    		 System.out.println(bike + " is moved to inspector");
    		 this.inspectBicy = bike;
    	}else{
    		 bike = this.inspectBicy;
    		
    		 if(segment[2] == null){
    			 segment[2] = bike;
    			 System.out.print( indentation + indentation);
    			 System.out.println(bike + " is moved to segment");
    			 this.inspectBicy = null;
    		 }else{
    			 wait();
    		 }
    		 
    	}
    	notifyAll();

    	while(bike == segment[2]|| segment[2] == null&&this.inspectBicy == null){
    		wait();
    	}
    }

    /**
     * Inspect a bicycle by inspector.
     *     
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    
    public synchronized void inspect()
           throws InterruptedException{
    	
    	while(this.inspectBicy == null){
    		wait();
    	}
    	//is inspecting
    	Random random = new Random();
        int sleepTime = random.nextInt(Params.INSPECT_TIME);       
        Thread.sleep(sleepTime);
    	
    	Bicycle bike = this.inspectBicy;
    	if(bike.isDefective() == true){
    		System.out.print( indentation + indentation);
    		System.out.println(bike + " is defective");
    		bike.tagged = true;
    		bike.inspected = true;
    	}else{
    		System.out.print( indentation + indentation);
    		System.out.println(bike + " is not defective");
    		bike.tagged = false;
    		bike.inspected = true;
    	}
    	
    	notifyAll();
    	
    	
    	while(this.inspectBicy == bike){
    		wait();
    	}
    	
    	
    }
    /**
     * Take a bicycle off the end of the belt
     * 
     * @return the removed bicycle
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Bicycle getEndBelt() throws InterruptedException {

        Bicycle bicycle;

        // while there is no bicycle at the end of the belt, block this thread
        while (segment[segment.length-1] == null) {
            wait();
        }

        // get the next item
        bicycle = segment[segment.length-1];
        segment[segment.length-1] = null;

        // make a note of the event in output trace
        
        System.out.print( indentation + indentation);
        System.out.println(bicycle + " departed");

        // notify any waiting threads that the belt has changed
        notifyAll();
        return bicycle;
    }

    /**
     * Move the belt along one segment
     * 
     * @throws OverloadException
     *             if there is a bicycle at position beltLength.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void move() 
            throws InterruptedException, OverloadException {
        // if there is something at the end of the belt, 
    	// or the belt is empty, do not move the belt
        while (isEmpty() || segment[segment.length-1] != null) {
            wait();
        }
        
        while(this.inspectBicy != null){
        	System.err.println(this.inspectBicy +" is inspected, please wait");
        	//System.out.println(this.inspectBicy);
        	wait();
        }

        // double check that a bicycle cannot fall of the end
        if (segment[segment.length-1] != null) {
            String message = "Bicycle fell off end of " + " belt";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = segment.length-1; i > 0; i--) {
            if (this.segment[i-1] != null) {
            	
                System.out.println( 
                		indentation +
                		this.segment[i-1] +
                        " [ s" + (i) + " -> s" + (i+1) +" ]");
            }
            segment[i] = segment[i-1];
        }
        segment[0] = null;
        // notify any waiting threads that the belt has changed
        notifyAll();
        //System.out.println("Fuck");
    }

    /**
     * @return the maximum size of this belt
     */
    public int length() {
        return beltLength;
    }

    /**
     * Peek at what is at a specified segment
     * 
     * @param index
     *            the index at which to peek
     * @return the bicycle in the segment (or null if the segment is empty)
     */
    public Bicycle peek(int index) {
        Bicycle result = null;
        if (index >= 0 && index < beltLength) {
            result = segment[index];
        }
        return result;
    }

    /**
     * Check whether the belt is currently empty
     * @return true if the belt is currently empty, otherwise false
     */
    private boolean isEmpty() {
        for (int i = 0; i < segment.length; i++) {
            if (segment[i] != null) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return java.util.Arrays.toString(segment);
    }

    /*
     * @return the final position on the belt
     */
    public int getEndPos() {
        return beltLength-1;
    }
}
