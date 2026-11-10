CREATE OR REPLACE FUNCTION delete_location_with_replacement(p_location_id INTEGER, p_replacement_id INTEGER)
RETURNS INTEGER LANGUAGE plpgsql AS $$
DECLARE v_count INTEGER;
BEGIN
    PERFORM 1 FROM locations WHERE id = p_location_id FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Локация % не найдена', p_location_id;
    END IF;
    IF p_replacement_id = p_location_id THEN
        RAISE EXCEPTION 'Локация не может заменять сама себя';
    END IF;
    IF p_replacement_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM locations WHERE id = p_replacement_id) THEN
        RAISE EXCEPTION 'Локация-замена % не найдена', p_replacement_id;
    END IF;
    SELECT count(*) INTO v_count FROM routes
    WHERE from_location_id = p_location_id OR to_location_id = p_location_id;
    IF v_count > 0 AND p_replacement_id IS NULL THEN
        RAISE EXCEPTION 'На локацию ссылаются маршруты (%), выберите локацию-замену', v_count;
    END IF;
    UPDATE routes SET from_location_id = p_replacement_id WHERE from_location_id = p_location_id;
    UPDATE routes SET to_location_id = p_replacement_id WHERE to_location_id = p_location_id;
    DELETE FROM locations WHERE id = p_location_id;
    RETURN v_count;
END;
$$;
