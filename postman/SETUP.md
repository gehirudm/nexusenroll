# Postman Setup Guide for NexusEnroll Microservices

## 📋 Prerequisites

1. **Postman Application**
   - Download from: https://www.postman.com/downloads/
   - Or use Postman Web: https://web.postman.com/

2. **NexusEnroll Services Running**
   ```bash
   # Start all services
   ./start_all_services.sh
   
   # Or start individually  
   ./start_student_service.sh   # Port 8081
   ./start_faculty_service.sh   # Port 8082
   ./start_admin_service.sh     # Port 8083
   ```

3. **Verify Services are Running**
   ```bash
   # Quick health check
   curl http://localhost:8081/health
   curl http://localhost:8082/health  
   curl http://localhost:8083/health
   ```

## 🔄 Step-by-Step Import Process

### Step 1: Import Collections

1. **Open Postman**
2. **Click "Import" (top-left corner)**
3. **Select "Upload Files" or drag & drop**
4. **Import all collection files:**
   - `postman/collections/NexusEnroll-Complete.postman_collection.json`
   - `postman/collections/NexusEnroll-Student-Service.postman_collection.json`
   - `postman/collections/NexusEnroll-Faculty-Service.postman_collection.json`
   - `postman/collections/NexusEnroll-Admin-Service.postman_collection.json`
5. **Click "Import"**

### Step 2: Import Environments

1. **Click "Import" again**
2. **Import all environment files:**
   - `postman/environments/Local-Development.postman_environment.json`
   - `postman/environments/Docker-Environment.postman_environment.json`
   - `postman/environments/Production.postman_environment.json`
3. **Click "Import"**

### Step 3: Select Environment

1. **Click the environment dropdown** (top-right corner)
2. **Select "Local Development"** for local testing
3. **Verify environment variables** by clicking the eye icon 👁️

## ✅ Quick Verification

### Test Individual Services

1. **Open "NexusEnroll - Complete Microservices" collection**
2. **Navigate to "Health Checks" folder**
3. **Click "Student Service Health"**
4. **Click "Send" button**
5. **Verify response shows:** `{"status":"healthy","service":"student","port":8081}`
6. **Repeat for Faculty and Admin services**

### Run Complete Test Suite

1. **Right-click on "NexusEnroll - Complete Microservices" collection**
2. **Click "Run collection"**
3. **Click "Run NexusEnroll - Complete Microservices"**
4. **Watch tests execute and verify all pass**

## 🎯 Test Execution Options

### Option 1: Complete Workflow Test
```
Collection: NexusEnroll - Complete Microservices
Folder: Integration Workflows → Complete Enrollment Workflow
```
This runs a full end-to-end scenario:
1. Enroll student
2. View updated roster
3. Submit grade  
4. Generate report

### Option 2: Service-Specific Testing
```
Student Service: POST /students/S001/enrollments
Faculty Service: GET /courses/CS201/roster  
Admin Service: GET /admin/reports/enrollments
```

### Option 3: Error Scenario Testing
```
Each collection has "Error Scenarios" or "Negative Scenarios" folders
Test invalid inputs, missing resources, etc.
```

## 🛠️ Customization

### Modify Test Data
1. **Click environment dropdown → "Local Development"**
2. **Click "Edit" (pencil icon)**
3. **Modify variables:**
   - `studentId`: Change from "S001" to your test student
   - `courseId`: Change from "CS201" to your test course
   - `grade`: Change from "A" to desired grade
4. **Save changes**

### Add Custom Tests
1. **Right-click on any folder in a collection**
2. **Select "Add Request"**
3. **Configure endpoint URL and method**
4. **Add test scripts in the "Tests" tab:**
   ```javascript
   pm.test("Status code is 200", function () {
       pm.response.to.have.status(200);
   });
   ```

## 🔍 Understanding Test Results

### Successful Response
```json
{
    "success": true,
    "message": "Student S001 enrolled in CS201"
}
```

### Error Response  
```json
{
    "success": false,
    "message": "Course not found: CS999"
}
```

### Test Results Panel
- ✅ **Green checkmark**: Test passed
- ❌ **Red X**: Test failed  
- **Numbers**: Show passed/total tests

## 🚨 Troubleshooting

### Common Issues & Solutions

#### ❌ **Connection Refused**
```
Error: connect ECONNREFUSED 127.0.0.1:8081
```
**Solution:** Start the services first
```bash
./start_all_services.sh
```

#### ❌ **Environment Variables Not Working**
```
Error: Invalid URL: {{baseUrl}}/health
```
**Solution:** 
1. Select correct environment from dropdown
2. Verify environment variables are set

#### ❌ **Tests Failing**
```
AssertionError: expected 404 to equal 200
```
**Solution:**
1. Check if test data exists (students S001, S002)
2. Run health checks first
3. Check service logs for errors

#### ❌ **JSON Parse Error**
```
JSONError: Unexpected token < in JSON at position 0
```
**Solution:**
1. Service might be returning HTML error page
2. Check service is running on correct port
3. Verify endpoint URL is correct

### Debug Steps
1. **Check Console Tab** in Postman for detailed logs
2. **Inspect Response Headers** and body  
3. **Run Individual Requests** to isolate issues
4. **Check Service Logs** in terminal where services are running

## 📊 Sample Test Output

### Successful Health Check
```
Request: GET http://localhost:8081/health
Status: 200 OK
Response Time: 45ms
Tests: ✅ Status code is 200
       ✅ Service is healthy
```

### Successful Enrollment  
```
Request: POST http://localhost:8081/students/S001/enrollments  
Body: {"courseId": "CS201"}
Status: 200 OK
Response Time: 120ms
Tests: ✅ Status code is 200
       ✅ Enrollment successful
```

## 🎯 Next Steps

Once setup is complete:

1. **Explore Collections**: Browse different request folders
2. **Run Integration Tests**: Execute complete workflows  
3. **Customize for Your Needs**: Modify test data and scenarios
4. **Monitor Performance**: Check response times and reliability
5. **Integrate CI/CD**: Use Newman for automated testing

## 📞 Support

If you encounter issues:
1. Check the main README.md for troubleshooting
2. Verify services are running with health checks
3. Check Postman console for detailed error messages
4. Review service logs for backend issues

Happy Testing! 🚀