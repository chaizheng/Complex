import java.util.Random;

public class Handler extends BicycleHandlingThread {

	// the belt from which the consumer takes the bicycles
    protected Belt belt;

    /**
     * Create a new Handler that consumes from a short belt
     */
    public Handler(Belt belt) {
        super();
        this.belt = belt;
    }
    
    public void run() {
        while (!isInterrupted()) {
            try {
                belt.getShortEndBelt();

                // let some time pass ...
                Random random = new Random();
                int sleepTime = Params.CONSUMER_MIN_SLEEP + 
                		random.nextInt(Params.CONSUMER_MAX_SLEEP - 
                				Params.CONSUMER_MIN_SLEEP);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Handler terminated");
    }
}
