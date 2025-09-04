# NexusEnroll Postman Collections - Quick Start Guide

## What's Included

This folder contains comprehensive Postman configurations to test all NexusEnroll microservices:

### 📁 Collections
- **`NexusEnroll-StudentService.postman_collection.json`** - Student enrollment/drop operations
- **`NexusEnroll-FacultyService.postman_collection.json`** - Grade management and roster operations  
- **`NexusEnroll-AdminService.postman_collection.json`** - Administrative and reporting functions
- **`NexusEnroll-CompleteTestSuite.postman_collection.json`** - Integration tests and workflows

### 🛠️ Configuration
- **`NexusEnroll-Environment.postman_environment.json`** - Environment variables and test data
- **`newman-config.json`** - Newman CLI configuration for automated testing
- **`run-tests.sh`** - Automated test runner script

### 📚 Documentation
- **`README.md`** - Comprehensive API documentation and usage guide
- **`TEST_SCENARIOS.md`** - Detailed test scenarios and validation criteria
- **`QUICKSTART.md`** - This quick start guide

## 🚀 Quick Start (5 Minutes)

### Option 1: Postman GUI
1. **Import Collections**: Open Postman → Import → Select all `.json` files in this folder
2. **Set Environment**: Click environment dropdown → Select "NexusEnroll Environment"  
3. **Update URLs**: Edit environment variables to match your service URLs
4. **Run Tests**: Select a collection → Click "Run" → Review results

### Option 2: Command Line (Newman)
```bash
# Install Newman
npm install -g newman newman-reporter-html

# Run all tests
./run-tests.sh

# View results
open results/test-summary.html
```

## 🎯 Test Coverage

### ✅ Success Cases
- Valid enrollment with prerequisites
- Grade submission and approval workflow
- Administrative course management
- Report generation with proper data

### ❌ Failure Cases  
- Missing prerequisites validation
- Course capacity enforcement
- Invalid grade letters rejection
- Unauthorized access prevention

### 🔄 Integration Scenarios
- End-to-end enrollment workflow
- Cross-service data consistency
- Error cascading and recovery
- Performance under load

## 📊 Expected Results

Each collection includes **comprehensive assertions** that validate:

- **HTTP Status Codes**: Correct 2xx/4xx/5xx responses
- **Response Structure**: Required fields and data types
- **Business Logic**: Enrollment rules and state transitions  
- **Error Handling**: Clear error messages and recovery
- **Performance**: Response times under acceptable limits

## 🏗️ API Architecture

The tests assume RESTful APIs wrapping the existing Java services:

```
Student Service (Port 8081)
├── POST /api/students/{id}/enrollments    # Enroll
├── DELETE /api/students/{id}/enrollments/{courseId}  # Drop
└── GET /api/students/{id}/enrollments     # List

Faculty Service (Port 8082)  
├── GET /api/courses/{id}/roster           # View roster
├── POST /api/courses/{id}/grades          # Submit grade
├── PUT /api/courses/{id}/grades/{studentId}  # Update grade
└── POST /api/courses/{id}/grades/batch    # Batch grades

Admin Service (Port 8083)
├── GET /api/reports/enrollments           # Generate report
├── POST /api/admin/enrollments            # Force enroll
├── POST /api/courses                      # Create course  
└── PUT /api/courses/{id}                  # Update course
```

## 🔧 Customization

### Environment Variables
Update `NexusEnroll-Environment.postman_environment.json`:
- Service URLs for different environments (dev/staging/prod)
- Test data (student IDs, course codes, etc.)
- Authentication tokens and credentials
- Timeout and delay settings

### Adding New Tests
1. Open collection in Postman
2. Add new request with appropriate method/URL
3. Add pre-request scripts for data setup
4. Add test scripts with assertions
5. Export updated collection

### CI/CD Integration
```yaml
# Example GitHub Actions workflow
- name: Run API Tests
  run: |
    npm install -g newman
    cd postman
    ./run-tests.sh
    
- name: Publish Test Results
  uses: dorny/test-reporter@v1
  with:
    name: NexusEnroll API Tests
    path: 'postman/results/*.xml'
    reporter: java-junit
```

## 🐛 Troubleshooting

### Common Issues
- **Services Not Running**: Ensure all microservices are started on correct ports
- **Network Issues**: Check firewall settings and URL accessibility
- **Test Failures**: Review service logs for detailed error information
- **Environment Issues**: Verify environment variables match your setup

### Debug Mode
```bash
# Run with verbose output
./run-tests.sh --verbose

# Run individual collection
newman run NexusEnroll-StudentService.postman_collection.json \
  -e NexusEnroll-Environment.postman_environment.json \
  --verbose
```

## 📞 Support

For issues or questions:
1. Check the detailed documentation in `README.md` and `TEST_SCENARIOS.md`
2. Review the Newman configuration in `newman-config.json`
3. Examine the test runner script `run-tests.sh` for automation details

## 🚀 Next Steps

1. **Run the tests** to validate current functionality
2. **Customize environment** variables for your setup  
3. **Add new test scenarios** based on your requirements
4. **Integrate with CI/CD** for automated quality assurance
5. **Monitor results** and track quality metrics over time