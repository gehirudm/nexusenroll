package admin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import admin.report.*;
import common.models.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Admin Service as a proper HTTP microservice
 * Endpoints:
 * GET /admin/reports/enrollments - generate enrollment reports
 * POST /admin/courses/{courseId}/students/{studentId} - force add student
 * GET /health - health check
 */
public class AdminHttpService {
    private static final int PORT = 8083;
    private HttpServer server;
    private final ServicesFacade facade;
    private final ReportGenerator reportGenerator;
    
    // In-memory data store for demo purposes
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    
    public AdminHttpService() {
        this.facade = new ServicesFacade();
        this.reportGenerator = new ReportAdapter();
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
        
        // Sample courses with some enrollments
        Course cs201 = new Course("CS201", "Algorithms", 2, "Mon9-11");
        cs201.addPrerequisite("CS101");
        
        Course bus101 = new Course("BUS101", "Intro Business", 50, "Tue10-12");
        
        // Add some enrollments for reporting
        Enrollment e1 = new Enrollment(alice, cs201);
        Enrollment e2 = new Enrollment(bob, cs201);
        cs201.addEnrollment(e1);
        cs201.addEnrollment(e2);
        alice.addEnrollment(e1);
        bob.addEnrollment(e2);
        
        courses.put("CS201", cs201);
        courses.put("BUS101", bus101);
        
        System.out.println("Initialized admin sample data: " + students.size() + " students, " + courses.size() + " courses");
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Define endpoints
        server.createContext("/admin", new AdminHandler());
        server.createContext("/health", new HealthHandler());
        
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Admin Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /admin/reports/enrollments - generate enrollment reports");
        System.out.println("  POST /admin/courses/{courseId}/students/{studentId} - force add student");
        System.out.println("  GET /health - health check");
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Admin Service stopped");
        }
    }
    
    class AdminHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            try {
                if ("GET".equals(method) && path.contains("/reports/enrollments")) {
                    handleEnrollmentReport(exchange);
                } else if ("POST".equals(method) && path.contains("/courses/") && path.contains("/students/")) {
                    handleForceAddStudent(exchange, path);
                } else {
                    sendResponse(exchange, 405, "Method not allowed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
            }
        }
        
        private void handleEnrollmentReport(HttpExchange exchange) throws IOException {
            try {
                List<Course> courseList = new ArrayList<>(courses.values());
                String csvReport = reportGenerator.generateEnrollmentReport(courseList);
                
                // Convert CSV to JSON format for better API response
                String jsonReport = convertCsvToJson(csvReport);
                
                sendResponse(exchange, 200, jsonReport);
                
            } catch (Exception e) {
                String errorResponse = "{\"success\":false,\"message\":\"Failed to generate report: " + e.getMessage() + "\"}";
                sendResponse(exchange, 500, errorResponse);
            }
        }
        
        private void handleForceAddStudent(HttpExchange exchange, String path) throws IOException {
            // Parse path: /admin/courses/{courseId}/students/{studentId}
            String[] parts = path.split("/");
            if (parts.length < 6) {
                sendResponse(exchange, 400, "Invalid path format. Expected: /admin/courses/{courseId}/students/{studentId}");
                return;
            }
            
            String courseId = parts[3];
            String studentId = parts[5];
            
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
            
            try {
                // Use facade to force add student
                facade.forceAddStudentToCourse(student, course);
                
                String response = String.format(
                    "{\"success\":true,\"message\":\"Student %s force-added to course %s. New enrollment count: %d\"}",
                    studentId, courseId, course.getRoster().size());
                
                sendResponse(exchange, 200, response);
                
            } catch (Exception e) {
                String response = String.format(
                    "{\"success\":false,\"message\":\"Failed to force-add student: %s\"}",
                    e.getMessage());
                sendResponse(exchange, 500, response);
            }
        }
        
        private String convertCsvToJson(String csvData) {
            String[] lines = csvData.trim().split("\n");
            StringBuilder json = new StringBuilder();
            json.append("{\"report\":\"enrollment\",\"data\":[");
            
            // Skip header line
            for (int i = 1; i < lines.length; i++) {
                if (i > 1) json.append(",");
                
                String[] fields = lines[i].split(",");
                if (fields.length >= 3) {
                    json.append("{\"course\":\"").append(fields[0]).append("\",");
                    json.append("\"enrolled\":").append(fields[1]).append(",");
                    json.append("\"capacity\":").append(fields[2]).append("}");
                }
            }
            
            json.append("]}");
            return json.toString();
        }
    }
    
    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"healthy\",\"service\":\"admin\",\"port\":" + PORT + "}";
            sendResponse(exchange, 200, response);
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
        AdminHttpService service = new AdminHttpService();
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