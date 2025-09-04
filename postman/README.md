# NexusEnroll Postman API Testing Suite

This directory contains Postman collections and configurations for testing the NexusEnroll microservices architecture.

## Architecture Overview

The NexusEnroll system consists of three main microservices:

1. **Student Service** - Handles enrollment and drop operations
2. **Faculty Service** - Manages grades and course rosters  
3. **Admin Service** - Administrative functions and reporting

## API Design

Since the current system is a console application, this Postman collection assumes a RESTful API wrapper around the existing Java services. The API endpoints are designed based on the business operations identified in the codebase.

## Collections

- `NexusEnroll-StudentService.postman_collection.json` - Student enrollment operations
- `NexusEnroll-FacultyService.postman_collection.json` - Faculty grade management
- `NexusEnroll-AdminService.postman_collection.json` - Administrative operations
- `NexusEnroll-Environment.postman_environment.json` - Environment variables

## Test Coverage

Each collection includes:
- **Success Cases**: Valid operations that should complete successfully
- **Failure Cases**: Invalid operations that should return appropriate errors
- **Edge Cases**: Boundary conditions and error handling scenarios

## Usage

1. Import all collection files into Postman
2. Import the environment file
3. Set the appropriate base URLs for each service
4. Run individual requests or entire collections
5. Review test results and assertions

## API Endpoints

### Student Service (Port 8081)
- `POST /api/students/{studentId}/enrollments` - Enroll student in course
- `DELETE /api/students/{studentId}/enrollments/{courseId}` - Drop student from course
- `GET /api/students/{studentId}/enrollments` - List student enrollments

### Faculty Service (Port 8082)  
- `GET /api/courses/{courseId}/roster` - View course roster
- `POST /api/courses/{courseId}/grades` - Submit grade for student
- `PUT /api/courses/{courseId}/grades/{studentId}` - Update existing grade
- `POST /api/courses/{courseId}/grades/batch` - Batch grade submission

### Admin Service (Port 8083)
- `GET /api/reports/enrollments` - Generate enrollment report
- `POST /api/admin/enrollments` - Force add student to course
- `POST /api/courses` - Create new course
- `PUT /api/courses/{courseId}` - Update course details