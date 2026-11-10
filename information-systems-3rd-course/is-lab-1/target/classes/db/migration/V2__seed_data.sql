INSERT INTO locations (id, x, y, z, name) OVERRIDING SYSTEM VALUE VALUES
    (1, 10, 20.5, 2.0, 'Главный корпус'),
    (2, 34, 15.2, 1.0, 'Общежитие №1'),
    (3, 57, 41.8, 4.5, 'Спортивный комплекс'),
    (4, 22, 61.1, 3.0, 'Научная библиотека'),
    (5, 78, 29.4, 2.5, 'Медицинский центр')
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('locations', 'id'), COALESCE((SELECT max(id) FROM locations), 1));

INSERT INTO routes (id, name, coordinate_x, coordinate_y, creation_date, from_location_id, to_location_id, distance, rating) OVERRIDING SYSTEM VALUE VALUES
    (101, 'Главный корпус — Общежитие №1', 59.94, 30, '2026-09-12 14:25', 1, 2, 3.2, 8),
    (102, 'Главный корпус — Спортивный комплекс', 59.95, 31, '2026-08-05 09:10', 1, 3, 2.1, 5),
    (103, 'Общежитие №1 — Научная библиотека', 59.96, 32, '2026-07-18 16:40', 2, 4, 4.7, 7),
    (104, 'Главный корпус — Медицинский центр', 59.97, 33, '2026-06-28 11:05', 1, 5, 1.6, 9),
    (105, 'Общежитие №1 — Главный корпус', 59.98, 34, '2026-05-10 13:20', 2, 1, 2.9, 6)
ON CONFLICT (id) DO NOTHING;
SELECT setval(pg_get_serial_sequence('routes', 'id'), COALESCE((SELECT max(id) FROM routes), 1));
