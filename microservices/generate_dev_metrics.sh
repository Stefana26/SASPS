#!/bin/bash
set -e

GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVICES_ROOT="$ROOT_DIR"
REPORT_FILE="$ROOT_DIR/DEV_METRICS_REPORT_MICROSERVICES.md"
TEMP_DIR="/tmp/dev_metrics_$$"

SERVICES=(
  booking-service
  room-service
  payment-service
  user-service
)

mkdir -p "$TEMP_DIR"

echo -e "${CYAN}Generating microservices development metrics...${NC}"

# Aggregated totals
TOTAL_LOC=0
TOTAL_JAVA_FILES=0
TOTAL_BUILD_TIME=0
TOTAL_TEST_TIME=0
TOTAL_DEPS=0

# Report header
echo "# Development Metrics – Microservices" > "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "## Per-service metrics" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "| Service | LOC | Java Files | Build Time (s) | Test Time (s) | JAR Size | Docker Build (s) | Image Size | Direct Deps | Total Deps |" >> "$REPORT_FILE"
echo "|--------|-----|------------|----------------|---------------|----------|------------------|------------|-------------|------------|" >> "$REPORT_FILE"

for SERVICE in "${SERVICES[@]}"; do
  SERVICE_DIR="$SERVICES_ROOT/$SERVICE"

  if [ ! -d "$SERVICE_DIR" ]; then
    echo "Skipping $SERVICE (not found)"
    continue
  fi

  echo -e "${CYAN}Processing $SERVICE...${NC}"
  cd "$SERVICE_DIR"

  # LOC
  if command -v cloc >/dev/null && [ -d src/main/java ]; then
    cloc src/main/java --json --quiet > "$TEMP_DIR/loc.json"
    LOC=$(jq -r '.SUM.code // 0' "$TEMP_DIR/loc.json")
  else
    LOC=0
  fi

  JAVA_FILES=$(find src/main/java -name "*.java" 2>/dev/null | wc -l | tr -d ' ')

  # Build time
  BUILD_START=$(date +%s.%N)
  mvn clean package -DskipTests -q > "$TEMP_DIR/build.log" 2>&1
  BUILD_END=$(date +%s.%N)
  BUILD_TIME=$(echo "$BUILD_END - $BUILD_START" | bc)

  JAR_FILE=$(ls target/*.jar 2>/dev/null | head -1)
  if [ -f "$JAR_FILE" ]; then
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
  else
    JAR_SIZE="N/A"
  fi

  # Test time
  if [ -d src/test/java ] && [ "$(find src/test/java -name '*.java' | wc -l)" -gt 0 ]; then
    TEST_START=$(date +%s.%N)
    mvn test -q > "$TEMP_DIR/test.log" 2>&1 || true
    TEST_END=$(date +%s.%N)
    TEST_TIME=$(echo "$TEST_END - $TEST_START" | bc)
  else
    TEST_TIME=0
  fi

  # Docker
  if command -v docker >/dev/null && [ -f Dockerfile ]; then
    IMAGE_NAME="$SERVICE:metrics-test"
    docker rmi "$IMAGE_NAME" >/dev/null 2>&1 || true

    DOCKER_START=$(date +%s.%N)
    docker build --no-cache -t "$IMAGE_NAME" . > "$TEMP_DIR/docker.log" 2>&1
    DOCKER_END=$(date +%s.%N)
    DOCKER_TIME=$(echo "$DOCKER_END - $DOCKER_START" | bc)
    IMAGE_SIZE=$(docker images "$IMAGE_NAME" --format "{{.Size}}" | head -1)
  else
    DOCKER_TIME="N/A"
    IMAGE_SIZE="N/A"
  fi

  # Dependencies
  mvn dependency:tree > "$TEMP_DIR/deps.txt" 2>&1
  TOTAL_DEP_COUNT=$(grep '^\[INFO\]' "$TEMP_DIR/deps.txt" | grep -E '(\+---|\\---|\|)' | wc -l | tr -d ' ')
  DIRECT_DEP_COUNT=$(grep -c "<dependency>" pom.xml || echo 0)

  # Aggregates
  TOTAL_LOC=$((TOTAL_LOC + LOC))
  TOTAL_JAVA_FILES=$((TOTAL_JAVA_FILES + JAVA_FILES))
  TOTAL_BUILD_TIME=$(echo "$TOTAL_BUILD_TIME + $BUILD_TIME" | bc)
  TOTAL_TEST_TIME=$(echo "$TOTAL_TEST_TIME + $TEST_TIME" | bc)
  TOTAL_DEPS=$((TOTAL_DEPS + TOTAL_DEP_COUNT))

  # Row
  echo "| $SERVICE | $LOC | $JAVA_FILES | ${BUILD_TIME}s | ${TEST_TIME}s | $JAR_SIZE | ${DOCKER_TIME}s | $IMAGE_SIZE | $DIRECT_DEP_COUNT | $TOTAL_DEP_COUNT |" >> "$REPORT_FILE"

done

# Aggregated section
echo "" >> "$REPORT_FILE"
echo "## Aggregated metrics" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "| Metric | Value |" >> "$REPORT_FILE"
echo "|--------|-------|" >> "$REPORT_FILE"
echo "| Total LOC | $TOTAL_LOC |" >> "$REPORT_FILE"
echo "| Total Java Files | $TOTAL_JAVA_FILES |" >> "$REPORT_FILE"
echo "| Total Build Time | ${TOTAL_BUILD_TIME}s |" >> "$REPORT_FILE"
echo "| Total Test Time | ${TOTAL_TEST_TIME}s |" >> "$REPORT_FILE"
echo "| Total Dependencies | $TOTAL_DEPS |" >> "$REPORT_FILE"

rm -rf "$TEMP_DIR"

echo -e "${GREEN}Report generated: $REPORT_FILE${NC}"
