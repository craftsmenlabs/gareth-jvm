Feature: Run ACME experiment

  Scenario: Run experiment with successful outcome
    When I want to create an experiment named toss a coin
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 3 seconds
    And the assume is toggle success and failure
    And I create the template
    Then the template is correct
    When I start the experiment immediately
    And I wait 1 seconds
    Then the experiment is running
    When I wait 4 seconds
    Then the experiment is completed
    And the environment key result has value sending success mail to Moos

  Scenario: Run experiment with failed outcome
    When I start an experiment for template toss a coin in 1 second
    When I wait 5 seconds
    Then the experiment is completed
    And the environment key result has value sending failure mail to Sam

  Scenario: Run experiment with delayed start
    When I start an experiment for template toss a coin in 3 seconds
    When I wait 1 seconds
    Then the experiment is pending
    When I wait 3 seconds
    Then the experiment is running
    When I wait 3 seconds
    Then the experiment is completed

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