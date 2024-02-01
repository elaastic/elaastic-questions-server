-- DROP IF EXISTS the columns
ALTER TABLE `choice_interaction_response`
    DROP COLUMN `is_recommended_by_system`,
    DROP COLUMN `is_recommended_by_teacher`;

-- Add the is_recommended_by_teacher column
ALTER TABLE `choice_interaction_response`
    ADD COLUMN `is_recommended_by_teacher` bool NOT NULL DEFAULT FALSE;