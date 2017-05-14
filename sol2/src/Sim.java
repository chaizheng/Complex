/**
 * The driver of the simulation 
 */

public class Sim {
    /**
     * Create all components and start all of the threads.
     */
    public static void main(String[] args) {
        
        Belt belt = new Belt();
        Producer producer = new Producer(belt);
        Consumer consumer = new Consumer(belt);
        BeltMover mover = new BeltMover(belt);
        Sensor sensor = new Sensor(belt);
        Robot robot = new Robot(belt);
        ShortBelt shortbelt = new ShortBelt(belt);
        Inspector inspector = new Inspector(belt);
        Handler handler = new Handler(belt);

        consumer.start();
        producer.start();
        mover.start();
        sensor.start();
        robot.start();
        inspector.start();        
        shortbelt.start();
        handler.start();
        

        while (consumer.isAlive() && 
               producer.isAlive() && 
               mover.isAlive() &&
               sensor.isAlive() &&
               robot.isAlive() &&
               inspector.isAlive() &&
               shortbelt.isAlive() &&
               handler.isAlive())
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                BicycleHandlingThread.terminate(e);
            }
        

        // interrupt other threads
        consumer.interrupt();
        producer.interrupt();
        mover.interrupt();
        sensor.interrupt();
        robot.interrupt();
        inspector.interrupt();
        shortbelt.interrupt();
        handler.interrupt();

        System.out.println("Sim terminating");
        System.out.println(BicycleHandlingThread.getTerminateException());
        System.exit(0);
    }
}
