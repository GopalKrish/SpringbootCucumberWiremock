Feature: User API Testing with JSON Templates
  As an API consumer
  I want to perform CRUD operations on users
  So that I can manage user data

  Background:
    Given the API is running successfully
   And Wiremock server is running

  @SCRUM-7
  Scenario: Get all users when no users exist
    And Add stub "200-first-response.json" and verify response as 202
    When I send a GET request to "/users"
    Then the response status should be 200
    And the response should be an empty array

  @SCRUM-4
  Scenario Outline: Create a new user from JSON template
    When I send a POST request to "/users" with the following user data:
      | name   | email   |
      | <name> | <email> |
    Then the response status should be 200
    And the response should contain the created user data:
      | name   | email   |
      | <name> | <email> |
    And the response should have a valid id

    Examples:
      | name        | email                   |
      | John Doe    | john.doe@example.com    |
      | Jane Smith  | jane.smith@example.com  |
      | Bob Johnson | bob.johnson@example.com |

  @SCRUM-6
  Scenario Outline: Get user by ID using template data
    Given a user exists with the following data:
      | name   | email   |
      | <name> | <email> |
    When I send a GET request to "/users/{id}"
    Then the response status should be 200
    And the response should contain the user data:
      | name   | email   |
      | <name> | <email> |

    Examples:
      | name          | email                     |
      | Alice Brown   | alice.brown@example.com   |
      | Charlie Davis | charlie.davis@example.com |

  @SCRUM-5
  Scenario Outline: Update an existing user with template
    Given a user exists with the following data:
      | name          | email                |
      | Original Name | original@example.com |
    When I send a PUT request to "/users/{id}" with the updated data:
      | name   | email   |
      | <name> | <email> |
    Then the response status should be 200
    And the response should contain the updated user data:
      | name   | email   |
      | <name> | <email> |

    Examples:
      | name          | email                     |
      | Updated Name  | updated.name@example.com  |
      | Modified User | modified.user@example.com |

#  Scenario Outline: Delete an existing user
#    Given a user exists with the following data:
#      | name     | email                 |
#      | <name>   | <email>               |
#    When I send a DELETE request to "/users/{id}"
#    Then the response status should be 200
#    And the user should no longer exist
#
#    Examples:
#      | name          | email                   |
#      | To Delete     | delete@example.com      |
#      | Temp User     | temp.user@example.com   |
#
#  Scenario: Successfully retrieve user bank details
#    Given Add stub for bank "200-bank-details-response.json" and verify response as 200
#    Given I request bank details for user ID 123
#    Then the response status should be 200
#    And Verify the users bank response data

  Scenario: Successfully retrieve user bank details
    Given Add stub for bank "200-bank-details-response.json" and verify response as 200
    Given I request bank details for user ID 123
    Then the response status should be 200
    And Verify the users bank response data