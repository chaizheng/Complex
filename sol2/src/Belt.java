import java.util.Random;

/**
 * The bicycle quality control belt
 */
public class Belt {

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength = 5;
    
    protected Boolean isTaged = false;//sensor
    protected Bicycle inspectBicy = null;//the bike which will be inspected by robot  
    protected Bicycle shortBicy = null;//the bike which will be moved to short belt 
        
    //the short belt
    protected Bicycle[] shortSegment = new Bicycle[2];

    // the length of short belt
    protected int shortBeltLength = 2;
    
    

    // to help format output trace
    final private static String indentation = "                  ";

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
    
    public synchronized void sense()
    		throws InterruptedException{
    	
    	while(segment[2] == null){
    		wait();
    	}
    	Bicycle bike = segment[2];
    	
    	
    	if(bike.tagged == true){
    		 System.out.print( indentation + indentation);
       		 System.out.println(bike + " is taged");
       		 this.isTaged = true;
       		       		 
       	}else{
       		System.out.print( indentation + indentation);
       		System.out.println(bike + " is not taged");
       		this.isTaged = false;
       	}
    	
    	

    	notifyAll();
    	//wait new bike
    	while(bike == segment[2]){
    		wait();
    	}
    	
    }
    
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
    		
    	}else{
    		System.out.print( indentation + indentation);
    		System.out.println(bike + " is not defective");
    		bike.tagged = false;
    		
    	}
    	
    	this.inspectBicy = null;
    	this.shortBicy = bike;
    	
    	
    	notifyAll();
    	
    	
    	while(this.inspectBicy == null || this.inspectBicy == bike){
    		wait();
    	}
    	
    }
    
    public synchronized void robotMove()
            throws InterruptedException{
   	   while(this.isTaged == false  ){
   		   wait();
   	   }
   	   //is moving
   	Random random = new Random();
    int sleepTime = random.nextInt(Params.ROBOT_MOVE_TIME);       
    Thread.sleep(sleepTime);
    
   	   Bicycle bike = this.segment[2];
   	   this.inspectBicy = bike;
   	   System.out.println(bike + " is being moved to inspector");
   	   this.segment[2] = null;
   	   notifyAll();
   	   
   	   
   	while(bike == segment[2]|| segment[2] == null){
		wait();
	}
   	   
   }
    
    public synchronized void shortPut()
            throws InterruptedException {

    	// while there is another bicycle in the way, block this thread
        while (shortSegment[0]!= null || this.shortBicy == null) {
            wait();
        }
        
        
        Bicycle bike = this.shortBicy;

        shortSegment[0] = bike;
        this.shortBicy = null;

        // make a note of the event in output trace
        System.err.println(indentation + indentation + 
        		bike + " arrived to short belt");

        // notify any waiting threads that the belt has changed
        notifyAll();
        
        while(bike == this.shortBicy || this.shortBicy == null){
        	wait();
        }
    }
    
    public synchronized void shortBeltMove()
    		throws InterruptedException{
    	
    	while(shortSegment[0] == null  ||shortSegment[1] != null ){
    		wait();
    	}
    	
    	Random random = new Random();
        int sleepTime = random.nextInt(Params.BELT_MOVE_TIME);       
        Thread.sleep(sleepTime);
    	
    	Bicycle bike1 = shortSegment[0];
 
    	shortSegment[0] = null;
    	shortSegment[1] = bike1;
    	System.out.println( indentation + indentation+ bike1 + 
    			"is moved to 2 segment at the short belt");
    	    	

      notifyAll();
      while(shortSegment[1] != null){
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
        System.out.print(indentation + indentation);
        System.out.println(bicycle + " departed");

        // notify any waiting threads that the belt has changed
        notifyAll();
        return bicycle;
    }
    
    public synchronized Bicycle getShortEndBelt() throws InterruptedException{
    	Bicycle bicycle;

        // while there is no bicycle at the end of the belt, block this thread
        while (this.shortSegment[1] == null) {
            wait();
        }

        // get the next item
        bicycle = this.shortSegment[1];
        shortSegment[1] = null;

        // make a note of the event in output trace
       
        System.out.println(indentation + indentation + bicycle + " departed at the short belt");

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
