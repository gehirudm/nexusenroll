package faculty;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import common.models.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * Faculty Service as a proper HTTP microservice
 * Endpoints:
 * GET /courses/{courseId}/roster - view course roster
 * POST /courses/{courseId}/grades - submit grades
 * GET /health - health check
 */
public class FacultyHttpService {
    private static final int PORT = 8082;
    private HttpServer server;
    
    // In-memory data store for demo purposes
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final Map<String, Grade> grades = new ConcurrentHashMap<>();
    
    public FacultyHttpService() {
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
        
        // Sample courses with enrolled students
        Course cs201 = new Course("CS201", "Algorithms", 10, "Mon9-11");
        cs201.addPrerequisite("CS101");
        
        // Create enrollments
        Enrollment e1 = new Enrollment(alice, cs201);
        Enrollment e2 = new Enrollment(bob, cs201);
        cs201.addEnrollment(e1);
        cs201.addEnrollment(e2);
        alice.addEnrollment(e1);
        bob.addEnrollment(e2);
        
        courses.put("CS201", cs201);
        
        Course bus101 = new Course("BUS101", "Intro Business", 50, "Tue10-12");
        courses.put("BUS101", bus101);
        
        System.out.println("Initialized faculty sample data: " + students.size() + " students, " + courses.size() + " courses");
    }
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Define endpoints
        server.createContext("/courses", new CourseHandler());
        server.createContext("/health", new HealthHandler());
        
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Faculty Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /courses/{courseId}/roster - view course roster");
        System.out.println("  POST /courses/{courseId}/grades - submit grades");
        System.out.println("  GET /health - health check");
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Faculty Service stopped");
        }
    }
    
    class CourseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            try {
                if ("GET".equals(method) && path.contains("/roster")) {
                    handleRosterView(exchange, path);
                } else if ("POST".equals(method) && path.contains("/grades")) {
                    handleGradeSubmission(exchange, path);
                } else {
                    sendResponse(exchange, 405, "Method not allowed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
            }
        }
        
        private void handleRosterView(HttpExchange exchange, String path) throws IOException {
            // Parse path: /courses/{courseId}/roster
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendResponse(exchange, 400, "Invalid path format. Expected: /courses/{courseId}/roster");
                return;
            }
            
            String courseId = parts[2];
            Course course = courses.get(courseId);
            
            if (course == null) {
                sendResponse(exchange, 404, "Course not found: " + courseId);
                return;
            }
            
            StringBuilder rosterJson = new StringBuilder();
            rosterJson.append("{\"courseId\":\"").append(courseId).append("\",");
            rosterJson.append("\"courseName\":\"").append(course.getName()).append("\",");
            rosterJson.append("\"students\":[");
            
            List<Enrollment> roster = course.getRoster();
            for (int i = 0; i < roster.size(); i++) {
                if (i > 0) rosterJson.append(",");
                Student student = roster.get(i).getStudent();
                rosterJson.append("{\"id\":\"").append(student.getId()).append("\",");
                rosterJson.append("\"name\":\"").append(student.getName()).append("\"}");
            }
            
            rosterJson.append("]}");
            
            sendResponse(exchange, 200, rosterJson.toString());
        }
        
        private void handleGradeSubmission(HttpExchange exchange, String path) throws IOException {
            // Parse path: /courses/{courseId}/grades
            String[] parts = path.split("/");
            if (parts.length < 4) {
                sendResponse(exchange, 400, "Invalid path format. Expected: /courses/{courseId}/grades");
                return;
            }
            
            String courseId = parts[2];
            Course course = courses.get(courseId);
            
            if (course == null) {
                sendResponse(exchange, 404, "Course not found: " + courseId);
                return;
            }
            
            // Read request body to get grade data
            String requestBody = readRequestBody(exchange);
            GradeSubmission gradeData = parseGradeSubmission(requestBody);
            
            if (gradeData == null) {
                sendResponse(exchange, 400, "Invalid grade submission format. Expected: {\"studentId\":\"S001\",\"grade\":\"A\"}");
                return;
            }
            
            Student student = students.get(gradeData.studentId);
            if (student == null) {
                sendResponse(exchange, 404, "Student not found: " + gradeData.studentId);
                return;
            }
            
            try {
                // Validate grade letter
                if (!isValidGradeLetter(gradeData.grade)) {
                    throw new RuntimeException("Invalid grade letter: " + gradeData.grade);
                }
                
                // Create and submit grade
                Grade grade = new Grade(student, course);
                grade.setLetter(gradeData.grade);
                grade.submit(); // Uses State pattern
                
                String gradeKey = student.getId() + "-" + course.getCode();
                grades.put(gradeKey, grade);
                
                String response = String.format(
                    "{\"success\":true,\"message\":\"Grade %s submitted for student %s in course %s\"}",
                    gradeData.grade, gradeData.studentId, courseId);
                
                sendResponse(exchange, 200, response);
                
            } catch (Exception e) {
                String response = String.format(
                    "{\"success\":false,\"message\":\"Failed to submit grade: %s\"}",
                    e.getMessage());
                sendResponse(exchange, 400, response);
            }
        }
        
        private boolean isValidGradeLetter(String letter) {
            return letter != null && (letter.matches("[ABCDF]") || letter.equals("P"));
        }
        
        private GradeSubmission parseGradeSubmission(String requestBody) {
            // Simple JSON parsing for studentId and grade
            if (requestBody == null) return null;
            
            String studentId = extractJsonValue(requestBody, "studentId");
            String grade = extractJsonValue(requestBody, "grade");
            
            if (studentId != null && grade != null) {
                return new GradeSubmission(studentId, grade);
            }
            
            return null;
        }
        
        private String extractJsonValue(String json, String key) {
            String searchKey = "\"" + key + "\":";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex >= 0) {
                int startIndex = keyIndex + searchKey.length();
                String value = json.substring(startIndex).trim();
                if (value.startsWith("\"")) {
                    int endIndex = value.indexOf("\"", 1);
                    if (endIndex > 0) {
                        return value.substring(1, endIndex);
                    }
                }
            }
            return null;
        }
    }
    
    static class GradeSubmission {
        final String studentId;
        final String grade;
        
        GradeSubmission(String studentId, String grade) {
            this.studentId = studentId;
            this.grade = grade;
        }
    }
    
    class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"healthy\",\"service\":\"faculty\",\"port\":" + PORT + "}";
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
        FacultyHttpService service = new FacultyHttpService();
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