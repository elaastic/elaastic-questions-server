Feature: Peer grading moderation

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
    When The teacher hide the peer grading
    Then the peer grading is mark as removed

