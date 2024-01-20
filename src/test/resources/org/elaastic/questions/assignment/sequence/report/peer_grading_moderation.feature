@txn
Feature: Peer grading moderation

  Rule: A peer grading can be moderated by a teacher
    As a teacher
    I want to moderate peer grading
    So that I can ensure that students are grading each other fairly

    Scenario: Hiding a peer grading
      Given a peer grading
      And the teacher owner of the sequence the peer grading belongs to
      When The teacher hide the peer grading
      Then the peer grading is mark as hidden

    Scenario: Removing a peer grading
      Given a peer grading
      And the teacher owner of the sequence the peer grading belongs to
      When The teacher remove the peer grading
      Then the peer grading is mark as removed

  Rule: A peer grading can be reported by a learner
    As a learner
    I want to report a peer grading
    So that I can ensure that students are grading each other fairly

    Scenario: Reporting a peer grading without comment
      Given a peer grading
      And the learner owner of the response the peer grading belongs to
      When The learner report the peer grading without comment
      Then the peer grading has report reason
      And the peer grading have no comment in the report

    Scenario: Reporting a peer grading with comment
      Given a peer grading
      And the learner owner of the response the peer grading belongs to
      When The learner report the peer grading with comment
      Then the peer grading has report reason
      And the peer grading have a comment attached to the report