#!/bin/bash

set -e

GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPORT_FILE="$SCRIPT_DIR/DEV_METRICS_REPORT.md"
TEMP_DIR="/tmp/dev_metrics_$$"

mkdir -p "$TEMP_DIR"

echo -e "${CYAN}Generare metrici dezvoltare...${NC}\n"

cd "$SCRIPT_DIR"

# LOC
if command -v cloc &> /dev/null; then
    echo "Calculare LOC..."
    cloc "$SCRIPT_DIR/src/main/java" --json --quiet > "$TEMP_DIR/loc_total.json"
    LOC_AVAILABLE=true
else
    LOC_AVAILABLE=false
fi

# Maven build time
echo "Masurare Maven build time..."
BUILD_START=$(date +%s.%N)
mvn clean package -DskipTests -q > "$TEMP_DIR/build.log" 2>&1
BUILD_END=$(date +%s.%N)
MAVEN_BUILD_TIME=$(echo "$BUILD_END - $BUILD_START" | bc)

if [ -f "target/hotel-booking-monolith-1.0.0.jar" ]; then
    JAR_SIZE=$(du -h "target/hotel-booking-monolith-1.0.0.jar" | cut -f1)
else
    JAR_SIZE="N/A"
fi

# Test time
if [ -d "src/test/java" ] && [ "$(find src/test/java -name '*.java' | wc -l)" -gt 0 ]; then
    echo "Masurare test time..."
    TEST_START=$(date +%s.%N)
    mvn test -q > "$TEMP_DIR/test.log" 2>&1 || true
    TEST_END=$(date +%s.%N)
    TEST_TIME=$(echo "$TEST_END - $TEST_START" | bc)
    TEST_AVAILABLE=true
else
    TEST_AVAILABLE=false
    TEST_TIME="N/A"
fi

# Java files count
JAVA_FILES=$(find src/main/java -name "*.java" | wc -l | tr -d ' ')

# Docker build time (include Maven build)
if command -v docker &> /dev/null; then
    echo "Docker build..."
    DOCKER_BUILD_START=$(date +%s.%N)
    docker build -t hotel-booking-monolith:metrics-test . > "$TEMP_DIR/docker_build.log" 2>&1
    DOCKER_BUILD_END=$(date +%s.%N)
    DOCKER_BUILD_TIME=$(echo "$DOCKER_BUILD_END - $DOCKER_BUILD_START" | bc)
    DOCKER_IMAGE_SIZE=$(docker images hotel-booking-monolith:metrics-test --format "{{.Size}}" | head -1)
    DOCKER_AVAILABLE=true
else
    DOCKER_AVAILABLE=false
    DOCKER_BUILD_TIME="N/A"
    DOCKER_IMAGE_SIZE="N/A"
fi

# Dependencies
mvn dependency:tree > "$TEMP_DIR/dependencies.txt" 2>&1
DEPENDENCY_COUNT=$(grep '^\[INFO\]' "$TEMP_DIR/dependencies.txt" | grep -E '(\+---|\\---|\|)' | wc -l | tr -d ' ')
DIRECT_DEPS=$(grep -c "<dependency>" pom.xml || echo "0")

# Generate report
echo "Generare raport..."

cat > "$REPORT_FILE" << EOF
# Metrici Dezvoltare - Monolit

---

## Metrici

| Metric | Value |
|--------|-------|
EOF

if [ "$LOC_AVAILABLE" = true ]; then
    cat >> "$REPORT_FILE" << EOF
| Total LOC | $(jq -r '.SUM.code' "$TEMP_DIR/loc_total.json") |
EOF
else
    cat >> "$REPORT_FILE" << EOF
| Total LOC | N/A |
EOF
fi

cat >> "$REPORT_FILE" << EOF
| Java Files | $JAVA_FILES |
| Maven Build Time | ${MAVEN_BUILD_TIME}s |
| Test Time | ${TEST_TIME}s |
| JAR Size | $JAR_SIZE |
EOF

if [ "$DOCKER_AVAILABLE" = true ]; then
    cat >> "$REPORT_FILE" << EOF
| Docker Build Time | ${DOCKER_BUILD_TIME}s |
| Docker Image Size | $DOCKER_IMAGE_SIZE |
EOF
else
    cat >> "$REPORT_FILE" << EOF
| Docker Build Time | N/A |
| Docker Image Size | N/A |
EOF
fi

cat >> "$REPORT_FILE" << EOF
| Direct Dependencies | $DIRECT_DEPS |
| Total Dependencies | $DEPENDENCY_COUNT |

EOF

# Cleanup
rm -rf "$TEMP_DIR"
if [ "$DOCKER_AVAILABLE" = true ]; then
    docker rmi hotel-booking-monolith:metrics-test > /dev/null 2>&1 || true
fi

echo -e "${GREEN}Raport generat: $REPORT_FILE${NC}"
