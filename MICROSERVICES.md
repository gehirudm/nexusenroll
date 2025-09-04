# NexusEnroll Microservices Architecture

## Overview
This project has been converted from a console-based proof-of-concept to a proper microservices architecture where each service runs as an independent HTTP server capable of receiving web requests.

## Services

### 1. Student Service (Port 8081)
**Purpose**: Manages student enrollment operations
**Endpoints**:
- `POST /students/{studentId}/enrollments` - Enroll student in a course
  - Body: `{"courseId": "CS201"}`
- `DELETE /students/{studentId}/enrollments/{courseId}` - Drop student from course  
- `GET /health` - Health check

**Example Usage**:
```bash
# Enroll student S001 in CS201
curl -X POST http://localhost:8081/students/S001/enrollments \
  -H "Content-Type: application/json" \
  -d '{"courseId":"CS201"}'

# Drop student from course
curl -X DELETE http://localhost:8081/students/S001/enrollments/CS201
```

### 2. Faculty Service (Port 8082)
**Purpose**: Manages course rosters and grade submissions
**Endpoints**:
- `GET /courses/{courseId}/roster` - View course enrollment roster
- `POST /courses/{courseId}/grades` - Submit grades for students
  - Body: `{"studentId": "S001", "grade": "A"}`
- `GET /health` - Health check

**Example Usage**:
```bash
# View course roster  
curl http://localhost:8082/courses/CS201/roster

# Submit grade
curl -X POST http://localhost:8082/courses/CS201/grades \
  -H "Content-Type: application/json" \
  -d '{"studentId":"S001","grade":"A"}'
```

### 3. Admin Service (Port 8083)
**Purpose**: Administrative operations and reporting
**Endpoints**:
- `GET /admin/reports/enrollments` - Generate enrollment reports
- `POST /admin/courses/{courseId}/students/{studentId}` - Force add student to course
- `GET /health` - Health check

**Example Usage**:
```bash
# Get enrollment report
curl http://localhost:8083/admin/reports/enrollments

# Force add student to course (admin override)
curl -X POST http://localhost:8083/admin/courses/BUS101/students/S001
```

## Running the Services

### Start All Services
```bash
./start_all_services.sh
```

### Start Individual Services
```bash
./start_student_service.sh  # Port 8081
./start_faculty_service.sh  # Port 8082
./start_admin_service.sh    # Port 8083
```

### Run Automated Demo
```bash
./test_microservices.sh
```

## Design Patterns Preserved

All original design patterns are preserved in the microservices implementation:

- **Factory Pattern**: `EnrollmentValidatorFactory` creates validation strategies
- **Strategy Pattern**: Multiple validation strategies (capacity, prerequisites, time conflicts)
- **Observer Pattern**: `MessageBroker` publishes enrollment events across services
- **State Pattern**: Grade lifecycle management (Pending → Submitted → Final)
- **Adapter Pattern**: `ReportAdapter` adapts CSV generation to JSON APIs
- **Facade Pattern**: `ServicesFacade` simplifies complex admin operations
- **Singleton Pattern**: `NotificationService` provides centralized notifications

## Technical Implementation

- **HTTP Server**: Uses Java's built-in `com.sun.net.httpserver.HttpServer`
- **No External Dependencies**: Maintains the original goal of no external libraries
- **JSON APIs**: All endpoints return JSON responses
- **Graceful Shutdown**: Services handle SIGTERM/SIGINT properly
- **Concurrent Processing**: Each service uses thread pools for request handling
- **In-Memory Data**: Sample data is initialized in each service for demonstration

## Key Benefits Achieved

1. **True Microservices**: Each service runs independently on its own port
2. **HTTP-Based Communication**: Services communicate via standard HTTP/REST APIs
3. **Independent Deployment**: Each service can be started/stopped independently
4. **Scalability**: Services can be scaled horizontally behind load balancers
5. **Technology Diversity**: Each service could use different tech stacks if needed
6. **Fault Isolation**: Failure in one service doesn't affect others

## Testing

The system includes comprehensive testing capabilities:
- Health check endpoints for monitoring
- Automated test script demonstrating all functionality
- Manual curl examples for each API
- Error handling and validation demonstrations

This conversion transforms the original console-based proof-of-concept into a production-ready microservices architecture suitable for real-world deployment.