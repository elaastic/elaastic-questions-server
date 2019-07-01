INSERT INTO `user` (`id`, `account_expired`, `account_locked`, `email`, `enabled`, `first_name`, `last_name`, `normalized_username`, `password`, `password_expired`, `username`, `version`, `owner_id`, `can_be_user_owner`)
VALUES (359,0,0,'admin3@tsaap.org',1,'Admin3','Tsaap3','admin3','7110eda4d09e062aa5e4a390b0a572ac0d2c0220',0,'admin3',0,NULL,b'1');

INSERT INTO `assignment` (`id`, `version`, `date_created`, `title`,`owner_id`, `last_updated`, `global_id`)
VALUES (382,11,'2017-10-09 17:08:59','EMINL1H1 : Développement collaboratif, qualité: Quizz Git',359,'2017-10-12 07:51:36','c71b94b6-ad03-11e7-93a4-00163e3774aa');

INSERT INTO `statement` (`id`,`version`,`date_created`,`last_updated`,`title`,`content`,`owner_id`,`question_type`,`choice_specification`,`parent_statement_id`,`expected_explanation`)
VALUES (618,0,'2017-10-09 17:23:58','2017-10-09 17:23:58','Git - Concepts clés','<p>Cochez les assertions vraies :</p><ol>  <li>Git repose sur une architecture centralisée</li>  <li>Avec Git, chaque développeur possède une copie du repository</li>  <li>Le projet Git a été initié par Linus Thorvald pour les besoins du    développement du noyau Linux</li>  <li>Git n\'est utilisable que sur les systèmes de type Unix </li>  <li>Avec Git, il n\'est plus possible d\'avoir un repository partagé de référence</li>  <li>Git est leader dans la catégorie des outils de gestion distribuée    de version de code</li></ol>',359,'MultipleChoice','{\"expectedChoiceList\":[{\"index\":2,\"score\":33.333332},{\"index\":3,\"score\":33.333332},{\"index\":6,\"score\":33.333332}],\"choiceInteractionType\":\"MULTIPLE\",\"itemCount\":6}',NULL,''),
	(619,0,'2017-10-09 17:26:16','2017-10-09 17:26:16','Git - Aire d\'embarquement','<p>L\'aire d\'embarquement est le &quot,passage obligé&quot, pour un  fichier que l\'on souhaite ajouter au repository.</p><ol>  <li>Vrai</li>  <li>Faux</li></ol>',359,'ExclusiveChoice','{\"expectedChoiceList\":[{\"index\":1,\"score\":100.0}],\"choiceInteractionType\":\"EXCLUSIVE\",\"itemCount\":2}',NULL,''),
	(620,2,'2017-10-09 17:27:08','2017-10-09 17:32:04','Git - Fichier .gitignore','<p>Le fichier .gitignore ne doit jamais être ajouté au repository Git.</p><ol>  <li>Vrai</li>  <li>Faux</li></ol>',359,'ExclusiveChoice','{\"expectedChoiceList\":[{\"index\":2,\"score\":100.0}],\"explanationChoiceList\":[],\"choiceInteractionType\":\"EXCLUSIVE\",\"itemCount\":2}',NULL,'<p>Dans la plupart des situations, toute l\'équipe doit partager la même  vision de qui doit être partagé et ignoré dans un projet. Mettre le  fichier .gitignore dans le repository est une manière de concrétiser  le partage de cette vision.</p>'),
	(621,1,'2017-10-09 17:29:43','2017-10-09 17:30:01','Git et les dossiers','<p>Git ne prend pas en compte les dossiers vides.</p><ol>  <li>Vrai</li>  <li>Faux</li></ol>',359,'ExclusiveChoice','{\"expectedChoiceList\":[{\"index\":1,\"score\":100.0}],\"explanationChoiceList\":[],\"choiceInteractionType\":\"EXCLUSIVE\",\"itemCount\":2}',NULL,'<p>Git ne traque que le contenu. Un dossier vide n\'ayant pas de contenu,  il n\'est pas traqué.</p>'),
	(622,0,'2017-10-09 17:38:16','2017-10-09 17:38:16','Git et les doublons','<p>Avec Git, il ne peut pas y avoir de fichiers en double dans le repository.</p><ol>  <li>Vrai </li>  <li>Faux</li></ol>',359,'ExclusiveChoice','{\"expectedChoiceList\":[{\"index\":1,\"score\":100.0}],\"choiceInteractionType\":\"EXCLUSIVE\",\"itemCount\":2}',NULL,'<p>Git identifie chaque fichier du repository à l\'aide de son code SHA  1. Git ne sauvegarde dans le repository que des fichiers qui n\'ont pas  le même code SHA 1, c\'est à dire que des fichiers distincts les uns  des autres. Il ne peut donc pas y avoir de doublons. </p>');

ALTER TABLE `interaction`
	DROP FOREIGN KEY `fk_interaction_sequence`;

ALTER TABLE `sequence`
  DROP FOREIGN KEY `fk_sequence_interaction`;
	
ALTER TABLE `choice_interaction_response`
  DROP FOREIGN KEY `fk_choice_interaction_response_learner`;
	
INSERT INTO `interaction` (`id`,`version`,`date_created`,`last_updated`,`rank`,`specification`,`owner_id`,`sequence_id`,`interaction_type`,`state`,`results`,`explanation_recommendation_mapping`)
VALUES (1712,2,'2017-10-12 07:51:33','2017-10-12 07:57:52',1,'{\"studentsProvideExplanation\":false,\"studentsProvideConfidenceDegree\":false}',359,611,'ResponseSubmission','afterStop','{\"1\":[0.000,29.167,87.500,58.333,0.000,8.333,87.500]}',NULL),
	(1688,33,'2017-10-10 08:54:53','2017-12-13 21:08:59',1,'{\"studentsProvideExplanation\":true,\"studentsProvideConfidenceDegree\":true}',359,615,'ResponseSubmission','beforeStart','{\"1\":[0.000,100.000,0.000],\"2\":[0.000,93.939,6.061]}',NULL);
	
INSERT INTO `sequence` (`id`,`version`,`date_created`,`last_updated`,`rank`,`owner_id`,`assignment_id`,`statement_id`,`active_interaction_id`,`state`,`execution_context`,`results_are_published`)
VALUES (611,4,'2017-10-09 17:23:58','2017-10-12 08:01:17',1,359,382,618,1712,'show','FaceToFace',1),
	(615,3,'2017-10-09 17:38:16','2017-10-10 08:54:53',5,359,382,622,1688,'show','Blended',1);

INSERT INTO `choice_interaction_response` (`id`,`version`,`date_created`,`last_updated`,`learner_id`,`interaction_id`,`choice_list_specification`,`explanation`,`confidence_degree`,`score`,`attempt`,`mean_grade`)
VALUES (7893,0,'2017-10-12 07:52:29','2017-10-12 07:52:29',359,1712,'[2,3,6]',NULL,NULL,100,1,NULL),
	(7900,0,'2017-10-12 07:53:03','2017-10-12 07:53:03',359,1712,'[1,2]',NULL,NULL,0.00000127157,2,NULL),
	(7901,0,'2017-10-12 07:53:04','2017-10-12 07:53:04',300,1712,'[2,3,6]',NULL,NULL,100,1,NULL),
	(7905,0,'2017-10-12 07:53:26','2017-10-12 07:53:26',300,1712,'[1,2,6]',NULL,NULL,33.3333,2,NULL),
	(11773,0,'2017-12-13 20:27:20','2017-12-13 20:27:20',300,1688,'[1]',NULL,2,100,1,NULL),
	(11774,0,'2017-12-13 20:28:15','2017-12-13 20:28:15',300,1688,'[1]',NULL,2,100,2,NULL),
	(11777,0,'2017-12-13 21:07:04','2017-12-13 21:07:04',359,1688,'[2]','<p>La plupart du temps il est utilie de partager le .gitignore avec les autres développeur</p>',0,0,1,NULL),
	(11778,0,'2017-12-13 21:08:59','2017-12-13 21:08:59',359,1688,'[1]',NULL,1,100,2,NULL);
	
INSERT INTO `attachement` (`id`,`version`,`path`,`name`,`original_name`,`size`,`dimension_height`,`dimension_width`,`type_mime`,`note_id`,`context_id`,`to_delete`,`statement_id`)
VALUES (1,1,'C:/testPath/test.png','test',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
	(2,1,'C:/testPath/test2.png','test2',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
	
INSERT INTO `peer_grading` (`id`,`version`,`date_created`,`last_updated`,`grade`,`annotation`,`grader_id`,`response_id`)
VALUES (15,1,'2017-12-13 20:27:20','2017-12-13 20:27:20',4.5,'peergrading1',359,7893),
	(16,1,'2017-12-13 21:07:04','2017-12-13 21:07:04',5.0,'peergrading2',359,11777);
	
INSERT INTO `learner_sequence` (`id`,`version`,`date_created`,`last_updated`,`learner_id`,`sequence_id`,`active_interaction_id`,`state`)
VALUES (1,1,'2017-10-12 07:52:29','2017-10-12 07:52:29',359,611,1712,100),
	(2,1,'2017-10-11 14:09:53','2017-10-12 06:44:51',359,615,1688,100);
	
ALTER TABLE `peer_grading`
	DROP FOREIGN KEY `fk_peer_grading_response_id`;	