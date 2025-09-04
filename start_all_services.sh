#!/bin/bash
# Start all microservices concurrently
cd "$(dirname "$0")"

echo "Starting all NexusEnroll microservices..."
echo "====================================="

# Make scripts executable
chmod +x start_student_service.sh
chmod +x start_faculty_service.sh
chmod +x start_admin_service.sh

# Start services in background
echo "Starting Student Service (port 8081)..."
./start_student_service.sh &
STUDENT_PID=$!

sleep 2

echo "Starting Faculty Service (port 8082)..."
./start_faculty_service.sh &
FACULTY_PID=$!

sleep 2

echo "Starting Admin Service (port 8083)..."
./start_admin_service.sh &
ADMIN_PID=$!

sleep 2

echo ""
echo "All services started!"
echo "====================================="
echo "Student Service: http://localhost:8081"
echo "Faculty Service: http://localhost:8082"
echo "Admin Service:   http://localhost:8083"
echo ""
echo "Health checks:"
echo "curl http://localhost:8081/health"
echo "curl http://localhost:8082/health"  
echo "curl http://localhost:8083/health"
echo ""
echo "Press Ctrl+C to stop all services"

# Handle cleanup on exit
cleanup() {
    echo ""
    echo "Stopping all services..."
    kill $STUDENT_PID $FACULTY_PID $ADMIN_PID 2>/dev/null
    exit 0
}

trap cleanup INT

# Wait for services
wait