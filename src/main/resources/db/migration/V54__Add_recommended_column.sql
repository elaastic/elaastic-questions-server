-- Add the is_recommended_by_teacher column
ALTER TABLE `choice_interaction_response`
    ADD COLUMN `is_recommended_by_teacher` bool NOT NULL DEFAULT FALSE;