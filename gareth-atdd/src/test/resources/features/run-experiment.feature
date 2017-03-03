Feature: Run ACME experiment

  Background:
    When I want to create an experiment named Hello world
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 2 seconds

  Scenario: Run experiment with successful outcome
    When the assume is sale of apples has risen by 8 per cent
    And I submit the experiment
    Then the experiment is created
    When I wait 2 seconds
    Then the experiment is ready
    When I start the experiment
    Then the experiment is started
    When I wait 3 seconds
    Then the experiment is completed successfully
    And the environment key result has value sending success mail to Moos

  Scenario: Run experiment with failed outcome
    When the assume is sale of apples has risen by 11 per cent
    And I submit the experiment
    Then the experiment is created
    When I wait 2 seconds
    Then the experiment is ready
    When I start the experiment
    Then the experiment is started
    When I wait 3 seconds
    Then the experiment is completed unsuccessfully
    And the environment key result has value sending failure mail to Sam