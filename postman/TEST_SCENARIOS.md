# NexusEnroll API Test Scenarios

## Overview

This document describes comprehensive test scenarios for the NexusEnroll microservices architecture, covering success cases, failure cases, edge cases, and integration scenarios.

## Test Categories

### 1. Unit Tests (Individual Service Operations)

#### Student Service Tests
- **Enrollment Success**: Valid student with prerequisites enrolls in available course
- **Enrollment Failures**: 
  - Missing prerequisites
  - Course at capacity
  - Time conflicts
  - Student already enrolled
- **Drop Success**: Enrolled student successfully drops course
- **Drop Failures**: Student not enrolled in course

#### Faculty Service Tests  
- **Roster Operations**: View enrolled students for courses
- **Grade Management**:
  - Submit valid grades (A, B, C, D, F, P)
  - Grade state transitions (Pending → Submitted → Final)
  - Batch grade processing with mixed results
- **Grade Failures**:
  - Invalid grade letters
  - Attempting to modify final grades
  - Grading non-enrolled students

#### Admin Service Tests
- **Report Generation**: CSV enrollment reports with course capacity data
- **Course Management**: Create, update course capacity
- **Administrative Overrides**: Force-add students bypassing validation
- **Authorization**: Proper admin authentication required

### 2. Integration Tests (Cross-Service Workflows)

#### Complete Enrollment Workflow
1. Admin creates course with capacity and prerequisites
2. Student attempts enrollment (validates prerequisites, capacity, conflicts)  
3. Faculty views updated roster
4. Faculty submits and approves grades
5. Admin generates enrollment report showing updated data
6. Student drops course, triggering waitlist notifications

#### Error Cascading Tests
1. Faculty attempts to grade non-enrolled student
2. Multiple validation failures during enrollment
3. Admin attempts operations on non-existent entities
4. Service unavailability handling

### 3. Performance and Load Tests

#### Concurrent Operations
- Multiple students enrolling in same course simultaneously
- Capacity limits enforced under load
- Response time requirements (< 2 seconds typical operations)

#### Large Data Handling  
- Report generation with extensive enrollment history
- Batch grade submissions for large classes
- Database query performance with large datasets

## Validation Criteria

### Success Case Validations
- **HTTP Status**: Appropriate 2xx status codes
- **Response Structure**: Required fields present and correctly typed
- **Business Logic**: Operations follow enrollment rules and state transitions
- **Side Effects**: Proper notifications and data consistency
- **Response Time**: Operations complete within reasonable time bounds

### Failure Case Validations  
- **HTTP Status**: Appropriate 4xx/5xx error codes
- **Error Messages**: Clear, actionable error descriptions
- **Error Details**: Specific validation failures and context
- **Data Integrity**: Failed operations don't corrupt system state
- **Recovery**: System remains functional after errors

### Edge Case Testing
- **Boundary Conditions**: Course capacity limits, grade boundaries
- **Data Validation**: Malformed requests, invalid IDs, missing fields
- **State Transitions**: Invalid state changes, concurrent modifications
- **Authorization**: Access control and permission validation

## Test Data Management

### Environment Variables
- **Service URLs**: Configurable endpoints for each microservice
- **Test Entities**: Reusable student IDs, course codes, valid/invalid data
- **Authentication**: Admin tokens and user credentials
- **Timing**: Test execution delays and timeout settings

### Data Cleanup
- Tests use unique identifiers to avoid conflicts
- Integration tests create and clean up test data
- Failed tests don't impact subsequent test runs
- Test isolation ensures repeatable results

## Monitoring and Reporting

### Test Metrics
- **Pass/Fail Rates**: Success percentage by test category
- **Performance**: Response times and throughput measurements
- **Coverage**: API endpoint and business rule validation coverage
- **Reliability**: Test stability and flakiness detection

### Continuous Integration
- **Automated Execution**: Tests run on code changes and deployments
- **Environment Testing**: Validation across dev, staging, production environments  
- **Regression Detection**: Early detection of functionality breakage
- **Quality Gates**: Deployment blocked on test failures

## Usage Instructions

### Local Testing
1. Start all microservices (Student, Faculty, Admin)
2. Update environment variables with correct service URLs
3. Import collections into Postman
4. Run individual collections or complete test suite
5. Review test results and response data

### CI/CD Integration  
1. Use Newman CLI to run collections in automation
2. Generate JUnit XML reports for test result tracking
3. Set appropriate timeouts for service startup and response times
4. Configure retry logic for transient failures

### Test Debugging
1. Enable request/response logging in Postman
2. Use environment variables to control test data and timing
3. Run individual requests to isolate failures
4. Check service logs for detailed error information