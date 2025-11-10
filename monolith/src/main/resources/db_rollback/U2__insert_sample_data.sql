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
