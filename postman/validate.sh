#!/bin/bash
# Validation script to verify Postman collections and environments are properly configured

echo "🧪 NexusEnroll Postman Configuration Validator"
echo "=============================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if file exists and is valid JSON
check_json_file() {
    local file=$1
    local type=$2
    
    if [ ! -f "$file" ]; then
        echo -e "${RED}❌ $type file not found: $file${NC}"
        return 1
    fi
    
    if ! python3 -m json.tool "$file" > /dev/null 2>&1; then
        echo -e "${RED}❌ Invalid JSON in $type: $file${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✅ Valid $type: $(basename "$file")${NC}"
    return 0
}

# Function to check if services are running
check_service() {
    local port=$1
    local service=$2
    
    if curl -s -f "http://localhost:$port/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service Service (port $port) is running${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠️  $service Service (port $port) is not running${NC}"
        return 1
    fi
}

echo ""
echo "📂 Checking Directory Structure..."
echo "--------------------------------"

# Check directories exist
if [ -d "postman/collections" ]; then
    echo -e "${GREEN}✅ Collections directory exists${NC}"
else
    echo -e "${RED}❌ Collections directory missing${NC}"
fi

if [ -d "postman/environments" ]; then
    echo -e "${GREEN}✅ Environments directory exists${NC}"
else
    echo -e "${RED}❌ Environments directory missing${NC}"
fi

echo ""
echo "📝 Validating Collection Files..."
echo "--------------------------------"

# Check collection files
collections=(
    "postman/collections/NexusEnroll-Complete.postman_collection.json"
    "postman/collections/NexusEnroll-Student-Service.postman_collection.json"
    "postman/collections/NexusEnroll-Faculty-Service.postman_collection.json"
    "postman/collections/NexusEnroll-Admin-Service.postman_collection.json"
)

collection_count=0
for collection in "${collections[@]}"; do
    if check_json_file "$collection" "Collection"; then
        ((collection_count++))
    fi
done

echo ""
echo "🌍 Validating Environment Files..."
echo "---------------------------------"

# Check environment files
environments=(
    "postman/environments/Local-Development.postman_environment.json"
    "postman/environments/Docker-Environment.postman_environment.json"
    "postman/environments/Production.postman_environment.json"
)

environment_count=0
for environment in "${environments[@]}"; do
    if check_json_file "$environment" "Environment"; then
        ((environment_count++))
    fi
done

echo ""
echo "🏥 Checking Service Health..."
echo "----------------------------"

# Check if services are running
service_count=0
if check_service 8081 "Student"; then ((service_count++)); fi
if check_service 8082 "Faculty"; then ((service_count++)); fi
if check_service 8083 "Admin"; then ((service_count++)); fi

echo ""
echo "📊 Validation Summary"
echo "===================="
echo "Collections found: $collection_count/4"
echo "Environments found: $environment_count/3"
echo "Services running: $service_count/3"

echo ""
if [ $collection_count -eq 4 ] && [ $environment_count -eq 3 ]; then
    echo -e "${GREEN}🎉 All Postman configurations are valid!${NC}"
    
    if [ $service_count -eq 3 ]; then
        echo -e "${GREEN}🚀 All services are running - ready for testing!${NC}"
        echo ""
        echo "📋 Quick Test Commands:"
        echo "curl http://localhost:8081/health"
        echo "curl http://localhost:8082/health" 
        echo "curl http://localhost:8083/health"
    else
        echo -e "${YELLOW}⚠️  Some services are not running. Start them with:${NC}"
        echo "./start_all_services.sh"
    fi
else
    echo -e "${RED}❌ Some configuration files are missing or invalid${NC}"
    exit 1
fi

echo ""
echo "📚 Next Steps:"
echo "1. Import collections and environments into Postman"
echo "2. Select 'Local Development' environment"
echo "3. Run the 'Health Checks' folder first"
echo "4. Execute full test suites"
echo ""
echo "📖 See postman/README.md for detailed instructions"