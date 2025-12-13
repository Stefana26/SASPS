#!/bin/bash

BASE_USER_URL="${USER_BASE:-http://localhost:8081}"
BASE_ROOM_URL="${ROOM_BASE:-http://localhost:8082/api}"
BASE_BOOKING_URL="${BOOKING_BASE:-http://localhost:8083/api}"
BASE_PAYMENT_URL="${PAYMENT_BASE:-http://localhost:8084/api}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

TESTS_PASSED=0
TESTS_FAILED=0

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_test() {
    echo -e "${YELLOW}TEST: $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
    ((TESTS_PASSED++))
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
    ((TESTS_FAILED++))
}

test_endpoint() {
    local method=$1
    local base=$2
    local endpoint=$3
    local data=$4
    local expected_code=$5
    local description=$6

    print_test "$description"

    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$base$endpoint" -H "Content-Type: application/json")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$base$endpoint" -H "Content-Type: application/json" -d "$data")
    fi

    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" -eq "$expected_code" ]; then
        print_success "$description (HTTP $http_code)"
        echo "$body"
        return 0
    else
        print_error "$description (Expected $expected_code, got $http_code)"
        return 1
    fi
}

print_header "Hotel Booking API (Microservices) - Test Suite"

# Users
print_header "Users API"
test_endpoint "GET" "$BASE_USER_URL" "/actuator/health" "" 200 "User service health"
test_endpoint "GET" "$BASE_USER_URL/api" "/users" "" 200 "List all users (placeholder)"
test_endpoint "GET" "$BASE_USER_URL/api" "/users/1" "" 200 "Get user by ID (placeholder)"

# Rooms / Hotels
print_header "Rooms API"
test_endpoint "GET" "$BASE_ROOM_URL" "/hotels" "" 200 "Get all hotels"
test_endpoint "GET" "$BASE_ROOM_URL" "/hotels/city/Bucharest" "" 200 "Get hotels by city"

hotel_data='{"name":"Test Hotel","description":"Test","address":"123 Test","city":"Bucharest","country":"Romania","postalCode":"010101","phoneNumber":"+40211111111","email":"test@hotel.ro","starRating":4,"amenities":"WiFi","active":true}'
hotel_response=$(test_endpoint "POST" "$BASE_ROOM_URL" "/hotels" "$hotel_data" 201 "Create hotel")
HOTEL_ID=$(echo "$hotel_response" | tail -n1 | jq -r '.id')

test_endpoint "GET" "$BASE_ROOM_URL" "/hotels/$HOTEL_ID" "" 200 "Get hotel by ID"
test_endpoint "PUT" "$BASE_ROOM_URL" "/hotels/$HOTEL_ID" '{"starRating":5}' 200 "Update hotel"
test_endpoint "POST" "$BASE_ROOM_URL" "/hotels/search" '{"searchTerm":"Test","minStarRating":4}' 200 "Search hotels"

# Rooms
print_header "Rooms API"
test_endpoint "GET" "$BASE_ROOM_URL" "/rooms/hotel/$HOTEL_ID" "" 200 "Get rooms by hotel"

room_data="{\"hotelId\":$HOTEL_ID,\"roomNumber\":\"999\",\"roomType\":\"DOUBLE\",\"pricePerNight\":250.00,\"maxOccupancy\":2,\"description\":\"Test\",\"facilities\":\"WiFi\",\"floorNumber\":9,\"status\":\"AVAILABLE\"}"
room_response=$(test_endpoint "POST" "$BASE_ROOM_URL" "/rooms" "$room_data" 201 "Create room")
ROOM_ID=$(echo "$room_response" | tail -n1 | jq -r '.id')

test_endpoint "GET" "$BASE_ROOM_URL" "/rooms/hotel/$HOTEL_ID/number/999" "" 200 "Get room by number"
test_endpoint "PUT" "$BASE_ROOM_URL" "/rooms/$ROOM_ID" '{"pricePerNight":300.00}' 200 "Update room"
test_endpoint "PATCH" "$BASE_ROOM_URL" "/rooms/$ROOM_ID/status?status=MAINTENANCE" "" 200 "Update room status"
test_endpoint "POST" "$BASE_ROOM_URL" "/rooms/search" '{"checkInDate":"2025-12-15","checkOutDate":"2025-12-20","minOccupancy":2}' 200 "Search rooms"
test_endpoint "PATCH" "$BASE_ROOM_URL" "/rooms/$ROOM_ID/status?status=AVAILABLE" "" 200 "Revert status"

# Bookings
print_header "Bookings API"
test_endpoint "GET" "$BASE_BOOKING_URL" "/bookings" "" 200 "Get all bookings"

booking_data="{\"userId\":1,\"roomId\":$ROOM_ID,\"checkInDate\":\"2025-12-15\",\"checkOutDate\":\"2025-12-20\",\"numberOfGuests\":2,\"specialRequests\":\"Test\",\"paymentMethod\":\"CREDIT_CARD\"}"
booking_response=$(test_endpoint "POST" "$BASE_BOOKING_URL" "/bookings" "$booking_data" 201 "Create booking")
BOOKING_ID=$(echo "$booking_response" | tail -n1 | jq -r '.id')
CONFIRMATION_NUMBER=$(echo "$booking_response" | tail -n1 | jq -r '.confirmationNumber')

test_endpoint "GET" "$BASE_BOOKING_URL" "/bookings/$BOOKING_ID" "" 200 "Get booking by ID"
test_endpoint "GET" "$BASE_BOOKING_URL" "/bookings/confirmation/$CONFIRMATION_NUMBER" "" 200 "Get by confirmation"
test_endpoint "PUT" "$BASE_BOOKING_URL" "/bookings/$BOOKING_ID" '{"status": "PENDING", "numberOfGuests":1}' 200 "Update booking"
test_endpoint "POST" "$BASE_BOOKING_URL" "/bookings/$BOOKING_ID/confirm" '{"paymentAmount":1250.00,"paymentMethod":"CREDIT_CARD"}' 200 "Confirm booking"
test_endpoint "POST" "$BASE_BOOKING_URL" "/bookings/$BOOKING_ID/cancel" '{"cancellationReason":"Test"}' 200 "Cancel booking"

# Payment
print_header "Payments API"
test_endpoint "GET" "$BASE_PAYMENT_URL" "/payments" "" 200 "Get all payments"

# Result
print_header "Results"
TOTAL_TESTS=$((TESTS_PASSED + TESTS_FAILED))
echo "Total: $TOTAL_TESTS"
echo -e "${GREEN}Passed: $TESTS_PASSED${NC}"
echo -e "${RED}Failed: $TESTS_FAILED${NC}"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}All tests passed!${NC}"
    exit 0
else
    echo -e "\n${RED}Some tests failed!${NC}"
    exit 1
fi
