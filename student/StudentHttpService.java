package student;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import common.models.*;
import common.messagebus.MessageBroker;
import common.notification.NotificationListener;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.Map;

/**
 * Student Service as a proper HTTP microservice
 * Endpoints:
 * POST /students/{studentId}/enrollments - enroll in course
 * DELETE /students/{studentId}/enrollments/{courseId} - drop course
 * GET /health - health check
 */
public class StudentHttpService {
    private static final int PORT = 8081;
    private final EnrollmentManager enrollmentManager;
    private final MessageBroker broker;
    private HttpServer server;
    
    // In-memory data store for demo purposes
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    
    public StudentHttpService() {
        this.enrollmentManager = new EnrollmentManager();
        this.broker = new MessageBroker();
        
        // Setup notification listener
        broker.subscribe(new NotificationListener() {
            public void onNotify(String topic, String message) {
                System.out.println("[StudentService Notification] topic=" + topic + " msg=" + message);
            }
        });
        
        // Initialize sample data
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Sample students
        Student alice = new Student("S001", "Alice");
        alice.addCompletedCourse("CS101");
        students.put("S001", alice);
        
        Student bob = new Student("S002", "Bob");  
        bob.addCompletedCourse("CS101");
        students.put("S002", bob);
        
        // Sample courses
        Course cs201 = new Course("CS201", "Algorithms", 2, "Mon9-11");
        cs201.addPrerequisite("CS101");
        courses.put("CS201", cs201);
        
        Course bus101 = new Course("BUS101", "Intro Business", 50, "Tue10-12");
        courses.put("BUS101", bus101);
        
        System.out.println("Initialized sample data: " + students.size() + " students, " + courses.size() + " courses");
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Define endpoints
        server.createContext("/students", new EnrollmentHandler());
        server.createContext("/health", new HealthHandler());
        
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Student Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  POST /students/{studentId}/enrollments - enroll in course");
        System.out.println("  DELETE /students/{studentId}/enrollments/{courseId} - drop course");
        System.out.println("  GET /health - health check");
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Student Service stopped");
        }
    }
    
    class EnrollmentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            try {
                if ("POST".equals(method)) {
                    handleEnrollment(exchange, path);
                } else if ("DELETE".equals(method)) {
                    handleDropping(exchange, path);
                } else {
                    sendResponse(exchange, 405, "Method not allowed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
            }
        }
        
        private void handleEnrollment(HttpExchange exchange, String path) throws IOException {
            // Parse path: /students/{studentId}/enrollments
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendResponse(exchange, 400, "Invalid path format. Expected: /students/{studentId}/enrollments");
                return;
            }
            
            String studentId = parts[2];
            
            // Read request body to get course ID
            String requestBody = readRequestBody(exchange);
            String courseId = extractCourseId(requestBody);
            
            if (courseId == null) {
                sendResponse(exchange, 400, "Missing courseId in request body. Expected JSON: {\"courseId\":\"CS201\"}");
                return;
            }
            
            Student student = students.get(studentId);
            Course course = courses.get(courseId);
            
            if (student == null) {
                sendResponse(exchange, 404, "Student not found: " + studentId);
                return;
            }
            
            if (course == null) {
                sendResponse(exchange, 404, "Course not found: " + courseId);
                return;
            }
            
            boolean success = enrollmentManager.enroll(student, course);
            
            if (success) {
                String response = String.format("{\"success\":true,\"message\":\"Student %s enrolled in %s\"}",
                    studentId, courseId);
                sendResponse(exchange, 200, response);
            } else {
                String response = String.format("{\"success\":false,\"message\":\"Failed to enroll student %s in %s\"}",
                    studentId, courseId);
                sendResponse(exchange, 409, response);
            }
        }
        
        private void handleDropping(HttpExchange exchange, String path) throws IOException {
            // Parse path: /students/{studentId}/enrollments/{courseId}
            String[] parts = path.split("/");
            if (parts.length < 5) {
                sendResponse(exchange, 400, "Invalid path format. Expected: /students/{studentId}/enrollments/{courseId}");
                return;
            }
            
            String studentId = parts[2];
            String courseId = parts[4];
            
            Student student = students.get(studentId);
            Course course = courses.get(courseId);
            
            if (student == null) {
                sendResponse(exchange, 404, "Student not found: " + studentId);
                return;
            }
            
            if (course == null) {
                sendResponse(exchange, 404, "Course not found: " + courseId);
                return;
            }
            
            boolean success = enrollmentManager.drop(student, course);
            
            if (success) {
                String response = String.format("{\"success\":true,\"message\":\"Student %s dropped from %s\"}",
                    studentId, courseId);
                sendResponse(exchange, 200, response);
            } else {
                String response = String.format("{\"success\":false,\"message\":\"Failed to drop student %s from %s\"}",
                    studentId, courseId);
                sendResponse(exchange, 409, response);
            }
        }
        
        private String extractCourseId(String requestBody) {
            // Simple JSON parsing for courseId
            if (requestBody != null && requestBody.contains("courseId")) {
                String[] parts = requestBody.split("\"courseId\":");
                if (parts.length > 1) {
                    String value = parts[1].trim();
                    if (value.startsWith("\"")) {
                        int endIndex = value.indexOf("\"", 1);
                        if (endIndex > 0) {
                            return value.substring(1, endIndex);
                        }
                    }
                }
            }
            return null;
        }
    }
    
    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"healthy\",\"service\":\"student\",\"port\":" + PORT + "}";
            sendResponse(exchange, 200, response);
        }
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    public static void main(String[] args) {
        StudentHttpService service = new StudentHttpService();
        try {
            service.start();
            System.out.println("Press Enter to stop the service...");
            System.in.read();
            service.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}