ALTER TABLE `choice_interaction_response`
    ADD COLUMN `is_favourite` bool NOT NULL DEFAULT FALSE;

WITH RankedResponses AS (
    SELECT
        id,
        interaction_id,
        mean_grade,
        ROW_NUMBER() OVER (PARTITION BY interaction_id ORDER BY mean_grade DESC) AS row_num
    FROM choice_interaction_response
)

UPDATE choice_interaction_response c
    JOIN RankedResponses r ON c.id = r.id
    SET c.is_favourite = (r.row_num <= 3);