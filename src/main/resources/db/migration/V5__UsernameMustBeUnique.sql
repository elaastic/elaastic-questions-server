ALTER TABLE `user`
    DROP COLUMN `normalized_username`;

ALTER TABLE `user`
ADD COLUMN normalized_username varchar(255) GENERATED ALWAYS AS (lower(username)) STORED UNIQUE;