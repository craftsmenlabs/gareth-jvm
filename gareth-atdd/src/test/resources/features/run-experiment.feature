Feature: Run ACME experiment

  Scenario: Run experiment with successful outcome
    When I want to create an experiment named toss a coin
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 1 seconds
    And the assume is toggle success and failure
    And I create the template
    Then the template is correct
    When I start the experiment
    Then the experiment is ready
    And the experiment is started
    When I wait 2 seconds
    Then the experiment is completed successfully
    And the environment key result has value sending success mail to Moos

  Scenario: Run experiment with failed outcome
    When I start an experiment for template toss a coin
    Then the experiment is ready
    And the experiment is started
    When I wait 2 seconds
    Then the experiment is completed unsuccessfully
    And the environment key result has value sending failure mail to Sam

  Scenario: Cannot start experiment with incorrect template
    When I want to create an experiment named Wrong try
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 2 seconds
    And the assume is sale of apples has done nothing
    And I create the template
    Then the template is not correct
    And I cannot start the experiment

  Scenario: Get overview
    When I get the overviews for all templates
    And I look at the overview for template toss a coin
    Then there is 1 failed run
    And there is 1 successful runs
    And the template is ready
    And the template is not editable
    When I look at the overview for template Wrong try
    Then there is 0 failed run
    And there is 0 successful runs
    And the template is not ready
    And the template is editable