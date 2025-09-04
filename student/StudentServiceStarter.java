package student;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Startup class for Student HTTP Service that can run as daemon
 */
public class StudentServiceStarter {
    private static StudentHttpService service;
    private static CountDownLatch shutdownLatch = new CountDownLatch(1);
    
    public static void main(String[] args) {
        // Handle graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down Student Service...");
            if (service != null) {
                service.stop();
            }
            shutdownLatch.countDown();
        }));
        
        try {
            service = new StudentHttpService();
            service.start();
            
            // Keep the service running
            shutdownLatch.await();
            
        } catch (Exception e) {
            System.err.println("Failed to start Student Service: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}