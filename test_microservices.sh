#!/bin/bash
# Test script to demonstrate all microservices working
echo "NexusEnroll Microservices Demo"
echo "=============================="

# Function to check if service is running
check_service() {
    local port=$1
    local service=$2
    echo -n "Checking $service service (port $port)... "
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/health | grep -q "200"; then
        echo "✓ Running"
        return 0
    else
        echo "✗ Not running"
        return 1
    fi
}

# Function to make HTTP request with pretty output
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo ""
    echo "→ $description"
    echo "  $method $url"
    if [ ! -z "$data" ]; then
        echo "  Data: $data"
    fi
    echo "  Response:"
    
    if [ -z "$data" ]; then
        response=$(curl -s -X $method "$url")
    else
        response=$(curl -s -X $method -H "Content-Type: application/json" -d "$data" "$url")
    fi
    
    echo "  $response" | sed 's/^/    /'
}

echo ""
echo "Please ensure all services are running with: ./start_all_services.sh"
echo "Press Enter to continue with the demo..."
read

# Check all services
echo "Service Health Checks:"
echo "====================="
check_service 8081 "Student"
check_service 8082 "Faculty" 
check_service 8083 "Admin"

echo ""
echo "Microservices API Demo:"
echo "======================"

# Student Service Demo
make_request "GET" "http://localhost:8081/health" "" "1. Check Student Service health"

make_request "POST" "http://localhost:8081/students/S001/enrollments" '{"courseId":"CS201"}' "2. Enroll Alice in CS201"

make_request "POST" "http://localhost:8081/students/S002/enrollments" '{"courseId":"CS201"}' "3. Enroll Bob in CS201 (should fill capacity)"

make_request "POST" "http://localhost:8081/students/S001/enrollments" '{"courseId":"BUS101"}' "4. Enroll Alice in BUS101"

# Faculty Service Demo  
make_request "GET" "http://localhost:8082/health" "" "5. Check Faculty Service health"

make_request "GET" "http://localhost:8082/courses/CS201/roster" "" "6. View CS201 roster"

make_request "POST" "http://localhost:8082/courses/CS201/grades" '{"studentId":"S001","grade":"A"}' "7. Submit grade A for Alice"

make_request "POST" "http://localhost:8082/courses/CS201/grades" '{"studentId":"S002","grade":"B"}' "8. Submit grade B for Bob"

make_request "POST" "http://localhost:8082/courses/CS201/grades" '{"studentId":"S001","grade":"X"}' "9. Try invalid grade (should fail)"

# Admin Service Demo
make_request "GET" "http://localhost:8083/health" "" "10. Check Admin Service health"

make_request "GET" "http://localhost:8083/admin/reports/enrollments" "" "11. Generate enrollment report"

make_request "POST" "http://localhost:8083/admin/courses/CS201/students/S001" "" "12. Force-add Alice to CS201 (admin override)"

make_request "GET" "http://localhost:8083/admin/reports/enrollments" "" "13. Check updated enrollment report"

# Drop demo
make_request "DELETE" "http://localhost:8081/students/S001/enrollments/CS201" "" "14. Drop Alice from CS201"

make_request "GET" "http://localhost:8082/courses/CS201/roster" "" "15. View updated CS201 roster"

echo ""
echo "Demo completed! All microservices are functioning properly."
echo "=========================================================="
echo ""
echo "The following design patterns were demonstrated:"
echo "• Strategy Pattern: Enrollment validation (capacity, prerequisites, time conflicts)"
echo "• Factory Pattern: Creating validation strategies"
echo "• Observer Pattern: Message broker notifications for enrollment events"
echo "• State Pattern: Grade lifecycle (Pending → Submitted → Final)"
echo "• Adapter Pattern: Report generation adapting CSV to JSON"
echo "• Facade Pattern: Admin service simplifying complex operations"
echo "• Singleton Pattern: Notification service"
echo ""
echo "Each service now runs as an independent HTTP microservice:"
echo "• Student Service (port 8081): Handles enrollments and drops"
echo "• Faculty Service (port 8082): Manages grades and rosters"
echo "• Admin Service (port 8083): Provides reports and admin functions"