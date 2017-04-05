Feature: Create experiment template

  Background:
    When the database is cleared

  @create-template
  Scenario: Create correct template
    When I want to create an experiment named Template 10
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 2 seconds
    And the assume is sale of apples has risen by 8 per cent
    And I create the template
    Then the template is correct

  Scenario: Cannot create template with non-unique name
    When I want to create an experiment named Template 10
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 2 seconds
    And the assume is sale of apples has risen by 8 per cent
    Then I cannot create the template

  Scenario: Create template with invalid assume glueline, then correct it
    When I want to create an experiment named Template 20
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 2 seconds
    And the assume is sale of apples has done nothing
    And I create the template
    Then the template is not correct
    When I update the assume line of the current template to sale of apples has risen by 8 per cent
    Then the template is correct

  Scenario: Create template with valid assume glueline, then change it to an incorrect value
    When I want to create an experiment named Template 30
    And the baseline is sale of apples
    And the success is send email to Moos
    And the failure is send email to Sam
    And the time is 2 seconds
    And the assume is sale of apples has risen by 8 per cent
    And I create the template
    Then the template is correct
    When I update the assume line of the current template to sale of apples has done nothing
    Then the template is not correct
