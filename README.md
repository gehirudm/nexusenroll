NexusEnroll PoC (Java)
======================

This Proof-of-Concept simulates a microservices architecture for a university course enrolment system.
It focuses on core business modules: student, faculty, and administrator (as separate packages/services).
Design patterns used (examples):
 - Creational: Factory Method (EnrollmentValidatorFactory)
 - Structural: Adapter (ReportAdapter), Facade (ServicesFacade)
 - Behavioural: Strategy (validation strategies), Observer (MessageBroker/Notification), State (Grade state)

How to run (simple, no external dependencies):
1. Compile all Java sources:
   javac -d out $(find . -name "*.java")
2. Run simulation mains:
   java -cp out student.StudentServiceMain
   java -cp out faculty.FacultyServiceMain
   java -cp out admin.AdminServiceMain

Or run the provided run_all.sh to compile and run sequentially.

Project structure:
 - common: shared models, notification and message broker (Observer)
 - student: enrollment manager + validators (Factory + Strategy)
 - faculty: grade submission (State), roster viewing
 - admin: report generator + adapter
 - docs: UML diagrams (text / PlantUML)

This is a simplified in-memory PoC to demonstrate architecture + patterns. No external DB or web server included intentionally to focus on business logic layer.

