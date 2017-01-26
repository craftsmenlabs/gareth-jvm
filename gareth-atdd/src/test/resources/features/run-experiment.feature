Feature: Run ACME experiment

  Scenario: Run experiment
    When I want to create an experiment
    And the baseline is blah
    And the assume is blah
    And the success is blah
    And the failure is blah
    And the time is 5 seconds
    And I submit the experiment
    Then the experiment is created