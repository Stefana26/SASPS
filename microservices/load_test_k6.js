import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

const USER_BASE = __ENV.USER_BASE || 'http://localhost:8081';
const ROOM_BASE = __ENV.ROOM_BASE || 'http://localhost:8082/api';
const BOOKING_BASE = __ENV.BOOKING_BASE || 'http://localhost:8083/api';
const PAYMENT_BASE = __ENV.PAYMENT_BASE || 'http://localhost:8084/api';

const successRate = new Rate('success_rate');
const responseTime = new Trend('response_time');
const requestCount = new Counter('request_count');

const scenarios = {
    smoke: {
        executor: 'constant-vus',
        vus: 5,
        duration: '30s',
    },
    load: {
        executor: 'constant-vus',
        vus: 50,
        duration: '2m',
    },
};

const selectedScenario = __ENV.SCENARIO || 'smoke';

export const options = {
    scenarios: {
        main: scenarios[selectedScenario],
    },
    thresholds: {
        http_req_duration: ['p(95)<500', 'p(99)<1000'],
        http_req_failed: ['rate<0.01'],
        success_rate: ['rate>0.99'],
    },
};

function getRooms() {
    const response = http.get(`${ROOM_BASE}/rooms/hotel/1`);
    const result = check(response, {
        'status is 200': (r) => r.status === 200,
    });
    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);
}

function getHotels() {
    const response = http.get(`${ROOM_BASE}/hotels`);
    const result = check(response, {
        'status is 200': (r) => r.status === 200,
    });
    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);
}

function getBookings() {
    const response = http.get(`${BOOKING_BASE}/bookings`);
    const result = check(response, {
        'status is 200': (r) => r.status === 200,
    });
    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);
}

export default function () {
    const rand = Math.random();
    if (rand < 0.4) {
        getRooms();
    } else if (rand < 0.8) {
        getHotels();
    } else {
        getBookings();
    }
    sleep(Math.random() * 2 + 0.5);
}
