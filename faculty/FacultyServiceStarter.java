package faculty;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Startup class for Faculty HTTP Service that can run as daemon
 */
public class FacultyServiceStarter {
    private static FacultyHttpService service;
    private static CountDownLatch shutdownLatch = new CountDownLatch(1);
    
    public static void main(String[] args) {
        // Handle graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down Faculty Service...");
            if (service != null) {
                service.stop();
            }
            shutdownLatch.countDown();
        }));
        
        try {
            service = new FacultyHttpService();
            service.start();
            
            // Keep the service running
            shutdownLatch.await();
            
        } catch (Exception e) {
            System.err.println("Failed to start Faculty Service: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}