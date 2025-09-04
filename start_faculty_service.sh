#!/bin/bash
# Start Faculty HTTP Service on port 8082
cd "$(dirname "$0")"
echo "Starting Faculty Service on port 8082..."
java -cp out faculty.FacultyServiceStarter