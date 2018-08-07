package ee360t.controlflow.trace.agent;

public class TracerShutdownHook extends Thread {
    @Override
    public void run() {
        System.out.println( "Running shutdown hook" );
    }
}
