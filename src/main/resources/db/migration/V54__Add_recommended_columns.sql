-- DROP IF EXISTS the is_favourite column
ALTER TABLE `choice_interaction_response`
    DROP COLUMN `is_recommended_by_system`,
    DROP COLUMN `is_recommended_by_teacher`;

-- Add the is_favourite column
ALTER TABLE `choice_interaction_response`
    ADD COLUMN `is_recommended_by_system` bool NOT NULL DEFAULT FALSE,
    ADD COLUMN `is_recommended_by_teacher` bool NOT NULL DEFAULT FALSE;