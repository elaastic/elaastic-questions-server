ALTER TABLE `lms_assignment`
    drop primary key,
    add id bigint(20) PRIMARY KEY AUTO_INCREMENT,
    add constraint `unique_lms_assignment` unique(assignment_id, lti_course_id, lti_activity_id, lti_consumer_key);

ALTER TABLE `lms_user`
    drop primary key,
    add id bigint(20) PRIMARY KEY AUTO_INCREMENT,
    add constraint `unique_lms_user` unique(tsaap_user_id, lti_consumer_key, lti_user_id);
