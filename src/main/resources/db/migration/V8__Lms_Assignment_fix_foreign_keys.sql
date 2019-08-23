ALTER TABLE `lms_assignment`
    drop foreign key `lms_assignment_fk2`,
    drop foreign key `lms_assignment_fk3`,
    add constraint lms_assignment_lti_course_id_lti_context_id
        foreign key (lti_course_id) references lti_context (lti_context_id),
    add constraint lms_assignment_lti_activity_id_context_id
        foreign key (lti_activity_id) references lti_context (context_id);


