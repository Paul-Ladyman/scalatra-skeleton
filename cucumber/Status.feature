Feature: ELB Health Check

  Scenario: Status requested
    When I "GET" "/status"
    Then the response status code is 200
