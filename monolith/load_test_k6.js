import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api';

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
    stress: {
        executor: 'ramping-vus',
        startVUs: 0,
        stages: [
            { duration: '1m', target: 50 },
            { duration: '2m', target: 100 },
            { duration: '1m', target: 200 },
            { duration: '2m', target: 500 },
            { duration: '1m', target: 0 },
        ],
    },
    spike: {
        executor: 'ramping-vus',
        startVUs: 0,
        stages: [
            { duration: '30s', target: 20 },
            { duration: '10s', target: 200 },
            { duration: '30s', target: 200 },
            { duration: '10s', target: 20 },
            { duration: '30s', target: 20 },
            { duration: '10s', target: 0 },
        ],
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

function searchRooms() {
    const checkInDate = new Date();
    checkInDate.setDate(checkInDate.getDate() + 7);
    const checkOutDate = new Date(checkInDate);
    checkOutDate.setDate(checkOutDate.getDate() + 5);

    const payload = JSON.stringify({
        checkInDate: checkInDate.toISOString().split('T')[0],
        checkOutDate: checkOutDate.toISOString().split('T')[0],
        minOccupancy: 2,
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'SearchRooms' },
    };

    const response = http.post(`${BASE_URL}/rooms/search`, payload, params);

    const result = check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
        'has rooms': (r) => {
            try {
                return JSON.parse(r.body).length >= 0;
            } catch {
                return false;
            }
        },
    });

    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);

    return response;
}

function getAllHotels() {
    const params = { tags: { name: 'GetAllHotels' } };
    const response = http.get(`${BASE_URL}/hotels`, params);

    const result = check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
        'has hotels': (r) => {
            try {
                return JSON.parse(r.body).length > 0;
            } catch {
                return false;
            }
        },
    });

    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);

    return response;
}

function getHotelById(id = 1) {
    const params = { tags: { name: 'GetHotelById' } };
    const response = http.get(`${BASE_URL}/hotels/${id}`, params);

    const result = check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 200ms': (r) => r.timings.duration < 200,
        'has hotel data': (r) => {
            try {
                const data = JSON.parse(r.body);
                return data.id && data.name;
            } catch {
                return false;
            }
        },
    });

    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);

    return response;
}

function getRoomsByHotel(hotelId = 1) {
    const params = { tags: { name: 'GetRoomsByHotel' } };
    const response = http.get(`${BASE_URL}/rooms/hotel/${hotelId}`, params);

    const result = check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 300ms': (r) => r.timings.duration < 300,
    });

    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);

    return response;
}

function getAllBookings() {
    const params = { tags: { name: 'GetAllBookings' } };
    const response = http.get(`${BASE_URL}/bookings`, params);

    const result = check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    successRate.add(result);
    responseTime.add(response.timings.duration);
    requestCount.add(1);

    return response;
}

export default function () {
    const rand = Math.random();

    if (rand < 0.40) {
        searchRooms();
    } else if (rand < 0.65) {
        getAllHotels();
    } else if (rand < 0.80) {
        const randomHotelId = Math.floor(Math.random() * 3) + 1;
        getHotelById(randomHotelId);
    } else if (rand < 0.95) {
        const randomHotelId = Math.floor(Math.random() * 3) + 1;
        getRoomsByHotel(randomHotelId);
    } else {
        getAllBookings();
    }

    sleep(Math.random() * 2 + 0.5);
}

export function setup() {
    const response = http.get(`${BASE_URL}/hotels`);
    if (response.status !== 200) {
        throw new Error('Server is not responding');
    }
    return { startTime: new Date() };
}

export function teardown(data) {
    console.log('Test completed');
}
