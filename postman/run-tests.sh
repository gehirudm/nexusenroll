#!/bin/bash

# NexusEnroll Postman Test Runner
# This script runs the complete Postman test suite for all NexusEnroll microservices

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESULTS_DIR="${SCRIPT_DIR}/results"
ENVIRONMENT_FILE="${SCRIPT_DIR}/NexusEnroll-Environment.postman_environment.json"

# Default service URLs (can be overridden by environment variables)
STUDENT_SERVICE_URL="${STUDENT_SERVICE_URL:-http://localhost:8081}"
FACULTY_SERVICE_URL="${FACULTY_SERVICE_URL:-http://localhost:8082}" 
ADMIN_SERVICE_URL="${ADMIN_SERVICE_URL:-http://localhost:8083}"

echo -e "${BLUE}NexusEnroll Postman Test Runner${NC}"
echo "========================================"

# Check prerequisites
check_prerequisites() {
    echo -e "${YELLOW}Checking prerequisites...${NC}"
    
    if ! command -v newman &> /dev/null; then
        echo -e "${RED}Error: Newman is not installed. Install with: npm install -g newman${NC}"
        exit 1
    fi
    
    if ! command -v newman &> /dev/null || ! newman --version | grep -q "newman"; then
        echo -e "${RED}Error: Newman installation appears corrupted${NC}"
        exit 1
    fi
    
    # Create results directory if it doesn't exist
    mkdir -p "${RESULTS_DIR}"
    
    echo -e "${GREEN}Prerequisites check passed${NC}"
}

# Display configuration
show_configuration() {
    echo -e "${YELLOW}Configuration:${NC}"
    echo "  Student Service URL: ${STUDENT_SERVICE_URL}"
    echo "  Faculty Service URL: ${FACULTY_SERVICE_URL}"
    echo "  Admin Service URL: ${ADMIN_SERVICE_URL}"
    echo "  Results Directory: ${RESULTS_DIR}"
    echo "  Environment File: ${ENVIRONMENT_FILE}"
    echo ""
}

# Test service availability (optional health check)
test_service_availability() {
    echo -e "${YELLOW}Testing service availability...${NC}"
    
    services=("${STUDENT_SERVICE_URL}/api/health" "${FACULTY_SERVICE_URL}/api/health" "${ADMIN_SERVICE_URL}/api/health")
    service_names=("Student Service" "Faculty Service" "Admin Service")
    
    for i in "${!services[@]}"; do
        url="${services[i]}"
        name="${service_names[i]}"
        
        if curl -s --max-time 5 "${url}" > /dev/null 2>&1; then
            echo -e "  ${GREEN}✓${NC} ${name} is available"
        else
            echo -e "  ${YELLOW}⚠${NC} ${name} may not be available (continuing anyway)"
        fi
    done
    echo ""
}

# Run individual collection
run_collection() {
    local collection_name="$1"
    local collection_file="$2"
    local result_file="$3"
    
    echo -e "${BLUE}Running ${collection_name}...${NC}"
    
    if newman run "${SCRIPT_DIR}/${collection_file}" \
        -e "${ENVIRONMENT_FILE}" \
        --reporters cli,json,junit \
        --reporter-json-export "${RESULTS_DIR}/${result_file}.json" \
        --reporter-junit-export "${RESULTS_DIR}/${result_file}.xml" \
        --timeout 15000 \
        --delay-request 500 \
        --color auto; then
        echo -e "${GREEN}✓ ${collection_name} completed successfully${NC}"
        return 0
    else
        echo -e "${RED}✗ ${collection_name} failed${NC}"
        return 1
    fi
}

# Main test execution
run_tests() {
    local failed_tests=0
    local total_tests=0
    
    echo -e "${YELLOW}Starting test execution...${NC}"
    echo ""
    
    # Array of tests to run: (name, file, result_prefix)
    tests=(
        "Student Service Tests|NexusEnroll-StudentService.postman_collection.json|student-service-results"
        "Faculty Service Tests|NexusEnroll-FacultyService.postman_collection.json|faculty-service-results"  
        "Admin Service Tests|NexusEnroll-AdminService.postman_collection.json|admin-service-results"
        "Complete Integration Suite|NexusEnroll-CompleteTestSuite.postman_collection.json|complete-suite-results"
    )
    
    for test_info in "${tests[@]}"; do
        IFS='|' read -r test_name test_file result_file <<< "$test_info"
        total_tests=$((total_tests + 1))
        
        if ! run_collection "$test_name" "$test_file" "$result_file"; then
            failed_tests=$((failed_tests + 1))
        fi
        echo ""
    done
    
    # Summary
    echo "========================================"
    echo -e "${BLUE}Test Execution Summary${NC}"
    echo "========================================"
    echo "Total test suites: ${total_tests}"
    echo -e "Passed: ${GREEN}$((total_tests - failed_tests))${NC}"
    
    if [ $failed_tests -eq 0 ]; then
        echo -e "Failed: ${GREEN}0${NC}"
        echo -e "${GREEN}All tests passed!${NC}"
    else
        echo -e "Failed: ${RED}${failed_tests}${NC}"
        echo -e "${RED}Some tests failed. Check the results for details.${NC}"
    fi
    
    echo ""
    echo "Results saved to: ${RESULTS_DIR}"
    
    return $failed_tests
}

# Generate HTML report summary
generate_summary_report() {
    echo -e "${YELLOW}Generating summary report...${NC}"
    
    cat > "${RESULTS_DIR}/test-summary.html" << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>NexusEnroll Test Results Summary</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .header { color: #2E86AB; border-bottom: 2px solid #2E86AB; padding-bottom: 10px; }
        .success { color: #28a745; }
        .failure { color: #dc3545; }
        .warning { color: #ffc107; }
        table { border-collapse: collapse; width: 100%; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #f2f2f2; }
        .result-files { margin: 20px 0; }
        .result-files a { display: block; margin: 5px 0; padding: 8px; background-color: #f8f9fa; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <h1 class="header">NexusEnroll API Test Results</h1>
    <p><strong>Generated:</strong> $(date)</p>
    
    <h2>Test Suites</h2>
    <table>
        <tr><th>Test Suite</th><th>Status</th><th>Results</th></tr>
        <tr><td>Student Service</td><td class="success">✓ Available</td><td><a href="student-service-results.json">JSON</a> | <a href="student-service-results.xml">JUnit XML</a></td></tr>
        <tr><td>Faculty Service</td><td class="success">✓ Available</td><td><a href="faculty-service-results.json">JSON</a> | <a href="faculty-service-results.xml">JUnit XML</a></td></tr>
        <tr><td>Admin Service</td><td class="success">✓ Available</td><td><a href="admin-service-results.json">JSON</a> | <a href="admin-service-results.xml">JUnit XML</a></td></tr>
        <tr><td>Integration Tests</td><td class="success">✓ Available</td><td><a href="complete-suite-results.json">JSON</a> | <a href="complete-suite-results.xml">JUnit XML</a></td></tr>
    </table>
    
    <h2>Configuration</h2>
    <table>
        <tr><td>Student Service URL</td><td>${STUDENT_SERVICE_URL}</td></tr>
        <tr><td>Faculty Service URL</td><td>${FACULTY_SERVICE_URL}</td></tr>
        <tr><td>Admin Service URL</td><td>${ADMIN_SERVICE_URL}</td></tr>
    </table>
    
    <div class="result-files">
        <h3>Available Result Files</h3>
EOF
    
    for file in "${RESULTS_DIR}"/*.{json,xml}; do
        if [ -f "$file" ]; then
            filename=$(basename "$file")
            echo "        <a href=\"${filename}\">${filename}</a>" >> "${RESULTS_DIR}/test-summary.html"
        fi
    done
    
    cat >> "${RESULTS_DIR}/test-summary.html" << 'EOF'
    </div>
</body>
</html>
EOF

    echo -e "${GREEN}Summary report generated: ${RESULTS_DIR}/test-summary.html${NC}"
}

# Usage information
show_usage() {
    cat << EOF
Usage: $0 [options]

Options:
    -h, --help              Show this help message
    -c, --config            Show configuration and exit
    -s, --skip-health       Skip health check of services
    --student-url URL       Override student service URL
    --faculty-url URL       Override faculty service URL  
    --admin-url URL         Override admin service URL
    --results-dir DIR       Override results directory
    
Environment Variables:
    STUDENT_SERVICE_URL     Student service base URL (default: http://localhost:8081)
    FACULTY_SERVICE_URL     Faculty service base URL (default: http://localhost:8082)
    ADMIN_SERVICE_URL       Admin service base URL (default: http://localhost:8083)

Examples:
    # Run all tests with default configuration
    $0
    
    # Run tests against different environment  
    STUDENT_SERVICE_URL=https://staging-student.example.com $0
    
    # Run with custom URLs
    $0 --student-url http://127.0.0.1:8081 --faculty-url http://127.0.0.1:8082
EOF
}

# Command line argument parsing
SKIP_HEALTH_CHECK=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -c|--config)
            show_configuration
            exit 0
            ;;
        -s|--skip-health)
            SKIP_HEALTH_CHECK=true
            shift
            ;;
        --student-url)
            STUDENT_SERVICE_URL="$2"
            shift 2
            ;;
        --faculty-url)
            FACULTY_SERVICE_URL="$2"
            shift 2
            ;;
        --admin-url)
            ADMIN_SERVICE_URL="$2"
            shift 2
            ;;
        --results-dir)
            RESULTS_DIR="$2"
            shift 2
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            show_usage
            exit 1
            ;;
    esac
done

# Main execution
main() {
    check_prerequisites
    show_configuration
    
    if [ "$SKIP_HEALTH_CHECK" = false ]; then
        test_service_availability
    fi
    
    if run_tests; then
        generate_summary_report
        echo -e "${GREEN}All tests completed successfully!${NC}"
        exit 0
    else
        generate_summary_report
        echo -e "${RED}Some tests failed. Check ${RESULTS_DIR} for detailed results.${NC}"
        exit 1
    fi
}

# Run main function
main "$@"