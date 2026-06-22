-- Fonction
CREATE OR REPLACE FUNCTION update_profile_review_stats()
    RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        UPDATE profiles
        SET review_count = (SELECT COUNT(*) FROM reviews WHERE reviewee_id = NEW.reviewee_id),
            avg_rating = (SELECT ROUND(AVG(rating), 1) FROM reviews WHERE reviewee_id = NEW.reviewee_id)
        WHERE id = NEW.reviewee_id;
    END IF;

    IF (TG_OP = 'DELETE' OR (TG_OP = 'UPDATE' AND OLD.reviewee_id != NEW.reviewee_id)) THEN
        UPDATE profiles
        SET review_count = (SELECT COUNT(*) FROM reviews WHERE reviewee_id = OLD.reviewee_id),
            avg_rating = (SELECT ROUND(AVG(rating), 1) FROM reviews WHERE reviewee_id = OLD.reviewee_id)
        WHERE id = OLD.reviewee_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- Fonction
CREATE OR REPLACE FUNCTION update_profile_completed_trips()
    RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.state IS DISTINCT FROM OLD.state) THEN
        IF (NEW.state = 'COMPLETED' OR OLD.state = 'COMPLETED') THEN
            UPDATE profiles
            SET completed_trips = (
                SELECT COUNT(*) FROM trips
                WHERE user_id = NEW.user_id AND state = 'COMPLETED' AND deleted = FALSE
            )
            WHERE id = NEW.user_id;
        END IF;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- Fonction
CREATE OR REPLACE FUNCTION update_profile_delivered_parcels()
    RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.state IS DISTINCT FROM OLD.state) THEN
        IF (NEW.state = 'DELIVERED' OR OLD.state = 'DELIVERED') THEN
            UPDATE profiles
            SET delivered_parcels = (
                SELECT COUNT(*) FROM parcels
                WHERE user_id = NEW.user_id AND state = 'DELIVERED' AND deleted = FALSE
            )
            WHERE id = NEW.user_id;
        END IF;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE TRIGGER trigger_update_review_stats
    AFTER INSERT OR UPDATE OR DELETE ON reviews
    FOR EACH ROW EXECUTE FUNCTION update_profile_review_stats();

CREATE TRIGGER trigger_update_completed_trips
    AFTER UPDATE OF state ON trips
    FOR EACH ROW EXECUTE FUNCTION update_profile_completed_trips();

CREATE TRIGGER trigger_update_delivered_parcels
    AFTER UPDATE OF state ON parcels
    FOR EACH ROW EXECUTE FUNCTION update_profile_delivered_parcels();