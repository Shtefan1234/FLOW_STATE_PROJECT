ALTER TABLE tracks DROP CONSTRAINT fk_tracks_user;
ALTER TABLE tracks
    ADD CONSTRAINT fk_tracks_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE tasks DROP CONSTRAINT fk_tasks_track;
ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_track
        FOREIGN KEY (track_id) REFERENCES tracks (id) ON DELETE CASCADE;
