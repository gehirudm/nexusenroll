#!/bin/bash
# Start Admin HTTP Service on port 8083
cd "$(dirname "$0")"
echo "Starting Admin Service on port 8083..."
java -cp out admin.AdminServiceStarter