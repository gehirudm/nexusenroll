# NexusEnroll Microservices - Postman Test Configuration

This directory contains comprehensive Postman test configurations for all NexusEnroll microservices. The collections provide automated testing capabilities for API endpoints, validation of responses, and complete workflow testing.

## 📁 Directory Structure

```
postman/
├── collections/                     # Postman collection files
│   ├── NexusEnroll-Complete.postman_collection.json      # Complete test suite
│   ├── NexusEnroll-Student-Service.postman_collection.json   # Student service only
│   ├── NexusEnroll-Faculty-Service.postman_collection.json   # Faculty service only
│   └── NexusEnroll-Admin-Service.postman_collection.json     # Admin service only
├── environments/                    # Environment configuration files
│   ├── Local-Development.postman_environment.json        # Local development setup
│   ├── Docker-Environment.postman_environment.json       # Docker container setup
│   └── Production.postman_environment.json               # Production deployment
└── README.md                       # This file
```

## 🎯 Collections Overview

### 1. Complete Microservices Collection
**File:** `NexusEnroll-Complete.postman_collection.json`

The comprehensive test suite that covers all three microservices with:
- **Health Checks:** Verify all services are running
- **Student Service Tests:** Enrollment and drop operations
- **Faculty Service Tests:** Roster viewing and grade submissions  
- **Admin Service Tests:** Reports and administrative overrides
- **Integration Workflows:** Complete end-to-end scenarios
- **Error Scenarios:** Validation of error handling

### 2. Individual Service Collections

#### Student Service Collection
**File:** `NexusEnroll-Student-Service.postman_collection.json`
- Port: 8081
- Endpoints:
  - `GET /health` - Health check
  - `POST /students/{studentId}/enrollments` - Enroll in course
  - `DELETE /students/{studentId}/enrollments/{courseId}` - Drop course

#### Faculty Service Collection  
**File:** `NexusEnroll-Faculty-Service.postman_collection.json`
- Port: 8082
- Endpoints:
  - `GET /health` - Health check
  - `GET /courses/{courseId}/roster` - View course roster
  - `POST /courses/{courseId}/grades` - Submit grades

#### Admin Service Collection
**File:** `NexusEnroll-Admin-Service.postman_collection.json`  
- Port: 8083
- Endpoints:
  - `GET /health` - Health check
  - `GET /admin/reports/enrollments` - Generate enrollment reports
  - `POST /admin/courses/{courseId}/students/{studentId}` - Force add student

## 🌍 Environment Configurations

### Local Development
**File:** `Local-Development.postman_environment.json`
- **Student Service:** http://localhost:8081
- **Faculty Service:** http://localhost:8082  
- **Admin Service:** http://localhost:8083
- **Use Case:** Development on local machine

### Docker Environment
**File:** `Docker-Environment.postman_environment.json`
- **Student Service:** http://student-service:8081
- **Faculty Service:** http://faculty-service:8082
- **Admin Service:** http://admin-service:8083
- **Use Case:** Testing within Docker containers

### Production Environment
**File:** `Production.postman_environment.json`
- **Student Service:** https://student-service.nexusenroll.com
- **Faculty Service:** https://faculty-service.nexusenroll.com
- **Admin Service:** https://admin-service.nexusenroll.com
- **Use Case:** Production deployment testing

## 🚀 Quick Start

### Prerequisites
1. Postman installed (desktop app or web version)
2. NexusEnroll services running (use `./start_all_services.sh`)

### Import Collections and Environments

1. **Import Collections:**
   - Open Postman
   - Click "Import" button
   - Select all `.postman_collection.json` files from the `collections/` directory
   - Click "Import"

2. **Import Environments:**
   - Click "Import" button  
   - Select all `.postman_environment.json` files from the `environments/` directory
   - Click "Import"

3. **Select Environment:**
   - Choose "Local Development" from the environment dropdown (top-right)

### Running Tests

#### Option 1: Run Complete Test Suite
1. Open the "NexusEnroll - Complete Microservices" collection
2. Click "Run collection" 
3. Select all requests or specific folders
4. Click "Run NexusEnroll - Complete Microservices"

#### Option 2: Run Individual Service Tests  
1. Open any individual service collection
2. Click "Run collection"
3. Select desired test scenarios
4. Click "Run [Service Name]"

#### Option 3: Manual Testing
1. Open any collection
2. Click on individual requests
3. Modify parameters as needed
4. Click "Send" to execute

## 🧪 Test Features

### Automated Validation
Each request includes comprehensive test scripts that validate:
- **Response Status:** HTTP status codes (200, 400, 404, etc.)
- **Response Structure:** JSON schema validation  
- **Business Logic:** Enrollment rules, grade validation, etc.
- **Performance:** Response time thresholds
- **Data Integrity:** Cross-request data consistency

### Test Data Management
- **Environment Variables:** Configurable student IDs, course IDs, grades
- **Dynamic Data:** Tests store and reuse data between requests
- **Sample Data:** Pre-configured test scenarios with known data

### Error Testing
Comprehensive error scenario coverage:
- **404 Errors:** Non-existent students, courses
- **400 Errors:** Invalid request formats, invalid grades
- **409 Errors:** Business rule violations (capacity, prerequisites)

## 📊 Sample Test Data

The collections use the following sample data (matches the services' initialized data):

### Students
- **S001:** Alice (completed CS101)
- **S002:** Bob (completed CS101)

### Courses  
- **CS201:** Algorithms (capacity: 2, prerequisite: CS101, time: Mon9-11)
- **BUS101:** Intro Business (capacity: 50, time: Tue10-12)

### Valid Grades
- **Letter Grades:** A, B, C, D, F
- **Pass/Fail:** P

## 🔧 Customization

### Modifying Test Data
Edit environment variables to test with different data:
- `studentId` - Primary student for testing
- `altStudentId` - Secondary student for testing
- `courseId` - Primary course for testing  
- `altCourseId` - Secondary course for testing
- `grade` - Grade to submit in tests

### Adding Custom Tests
1. Open any collection
2. Right-click on a folder → "Add Request"
3. Configure endpoint and test scripts
4. Use existing requests as templates

### Environment Switching
Easily switch between environments:
1. Click environment dropdown (top-right)
2. Select desired environment
3. All requests will automatically use new base URLs

## 🏗️ Architecture Patterns Tested

The collections validate the implementation of key design patterns:

### Creational Patterns
- **Factory Method:** EnrollmentValidatorFactory creates validation strategies

### Structural Patterns  
- **Adapter Pattern:** CSV reports adapted to JSON in admin service
- **Facade Pattern:** ServicesFacade simplifies admin operations

### Behavioral Patterns
- **Strategy Pattern:** Multiple validation strategies for enrollments
- **Observer Pattern:** MessageBroker notifications (tested via logs)
- **State Pattern:** Grade lifecycle (Pending → Submitted → Final)

## 🐛 Troubleshooting

### Common Issues

**Services Not Responding:**
- Ensure all services are running: `./start_all_services.sh`
- Check health endpoints first
- Verify correct environment is selected

**Tests Failing:**
- Check service logs for errors
- Verify test data matches service initialization
- Run individual requests to isolate issues

**Environment Variables Not Working:**  
- Ensure correct environment is selected
- Check variable names match collection references
- Verify environment file import was successful

### Debug Tips
1. **Check Console:** View Postman console for detailed test results
2. **Inspect Responses:** Look at response body and headers  
3. **Test Scripts:** Add `console.log()` statements in test scripts
4. **Isolated Testing:** Run individual requests to narrow down issues

## 📈 Continuous Integration

### Newman CLI
Run collections from command line using Newman:

```bash
# Install Newman
npm install -g newman

# Run complete test suite
newman run collections/NexusEnroll-Complete.postman_collection.json \
  -e environments/Local-Development.postman_environment.json \
  --reporters cli,json \
  --reporter-json-export results.json

# Run individual service tests
newman run collections/NexusEnroll-Student-Service.postman_collection.json \
  -e environments/Local-Development.postman_environment.json
```

### CI/CD Integration
Example GitHub Actions workflow:

```yaml
name: API Tests
on: [push, pull_request]
jobs:
  api-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Start Services
        run: ./start_all_services.sh &
      - name: Wait for Services  
        run: sleep 30
      - name: Install Newman
        run: npm install -g newman
      - name: Run API Tests
        run: |
          newman run postman/collections/NexusEnroll-Complete.postman_collection.json \
            -e postman/environments/Local-Development.postman_environment.json
```

## 📝 Best Practices

### Test Organization
- **Logical Grouping:** Tests organized by service and scenario type
- **Descriptive Names:** Clear, descriptive request and test names
- **Documentation:** Each request includes detailed descriptions

### Test Maintenance  
- **Environment Variables:** Use variables for all configurable values
- **Reusable Scripts:** Common validation logic in collection-level scripts
- **Version Control:** All collections and environments in git

### Performance Testing
- **Response Times:** All tests include response time validations
- **Load Testing:** Use collection runner for basic load testing
- **Monitoring:** Integrate with monitoring tools for ongoing health checks

## 🤝 Contributing

When adding new endpoints or modifying existing ones:

1. **Update Collections:** Add requests to appropriate service collection
2. **Update Complete Collection:** Include new tests in comprehensive suite
3. **Add Test Scripts:** Include validation for all response scenarios
4. **Update Documentation:** Modify this README with new endpoint details
5. **Test Environments:** Verify tests work across all environments

## 📚 Additional Resources

- [Postman Learning Center](https://learning.postman.com/)
- [Newman Documentation](https://github.com/postmanlabs/newman)
- [JSON Schema Validation in Postman](https://learning.postman.com/docs/writing-scripts/script-references/test-examples/#validating-json-schema)
- [Postman Environment Variables](https://learning.postman.com/docs/sending-requests/variables/)

---

**Note:** These Postman configurations are designed to work with the NexusEnroll microservices architecture. Ensure all services are properly configured and running before executing the test collections.