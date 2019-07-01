# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Hôte: localhost (MySQL 5.6.15)
# Base de données: elaastic-qt-test
# Temps de génération: 2019-02-05 08:57:08 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Affichage de la table activation_key
# ------------------------------------------------------------

CREATE TABLE `activation_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `activation_email_sent` tinyint(1) NOT NULL,
  `activation_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date_created` datetime NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `subscription_source` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'elaastic',
  PRIMARY KEY (`id`),
  KEY `FKAFE4D3B62E7CCBC2` (`user_id`),
  CONSTRAINT `FKAFE4D3B62E7CCBC2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Affichage de la table assignment
# ------------------------------------------------------------

CREATE TABLE `assignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `title` text NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `last_updated` datetime NOT NULL,
  `global_id` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_assignment_owner_id` (`owner_id`),
  CONSTRAINT `fk_assignment_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table attachement
# ------------------------------------------------------------

CREATE TABLE `attachement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) DEFAULT NULL,
  `path` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `original_name` varchar(255) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `dimension_height` bigint(20) DEFAULT NULL,
  `dimension_width` bigint(20) DEFAULT NULL,
  `type_mime` varchar(255) DEFAULT NULL,
  `note_id` bigint(20) DEFAULT NULL,
  `context_id` bigint(20) DEFAULT NULL,
  `to_delete` bit(1) DEFAULT NULL,
  `statement_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_note_id` (`note_id`),
  KEY `fk_context_id` (`context_id`),
  KEY `idx_attachement_statement_id` (`statement_id`),
  CONSTRAINT `fk_attachement_statement` FOREIGN KEY (`statement_id`) REFERENCES `statement` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_context_id` FOREIGN KEY (`context_id`) REFERENCES `context` (`id`),
  CONSTRAINT `fk_note_id` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table choice_interaction_response
# ------------------------------------------------------------

CREATE TABLE `choice_interaction_response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `learner_id` bigint(20) NOT NULL,
  `interaction_id` bigint(20) NOT NULL,
  `choice_list_specification` varchar(100) DEFAULT NULL,
  `explanation` text,
  `confidence_degree` int(11) DEFAULT NULL,
  `score` float DEFAULT NULL,
  `attempt` int(11) NOT NULL DEFAULT '1',
  `mean_grade` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `learner_interaction_attempt_unique` (`learner_id`,`interaction_id`,`attempt`),
  KEY `idx_choice_interaction_response_learner_id` (`learner_id`),
  KEY `idx_choice_interaction_response_interaction_id` (`interaction_id`),
  CONSTRAINT `fk_choice_interaction_response_interaction` FOREIGN KEY (`interaction_id`) REFERENCES `interaction` (`id`),
  CONSTRAINT `fk_choice_interaction_response_learner` FOREIGN KEY (`learner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table fake_explanation
# ------------------------------------------------------------

CREATE TABLE `fake_explanation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `author_id` bigint(20) NOT NULL,
  `statement_id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `corresponding_item` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fake_explanation_author_id` (`author_id`),
  KEY `idx_fake_explanation_statement_id` (`statement_id`),
  CONSTRAINT `fk_fake_explanation_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_fake_explanation_statement` FOREIGN KEY (`statement_id`) REFERENCES `statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table interaction
# ------------------------------------------------------------

CREATE TABLE `interaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `rank` int(11) NOT NULL,
  `specification` text NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `sequence_id` bigint(20) NOT NULL,
  `interaction_type` varchar(512) NOT NULL,
  `state` varchar(32) NOT NULL DEFAULT 'beforeStart',
  `results` text,
  `explanation_recommendation_mapping` text,
  PRIMARY KEY (`id`),
  KEY `idx_interaction_owner_id` (`owner_id`),
  KEY `idx_interaction_sequence_id` (`sequence_id`),
  CONSTRAINT `fk_interaction_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_interaction_sequence` FOREIGN KEY (`sequence_id`) REFERENCES `sequence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table learner_assignment
# ------------------------------------------------------------

CREATE TABLE `learner_assignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `learner_id` bigint(20) NOT NULL,
  `assignment_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `learner_assignment_unique` (`learner_id`,`assignment_id`),
  KEY `idx_learner_assignment_learner_id` (`learner_id`),
  KEY `idx_learner_assignment_assignment_id` (`assignment_id`),
  CONSTRAINT `fk_learner_assignment_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`),
  CONSTRAINT `fk_learner_assignment_learner` FOREIGN KEY (`learner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table learner_sequence
# ------------------------------------------------------------

CREATE TABLE `learner_sequence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `learner_id` bigint(20) NOT NULL,
  `sequence_id` bigint(20) NOT NULL,
  `active_interaction_id` bigint(20) DEFAULT NULL,
  `state` varchar(32) NOT NULL DEFAULT 'show',
  PRIMARY KEY (`id`),
  UNIQUE KEY `learner_sequence_unique` (`learner_id`,`sequence_id`),
  KEY `idx_learner_sequence_learner_id` (`learner_id`),
  KEY `idx_learner_sequence_sequence_id` (`sequence_id`),
  KEY `idx_learner_sequence_active_interaction_id` (`active_interaction_id`),
  CONSTRAINT `fk_learner_sequence_active_interaction` FOREIGN KEY (`active_interaction_id`) REFERENCES `interaction` (`id`),
  CONSTRAINT `fk_learner_sequence_learner` FOREIGN KEY (`learner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_learner_sequence_sequence` FOREIGN KEY (`sequence_id`) REFERENCES `sequence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lms_assignment
# ------------------------------------------------------------

CREATE TABLE `lms_assignment` (
  `assignment_id` bigint(20) NOT NULL,
  `lti_course_id` varchar(255) NOT NULL,
  `lti_activity_id` varchar(255) NOT NULL,
  `lti_consumer_key` varchar(255) NOT NULL,
  `source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`assignment_id`,`lti_course_id`,`lti_activity_id`,`lti_consumer_key`),
  KEY `lms_assignment_fk2` (`lti_course_id`),
  KEY `lms_assignment_fk3` (`lti_activity_id`),
  KEY `lms_assignment_fk4` (`lti_consumer_key`),
  CONSTRAINT `lms_assignment_fk1` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`),
  CONSTRAINT `lms_assignment_fk2` FOREIGN KEY (`lti_course_id`) REFERENCES `lti_context` (`context_id`),
  CONSTRAINT `lms_assignment_fk3` FOREIGN KEY (`lti_activity_id`) REFERENCES `lti_context` (`lti_context_id`),
  CONSTRAINT `lms_assignment_fk4` FOREIGN KEY (`lti_consumer_key`) REFERENCES `lti_context` (`consumer_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lms_user
# ------------------------------------------------------------

CREATE TABLE `lms_user` (
  `tsaap_user_id` bigint(20) NOT NULL,
  `lti_consumer_key` varchar(255) NOT NULL,
  `lti_user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`tsaap_user_id`,`lti_consumer_key`,`lti_user_id`),
  KEY `lms_user_fk2` (`lti_consumer_key`),
  KEY `lms_user_fk3` (`lti_user_id`),
  CONSTRAINT `lms_user_fk1` FOREIGN KEY (`tsaap_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `lms_user_fk2` FOREIGN KEY (`lti_consumer_key`) REFERENCES `lti_consumer` (`consumer_key`),
  CONSTRAINT `lms_user_fk3` FOREIGN KEY (`lti_user_id`) REFERENCES `lti_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lti_consumer
# ------------------------------------------------------------

CREATE TABLE `lti_consumer` (
  `consumer_key` varchar(255) NOT NULL,
  `name` varchar(45) NOT NULL,
  `secret` varchar(32) NOT NULL,
  `lti_version` varchar(12) DEFAULT NULL,
  `consumer_name` varchar(255) DEFAULT NULL,
  `consumer_version` varchar(255) DEFAULT NULL,
  `consumer_guid` varchar(255) DEFAULT NULL,
  `css_path` varchar(255) DEFAULT NULL,
  `protected` tinyint(1) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `enable_from` datetime DEFAULT NULL,
  `enable_until` datetime DEFAULT NULL,
  `last_access` date DEFAULT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  PRIMARY KEY (`consumer_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lti_context
# ------------------------------------------------------------

CREATE TABLE `lti_context` (
  `consumer_key` varchar(255) NOT NULL,
  `context_id` varchar(255) NOT NULL,
  `lti_context_id` varchar(255) DEFAULT NULL,
  `lti_resource_id` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `settings` text,
  `primary_consumer_key` varchar(255) DEFAULT NULL,
  `primary_context_id` varchar(255) DEFAULT NULL,
  `share_approved` tinyint(1) DEFAULT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  PRIMARY KEY (`consumer_key`,`context_id`),
  KEY `context_id_index` (`context_id`),
  KEY `lti_context_id_index` (`lti_context_id`),
  KEY `lti_context_consumer_key_index` (`consumer_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lti_nonce
# ------------------------------------------------------------

CREATE TABLE `lti_nonce` (
  `consumer_key` varchar(255) NOT NULL,
  `value` varchar(32) NOT NULL,
  `expires` datetime NOT NULL,
  PRIMARY KEY (`consumer_key`,`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lti_share_key
# ------------------------------------------------------------

CREATE TABLE `lti_share_key` (
  `share_key_id` varchar(32) NOT NULL,
  `primary_consumer_key` varchar(255) NOT NULL,
  `primary_context_id` varchar(255) NOT NULL,
  `auto_approve` tinyint(1) NOT NULL,
  `expires` datetime NOT NULL,
  PRIMARY KEY (`share_key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table lti_user
# ------------------------------------------------------------

CREATE TABLE `lti_user` (
  `consumer_key` varchar(255) NOT NULL,
  `context_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `lti_result_sourcedid` varchar(255) NOT NULL,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`consumer_key`,`context_id`,`user_id`),
  KEY `user_id_index` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table password_reset_key
# ------------------------------------------------------------

CREATE TABLE `password_reset_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `password_reset_key` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `password_reset_email_sent` tinyint(1) DEFAULT NULL,
  `date_created` datetime DEFAULT NULL,
  `subscription_source` varchar(50) NOT NULL DEFAULT 'elaastic',
  PRIMARY KEY (`id`),
  KEY `fk_password_reset_key_user_id` (`user_id`),
  CONSTRAINT `fk_password_reset_key_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table peer_grading
# ------------------------------------------------------------

CREATE TABLE `peer_grading` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `grade` float DEFAULT NULL,
  `annotation` text,
  `grader_id` bigint(20) NOT NULL,
  `response_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_peer_grading_grader_id` (`grader_id`),
  KEY `idx_peer_grading_grader_response_id` (`response_id`),
  CONSTRAINT `fk_peer_grading_grader_id` FOREIGN KEY (`grader_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_peer_grading_response_id` FOREIGN KEY (`response_id`) REFERENCES `choice_interaction_response` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table resource
# ------------------------------------------------------------

CREATE TABLE `resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description_as_note` varchar(280) COLLATE utf8_unicode_ci DEFAULT NULL,
  `metadata` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Affichage de la table resource_follower
# ------------------------------------------------------------

CREATE TABLE `resource_follower` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `follower_id` bigint(20) NOT NULL,
  `resource_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB2B73D2F96BFD28F` (`follower_id`),
  KEY `FKB2B73D2F68909C2A` (`resource_id`),
  CONSTRAINT `FKB2B73D2F68909C2A` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`id`),
  CONSTRAINT `FKB2B73D2F96BFD28F` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Affichage de la table role
# ------------------------------------------------------------

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authority` varchar(12) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;

INSERT INTO `role` (`id`, `authority`)
VALUES
	(1,'ADMIN_ROLE'),
	(2,'STUDENT_ROLE'),
	(3,'TEACHER_ROLE');

/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


# Affichage de la table sequence
# ------------------------------------------------------------

CREATE TABLE `sequence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `rank` int(11) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `assignment_id` bigint(20) NOT NULL,
  `statement_id` bigint(20) NOT NULL,
  `active_interaction_id` bigint(20) DEFAULT NULL,
  `state` varchar(32) NOT NULL DEFAULT 'beforeStart',
  `execution_context` varchar(32) NOT NULL DEFAULT 'FaceToFace',
  `results_are_published` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_sequence_owner_id` (`owner_id`),
  KEY `idx_sequence_assignment_id` (`assignment_id`),
  KEY `idx_sequence_statement_id` (`statement_id`),
  KEY `idx_sequence_active_interaction_id` (`active_interaction_id`),
  CONSTRAINT `fk_sequence_assignment` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`),
  CONSTRAINT `fk_sequence_interaction` FOREIGN KEY (`active_interaction_id`) REFERENCES `interaction` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_sequence_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_sequence_statement` FOREIGN KEY (`statement_id`) REFERENCES `statement` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table settings
# ------------------------------------------------------------

CREATE TABLE `settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `daily_notifications` bit(1) DEFAULT NULL,
  `mention_notifications` bit(1) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_id` (`user_id`),
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;

INSERT INTO `settings` (`id`, `version`, `user_id`, `daily_notifications`, `mention_notifications`, `language`)
VALUES
	(1,0,211,b'1',b'1','en'),
	(4633,0,4938,b'1',b'1','fr'),
	(4634,0,4939,b'1',b'1','fr'),
	(4635,0,4940,b'1',b'1','fr'),
	(4636,0,4941,b'1',b'1','fr'),
	(4637,0,4942,b'1',b'1','fr'),
	(4638,0,4943,b'1',b'1','fr'),
	(4639,0,4944,b'1',b'1','fr'),
	(4640,0,4945,b'1',b'1','fr'),
	(4641,0,4946,b'1',b'1','fr');

/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;


# Affichage de la table statement
# ------------------------------------------------------------

CREATE TABLE `statement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `last_updated` datetime NOT NULL,
  `title` varchar(512) NOT NULL,
  `content` text NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `question_type` varchar(255) DEFAULT NULL,
  `choice_specification` text,
  `parent_statement_id` bigint(20) DEFAULT NULL,
  `expected_explanation` text,
  PRIMARY KEY (`id`),
  KEY `idx_statement_owner_id` (`owner_id`),
  KEY `fk_parent_statement` (`parent_statement_id`),
  CONSTRAINT `fk_parent_statement` FOREIGN KEY (`parent_statement_id`) REFERENCES `statement` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_statement_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Affichage de la table unsubscribe_key
# ------------------------------------------------------------

CREATE TABLE `unsubscribe_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `unsubscribe_key` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_unsubscribe_key_user_id` (`user_id`),
  CONSTRAINT `fk_unsubscribe_key_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `unsubscribe_key` WRITE;
/*!40000 ALTER TABLE `unsubscribe_key` DISABLE KEYS */;

INSERT INTO `unsubscribe_key` (`id`, `version`, `unsubscribe_key`, `user_id`)
VALUES
	(1,0,'eb931dd1-cb46-4789-a7b6-1e24e0c1fccc',211),
	(122,0,'755ebfb7-4a9f-4e0f-8d34-58de4d638974',4938),
	(123,0,'6b0f6367-0f52-4d85-9403-152a5dfa348d',4939),
	(124,0,'de00c86d-ed0b-4b62-b899-c4220f1354bc',4940),
	(125,0,'775e6038-9527-4078-b3bc-36e3680ab029',4941),
	(126,0,'dc1fa2c5-e6c0-4e21-8836-c5f8449325e3',4942),
	(127,0,'3abeea15-b8a0-4465-b0fc-156771e6e77b',4943),
	(128,0,'4bdcd38b-8b6e-4b6d-89fb-d2b43fe4909d',4944),
	(129,0,'836f830a-98bf-4481-bec0-653d2e4614e3',4945),
	(130,0,'94d210f8-4652-47b8-ae01-597b9749199f',4946);

/*!40000 ALTER TABLE `unsubscribe_key` ENABLE KEYS */;
UNLOCK TABLES;


# Affichage de la table user
# ------------------------------------------------------------

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_expired` tinyint(1) NOT NULL,
  `account_locked` tinyint(1) NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `first_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `last_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `normalized_username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `password_expired` tinyint(1) NOT NULL,
  `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `version` bigint(20) NOT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `can_be_user_owner` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `user_owner_id_index` (`owner_id`),
  CONSTRAINT `user_owner_fk` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `account_expired`, `account_locked`, `email`, `enabled`, `first_name`, `last_name`, `normalized_username`, `password`, `password_expired`, `username`, `version`, `owner_id`, `can_be_user_owner`)
VALUES
	(211,0,0,'admin@tsaap.org',1,'Admin','Tsaap','admin','7110eda4d09e062aa5e4a390b0a572ac0d2c0220',0,'admin',0,NULL,b'0'),
	(4938,0,0,'John_Doe___1@fakeuser.com',1,'John','Doe','john_doe___1','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___1',0,NULL,b'0'),
	(4939,0,0,'John_Doe___2@fakeuser.com',1,'John','Doe','john_doe___2','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___2',0,NULL,b'0'),
	(4940,0,0,'John_Doe___3@fakeuser.com',1,'John','Doe','john_doe___3','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___3',0,NULL,b'0'),
	(4941,0,0,'John_Doe___4@fakeuser.com',1,'John','Doe','john_doe___4','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___4',0,NULL,b'0'),
	(4942,0,0,'John_Doe___5@fakeuser.com',1,'John','Doe','john_doe___5','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___5',0,NULL,b'0'),
	(4943,0,0,'John_Doe___6@fakeuser.com',1,'John','Doe','john_doe___6','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___6',0,NULL,b'0'),
	(4944,0,0,'John_Doe___7@fakeuser.com',1,'John','Doe','john_doe___7','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___7',0,NULL,b'0'),
	(4945,0,0,'John_Doe___8@fakeuser.com',1,'John','Doe','john_doe___8','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___8',0,NULL,b'0'),
	(4946,0,0,'John_Doe___9@fakeuser.com',1,'John','Doe','john_doe___9','06c129f13e5ec40f6d08f57504d30cf416e6cad9',0,'John_Doe___9',0,NULL,b'0');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Affichage de la table user_role
# ------------------------------------------------------------

CREATE TABLE `user_role` (
  `role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FK143BF46A895207E2` (`role_id`),
  KEY `FK143BF46A2E7CCBC2` (`user_id`),
  CONSTRAINT `FK143BF46A2E7CCBC2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK143BF46A895207E2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;

INSERT INTO `user_role` (`role_id`, `user_id`)
VALUES
	(1,211),
	(2,4938),
	(2,4939),
	(2,4940),
	(2,4941),
	(2,4942),
	(2,4943),
	(2,4944),
	(2,4945),
	(2,4946);

/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
