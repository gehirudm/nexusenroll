NexusEnroll Microservices (Java)
================================

This implementation demonstrates a true microservices architecture for a university course enrollment system.
Each service runs as an independent HTTP server capable of receiving web requests, making it a proper microservice architecture.

**Services:**
- **Student Service** (port 8081): Handles enrollment and drop operations
- **Faculty Service** (port 8082): Manages grade submission and roster viewing  
- **Admin Service** (port 8083): Provides enrollment reports and administrative overrides

**Design patterns implemented:**
 - Creational: Factory Method (EnrollmentValidatorFactory)
 - Structural: Adapter (ReportAdapter), Facade (ServicesFacade)
 - Behavioural: Strategy (validation strategies), Observer (MessageBroker/Notification), State (Grade state)

## Quick Start

### Option 1: Start All Services
```bash
./start_all_services.sh
```

### Option 2: Start Services Individually  
```bash
./start_student_service.sh  # Port 8081
./start_faculty_service.sh  # Port 8082
./start_admin_service.sh    # Port 8083
```

### Option 3: Run Original Console Demo
```bash
./run_all.sh  # Runs original console-based services
```

## Testing the Microservices

### Automated Demo
```bash
./test_microservices.sh  # Comprehensive API demo
```

### Manual Testing
```bash
# Student Service
curl http://localhost:8081/health
curl -X POST http://localhost:8081/students/S001/enrollments -d '{"courseId":"CS201"}'
curl -X DELETE http://localhost:8081/students/S001/enrollments/CS201

# Faculty Service  
curl http://localhost:8082/health
curl http://localhost:8082/courses/CS201/roster
curl -X POST http://localhost:8082/courses/CS201/grades -d '{"studentId":"S001","grade":"A"}'

# Admin Service
curl http://localhost:8083/health
curl http://localhost:8083/admin/reports/enrollments
curl -X POST http://localhost:8083/admin/courses/BUS101/students/S001
```

Project structure:
 - common: shared models, notification and message broker (Observer)
 - student: enrollment manager + validators (Factory + Strategy)
 - faculty: grade submission (State), roster viewing
 - admin: report generator + adapter
 - docs: UML diagrams (text / PlantUML)

This is a simplified in-memory PoC to demonstrate architecture + patterns. No external DB or web server included intentionally to focus on business logic layer.

