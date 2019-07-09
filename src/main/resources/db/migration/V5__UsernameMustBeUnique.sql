ALTER TABLE `user`
    DROP COLUMN `normalized_username`;

ALTER TABLE `user`
    ADD COLUMN normalized_username varchar(255) COLLATE utf8_unicode_ci GENERATED ALWAYS AS (lower(username)) STORED;

ALTER TABLE `user`
    ADD CONSTRAINT `unique_username` UNIQUE (`normalized_username`);