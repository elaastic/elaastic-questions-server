-- Add the is_favourite column
ALTER TABLE `choice_interaction_response`
    ADD COLUMN `is_favourite` bool NOT NULL DEFAULT FALSE;

-- Get the CORRECT (score = 100) responses from a STUDENT
WITH RankedResponses AS (
    SELECT
        id,
        interaction_id,
        mean_grade,
        learner_id,
        ROW_NUMBER() OVER (PARTITION BY interaction_id ORDER BY mean_grade DESC) AS row_num
    FROM choice_interaction_response
    WHERE learner_id IN (
        SELECT u.id
        FROM user u
                 JOIN user_role ur ON u.id = ur.user_id
                 JOIN role ro ON ur.role_id = ro.id
        WHERE ro.authority = 'STUDENT_ROLE'
    )
      AND score = 100
)

-- Update the TOP 3 responses to be favourites
UPDATE choice_interaction_response c
    JOIN RankedResponses r ON c.id = r.id
    SET is_favourite = (r.row_num <= 3);