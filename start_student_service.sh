#!/bin/bash
# Start Student HTTP Service on port 8081
cd "$(dirname "$0")"
echo "Starting Student Service on port 8081..."
java -cp out student.StudentServiceStarter