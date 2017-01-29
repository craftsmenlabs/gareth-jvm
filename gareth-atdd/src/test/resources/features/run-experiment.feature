Feature: Run ACME experiment

  Scenario: Run experiment
    When I want to create an experiment
    And the baseline is sale of fruit
    And the assume is sale of fruit has risen by 81 per cent
    And the success is send email to Moos
    And the failure is send text to Sam
    And the time is 5 seconds
    And I submit the experiment
    Then the experiment is created
    And the experiment is ready
    When I start the experiment
    Then the experiment is started
    When I wait 7 seconds
    Then the experiment is completed
    And the experiment environment key result has value sending success mail to Moos