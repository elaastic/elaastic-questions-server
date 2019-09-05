drop table lti_nonce ;
drop table lti_share_key;

ALTER TABLE lms_user
    DROP FOREIGN KEY lms_user_fk3;

drop table lti_user;

ALTER TABLE lms_assignment
    DROP FOREIGN KEY lms_assignment_fk4,
    DROP FOREIGN KEY lms_assignment_lti_activity_id_context_id,
    DROP FOREIGN KEY lms_assignment_lti_course_id_lti_context_id,
    add constraint lms_assignment_lti_consumer_key
        foreign key (lti_consumer_key) references lti_consumer (consumer_key);

drop table lti_context;


