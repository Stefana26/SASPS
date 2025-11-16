DELETE FROM rooms WHERE hotel_id IN (
    SELECT id FROM hotels WHERE name IN (
        'Grand Hotel Bucharest',
        'Central Hotel Cluj',
        'Seaside Resort Constanta'
    )
);

DELETE FROM hotels WHERE name IN (
    'Grand Hotel Bucharest',
    'Central Hotel Cluj',
    'Seaside Resort Constanta'
);

DELETE FROM payments WHERE booking_id IN (
    SELECT id FROM bookings WHERE confirmation_number IN (
        'BK-2024-001', 'BK-2024-002', 'BK-2024-003', 'BK-2024-004', 
        'BK-2024-005', 'BK-2024-006', 'BK-2024-007', 'BK-2024-008'
    )
);

DELETE FROM bookings WHERE confirmation_number IN (
    'BK-2024-001', 'BK-2024-002', 'BK-2024-003', 'BK-2024-004', 
    'BK-2024-005', 'BK-2024-006', 'BK-2024-007', 'BK-2024-008'
);

DELETE FROM users WHERE username IN ('admin', 'andrei.ciobanu', 'maria.pop', 'alex.ionescu');
