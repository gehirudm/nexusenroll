# NexusEnroll PoC Implementation Documentation

## 0. Project Structure, Classes, Modules and Services

The project follows a microservices-inspired architecture with clear separation of concerns across three main service modules:

### File Structure
```
nexusenroll-poc/
├── common/                     # Shared components across services
│   ├── messagebus/            # Message broker for inter-service communication
│   ├── models/                # Domain entities (Student, Course, Enrollment, Grade)
│   └── notification/          # Observer pattern implementation
├── student/                   # Student service module
│   ├── validation/            # Enrollment validation strategies
│   └── EnrollmentManager.java # Core enrollment business logic
├── faculty/                   # Faculty service module
├── admin/                     # Administrator service module
│   └── report/               # Report generation with adapter pattern
└── docs/                     # Architecture documentation
```

### Key Classes and Modules

**Common Module (Shared Domain)**
- [`Student`](common/models/Student.java) - Student entity with completed courses and enrollments
- [`Course`](common/models/Course.java) - Course entity with capacity, prerequisites, and roster
- [`Enrollment`](common/models/Enrollment.java) - Association between student and course
- [`Grade`](common/models/Grade.java) - Grade entity with state pattern implementation
- [`MessageBroker`](common/messagebus/MessageBroker.java) - Pub/Sub messaging system
- [`NotificationService`](common/notification/NotificationService.java) - Singleton notification manager

**Student Service**
- [`EnrollmentManager`](student/EnrollmentManager.java) - Core enrollment business logic
- [`EnrollmentValidator`](student/validation/EnrollmentValidator.java) - Strategy interface for validation
- [`EnrollmentValidatorFactory`](student/validation/EnrollmentValidatorFactory.java) - Factory for creating validators

**Faculty Service**
- [`FacultyServiceMain`](faculty/FacultyServiceMain.java) - Grade management and roster viewing

**Admin Service**
- [`ReportGenerator`](admin/report/ReportGenerator.java) - Report generation interface
- [`ServicesFacade`](admin/ServicesFacade.java) - Simplified interface for admin operations

## 1. Software Design Patterns Implementation

### Factory Method Pattern
**Location**: [`EnrollmentValidatorFactory`](student/validation/EnrollmentValidatorFactory.java)

```java
public static List<EnrollmentValidator> createValidators(){
    List<EnrollmentValidator> validators = new ArrayList<>();
    validators.add(new PrerequisiteValidator());
    validators.add(new CapacityValidator());
    validators.add(new TimeConflictValidator());
    return validators;
}
```

**Usage**: Creates different validation strategies without exposing instantiation logic to clients.

### Strategy Pattern
**Location**: Validation classes in [`student/validation`](student/validation)

```java
public interface EnrollmentValidator {
    boolean validate(Student s, Course c);
    String reason();
}

// Concrete strategies
public class PrerequisiteValidator implements EnrollmentValidator { ... }
public class CapacityValidator implements EnrollmentValidator { ... }
public class TimeConflictValidator implements EnrollmentValidator { ... }
```

**Usage**: Different validation algorithms are encapsulated and can be selected at runtime.

### Observer Pattern
**Location**: [`MessageBroker`](common/messagebus/MessageBroker.java) and [`NotificationService`](common/notification/NotificationService.java)

```java
public class MessageBroker {
    public void publish(String topic, String message){
        System.out.println("[MessageBroker] Publishing topic=" + topic);
        ns.notifyAll(topic, message);
    }
    
    public void subscribe(NotificationListener l){
        ns.register(l);
    }
}
```

**Usage**: Decoupled communication between services when enrollment events occur.

### State Pattern
**Location**: Grade state classes ([`PendingState`](common/models/PendingState.java), [`SubmittedState`](common/models/SubmittedState.java), [`FinalState`](common/models/FinalState.java))

```java
public class PendingState implements GradeState {
    public void submit(Grade g){
        System.out.println("Submitting grade -> moving to SubmittedState");
        g.setState(new SubmittedState());
    }
}
```

**Usage**: Grade lifecycle management with different behaviors based on current state.

### Singleton Pattern
**Location**: [`NotificationService`](common/notification/NotificationService.java)

```java
public static synchronized NotificationService getInstance(){
    if(instance == null) instance = new NotificationService();
    return instance;
}
```

**Usage**: Single point of notification management across the application.

### Adapter Pattern
**Location**: [`ReportAdapter`](admin/report/ReportAdapter.java)

```java
public class ReportAdapter implements ReportGenerator {
    private final ConcreteCsvReportGenerator adaptee = new ConcreteCsvReportGenerator();
    
    public String generateEnrollmentReport(List<Course> courses){
        return adaptee.createCSV(courses);
    }
}
```

**Usage**: Adapts third-party CSV generator to match internal report interface.

### Facade Pattern
**Location**: [`ServicesFacade`](admin/ServicesFacade.java)

```java
public void forceAddStudentToCourse(Student s, Course c){
    Enrollment e = new Enrollment(s,c);
    c.addEnrollment(e);
    s.addEnrollment(e);
    System.out.println("[Facade] Force-added " + s + " to " + c);
}
```

**Usage**: Simplifies complex operations across multiple services for admin users.

## 2. Software Design Principles

### Single Responsibility Principle (SRP)
- Each validator class ([`PrerequisiteValidator`](student/validation/PrerequisiteValidator.java), [`CapacityValidator`](student/validation/CapacityValidator.java), [`TimeConflictValidator`](student/validation/TimeConflictValidator.java)) handles only one validation concern
- [`MessageBroker`](common/messagebus/MessageBroker.java) only handles message publishing/subscribing
- Each service module focuses on its domain responsibilities

### Open/Closed Principle (OCP)
- New validation strategies can be added without modifying existing code by implementing [`EnrollmentValidator`](student/validation/EnrollmentValidator.java)
- New grade states can be added by implementing [`GradeState`](common/models/GradeState.java)

### Liskov Substitution Principle (LSP)
- All validation implementations can substitute the [`EnrollmentValidator`](student/validation/EnrollmentValidator.java) interface
- All grade state implementations properly substitute [`GradeState`](common/models/GradeState.java)

### Interface Segregation Principle (ISP)
- Small, focused interfaces like [`EnrollmentValidator`](student/validation/EnrollmentValidator.java) and [`GradeState`](common/models/GradeState.java)
- [`NotificationListener`](common/notification/NotificationListener.java) contains only the necessary notification method

### Dependency Inversion Principle (DIP)
- [`EnrollmentManager`](student/EnrollmentManager.java) depends on [`EnrollmentValidator`](student/validation/EnrollmentValidator.java) abstraction, not concrete implementations
- High-level modules use interfaces rather than concrete classes

### Separation of Concerns
- Clear separation between validation logic, business logic, and presentation
- Domain models in common package are separate from service-specific logic

## 3. Microservice Architecture Justification

### Service Decomposition by Business Domain
The architecture divides functionality into three distinct services aligned with business domains:
- **Student Service**: Handles enrollment operations and validation
- **Faculty Service**: Manages grade submission and roster viewing  
- **Admin Service**: Provides reporting and administrative overrides

### Decoupled Communication
**Event-Driven Architecture**: The [`MessageBroker`](common/messagebus/MessageBroker.java) enables asynchronous communication between services:

```java
// In EnrollmentManager
broker.publish("enrollment", "Student " + s.getId() + " enrolled in " + c.getCode());
broker.publish("waitlist", "Seat opened in " + c.getCode());
```

This allows services to react to events without direct coupling.

### Independent Deployment and Scaling
Each service module ([`student`](student), [`faculty`](faculty), [`admin`](admin)) can be:
- Developed by separate teams
- Deployed independently
- Scaled based on specific load requirements

### Technology Diversity Support
While this PoC uses Java throughout, the clear service boundaries enable:
- Different programming languages per service
- Service-specific database choices
- Independent technology stack evolution

### Fault Isolation
Service boundaries prevent cascading failures:
- Faculty grade submission failures don't impact student enrollment
- Admin report generation issues don't affect core enrollment operations

### Business Alignment
Each service maps directly to organizational responsibilities:
- **Student Service**: Student-facing enrollment operations
- **Faculty Service**: Instructor workflows (grading, roster management)
- **Admin Service**: Administrative oversight and reporting

### Shared Infrastructure
The [`common`](common) package provides shared concerns:
- Domain models for consistent data representation
- Message broker for inter-service communication
- Notification system for cross-cutting concerns

This microservice approach provides the foundation for a scalable, maintainable university enrollment system that can evolve with changing business requirements while maintaining clear service boundaries