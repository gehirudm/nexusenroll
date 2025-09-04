package admin;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Startup class for Admin HTTP Service that can run as daemon
 */
public class AdminServiceStarter {
    private static AdminHttpService service;
    private static CountDownLatch shutdownLatch = new CountDownLatch(1);
    
    public static void main(String[] args) {
        // Handle graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down Admin Service...");
            if (service != null) {
                service.stop();
            }
            shutdownLatch.countDown();
        }));
        
        try {
            service = new AdminHttpService();
            service.start();
            
            // Keep the service running
            shutdownLatch.await();
            
        } catch (Exception e) {
            System.err.println("Failed to start Admin Service: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}