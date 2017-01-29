@execution
Feature: Run the default experiment in gareth-execution

  Scenario: Run default experiment with failed assumption
    When I set the baseline to sale of fruit
    Then the experiment environment key ping has value pong
    And I validate the assumption sale of fruit has risen by 79 per cent
    Then the assumption is not successful
    When I execute the failure step send email to Sam
    Then the experiment environment key result has value sending failure mail to Sam


  Scenario: Run default experiment with successful assumption
    When I set the baseline to sale of fruit
    Then the experiment environment key ping has value pong
    And I validate the assumption sale of fruit has risen by 81 per cent
    Then the assumption is successful
    When I execute the success step send email to Moos
    Then the experiment environment key result has value sending success mail to Moos