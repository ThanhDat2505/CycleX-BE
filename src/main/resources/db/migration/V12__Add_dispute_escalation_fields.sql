-- Add escalation-specific fields to disputes table
-- These replace the hacky "[Escalated] ..." prefix in resolution_note
ALTER TABLE disputes
    ADD COLUMN IF NOT EXISTS escalation_note        TEXT,
    ADD COLUMN IF NOT EXISTS escalation_suggestion  VARCHAR(50),
    ADD COLUMN IF NOT EXISTS escalated_by_id        INT REFERENCES users(user_id),
    ADD COLUMN IF NOT EXISTS escalated_at           TIMESTAMP;
