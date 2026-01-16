#!/bin/bash

set -e

SCENARIO="${1:-load}"
REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
RUN_DIR="${REPO_ROOT}/artifacts/perf/$(date +%Y%m%d_%H%M%S)"
MONOLITH_DIR="${REPO_ROOT}/monolith"
MICROSERVICES_DIR="${REPO_ROOT}/microservices"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Create output directory
mkdir -p "$RUN_DIR"

echo -e "${GREEN}=== Performance Comparison: Monolith vs Microservices ===${NC}"
echo "Scenario: $SCENARIO"
echo "Output directory: $RUN_DIR"
echo ""

# Helper function to wait for service health
wait_for_service() {
    local url=$1
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Service responding: $url${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        echo "Waiting for service: $url (attempt $attempt/$max_attempts)"
        sleep 2
    done
    
    echo -e "${RED}✗ Service failed to respond: $url${NC}"
    return 1
}

# Helper function to extract k6 metrics from JSON summary
extract_k6_metrics() {
    local json_file=$1
    local output_file=$2
    
    if [ ! -f "$json_file" ]; then
        echo "K6 summary not found: $json_file"
        return 1
    fi
    
    cat > "$output_file" << 'EOF'
K6 Test Metrics:
EOF
    
    # Check if jq is available, otherwise use basic parsing
    if command -v jq &> /dev/null; then
        {
            echo "  Overall:"
            echo "    p95 latency (ms): $(jq '.metrics.http_req_duration["p(95)"]' "$json_file")"
            echo "    max latency (ms): $(jq '.metrics.http_req_duration.max' "$json_file")"
            echo "    Total requests: $(jq '.metrics.http_reqs.count' "$json_file")"
            echo "    Check success rate: $(jq '(.metrics.checks.value * 100) | round / 100 | tostring + "%"' "$json_file")"
            echo "    Threshold: p95<500 = $(jq '.metrics.http_req_duration.thresholds["p(95)<500"]' "$json_file"), p99<1000 = $(jq '.metrics.http_req_duration.thresholds["p(99)<1000"]' "$json_file")"
        } >> "$output_file"
    else
        echo "  (jq not available; see raw k6_summary.json for detailed metrics)" >> "$output_file"
    fi
}

# ============================================================================
# MONOLITH
# ============================================================================

echo -e "${YELLOW}--- MONOLITH ---${NC}"
MONOLITH_RUN_DIR="${RUN_DIR}/monolith"
mkdir -p "$MONOLITH_RUN_DIR"

cd "$MONOLITH_DIR"

echo "Stopping existing containers..."
docker compose down --remove-orphans 2>/dev/null || true

echo "Removing old volumes..."
docker volume rm sasps-monolith_postgres_data 2>/dev/null || true

echo "Starting monolith stack..."
docker compose up -d --build

echo "Waiting for services to be ready..."
wait_for_service "http://localhost:8080/actuator/health"

echo "Running k6 $SCENARIO scenario..."
K6_SUMMARY_MONOLITH="${MONOLITH_RUN_DIR}/k6_summary.json"
k6 run --env BASE_URL=http://localhost:8080/api --env SCENARIO="$SCENARIO" \
    --summary-export="$K6_SUMMARY_MONOLITH" \
    ./load_test_k6.js 2>&1 | tee "$MONOLITH_RUN_DIR/k6_stdout.txt"

echo "Capturing service status..."
docker compose ps > "$MONOLITH_RUN_DIR/compose_ps.txt"

echo "Extracting k6 metrics..."
extract_k6_metrics "$K6_SUMMARY_MONOLITH" "$MONOLITH_RUN_DIR/k6_metrics.txt"

echo "Stopping monolith stack..."
docker compose down --remove-orphans

# Short pause to ensure cleanup
sleep 5

# ============================================================================
# MICROSERVICES
# ============================================================================

echo ""
echo -e "${YELLOW}--- MICROSERVICES ---${NC}"
MICROSERVICES_RUN_DIR="${RUN_DIR}/microservices"
mkdir -p "$MICROSERVICES_RUN_DIR"

cd "$MICROSERVICES_DIR"

echo "Stopping existing containers..."
docker compose -f docker-compose-perf.yml down --remove-orphans 2>/dev/null || true

echo "Removing old volumes..."
docker volume rm sasps-microservices_db-cluster-data 2>/dev/null || true

echo "Starting microservices stack (perf variant, no auth-service)..."
docker compose -f docker-compose-perf.yml up -d --build

echo "Waiting for services to be ready..."
wait_for_service "http://localhost:8081/actuator/health" || true
wait_for_service "http://localhost:8082/actuator/health" || true
wait_for_service "http://localhost:8083/actuator/health" || true
wait_for_service "http://localhost:8084/actuator/health" || true

echo "Running k6 $SCENARIO scenario..."
K6_SUMMARY_MICROSERVICES="${MICROSERVICES_RUN_DIR}/k6_summary.json"
k6 run --env SCENARIO="$SCENARIO" \
    --env ROOM_BASE=http://localhost:8082/api \
    --env BOOKING_BASE=http://localhost:8083/api \
    --summary-export="$K6_SUMMARY_MICROSERVICES" \
    ./load_test_k6.js 2>&1 | tee "$MICROSERVICES_RUN_DIR/k6_stdout.txt"

echo "Capturing service status..."
docker compose -f docker-compose-perf.yml ps > "$MICROSERVICES_RUN_DIR/compose_ps.txt"

echo "Extracting k6 metrics..."
extract_k6_metrics "$K6_SUMMARY_MICROSERVICES" "$MICROSERVICES_RUN_DIR/k6_metrics.txt"

echo "Stopping microservices stack..."
docker compose -f docker-compose-perf.yml down --remove-orphans

# ============================================================================
# GENERATE COMPARISON SUMMARY
# ============================================================================

echo ""
echo -e "${GREEN}=== Generating Comparison Summary ===${NC}"

SUMMARY_FILE="${RUN_DIR}/SUMMARY.md"

cat > "$SUMMARY_FILE" << EOF
# Performance Comparison: Monolith vs Microservices

**Test Date:** $(date)
**Scenario:** $SCENARIO

## Results

### Monolith

EOF

if [ -f "$MONOLITH_RUN_DIR/k6_metrics.txt" ]; then
    cat "$MONOLITH_RUN_DIR/k6_metrics.txt" >> "$SUMMARY_FILE"
else
    echo "  (k6 metrics not available)" >> "$SUMMARY_FILE"
fi

cat >> "$SUMMARY_FILE" << EOF

### Microservices

EOF

if [ -f "$MICROSERVICES_RUN_DIR/k6_metrics.txt" ]; then
    cat "$MICROSERVICES_RUN_DIR/k6_metrics.txt" >> "$SUMMARY_FILE"
else
    echo "  (k6 metrics not available)" >> "$SUMMARY_FILE"
fi

cat >> "$SUMMARY_FILE" << EOF

EOF

echo -e "${GREEN}✓ Summary written to: $SUMMARY_FILE${NC}"

# Print summary to console
echo ""
cat "$SUMMARY_FILE"

echo ""
echo -e "${GREEN}=== Test Run Complete ===${NC}"
echo "Results directory: $RUN_DIR"
