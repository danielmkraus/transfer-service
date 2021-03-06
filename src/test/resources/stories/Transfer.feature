# language: en
@transfer
Feature: Transfer between accounts
  Background:
    As a bank user
    I want to transfer my money to another accounts
    So that will allow to manage finances


  @basic
  Scenario: Transfer money to an account with negative balance
    Given an account "1" with a balance of 500.00
    And an account "2" with a balance of -500.00
    When transfer 499.99 from "1" to "2"
    Then successfully transfer
    And account "1" have balance of 0.01
    And account "2" have balance of -0.01

  @basic
  Scenario: Transfer money between accounts
    Given an account "1" with a balance of 500.00
    And an account "2"
    When transfer 200.53 from "1" to "2"
    Then successfully transfer
    And account "1" have balance of 299.47
    And account "2" have balance of 200.53

  @basic
  Scenario: Transfer money to an account without setting balance
    Given an account "1" with a balance of 500.00
    And an account "2" set without balance
    When transfer 200.53 from "1" to "2"
    Then successfully transfer
    And account "1" have balance of 299.47
    And account "2" have balance of 200.53

  @basic
  Scenario: Transfer all money between accounts
    Given an account "1" with a balance of 200.53
    And an account "2" with a balance of 0.00
    When transfer 200.53 from "1" to "2"
    Then successfully transfer
    And account "1" have balance of 0.00
    And account "2" have balance of 200.53

  @validation
  Scenario: Transfer more money than available between accounts
    Given an account "1" with a balance of 200.53
    And an account "2" with a balance of 0.00
    When transfer 200.54 from "1" to "2"
    Then a validation error on transfer occur
    And account "1" have balance of 200.53
    And account "2" have balance of 0.00

  @validation
  Scenario: Transfer to an unexistent account
    Given an account "1" with a balance of 200.53
    And does not exist account "3"
    When transfer 200.53 from "1" to "3"
    Then account is not found
    And account "1" have balance of 200.53

  @validation
  Scenario: Transfer from an unexistent account
    Given an account "1" with a balance of 200.53
    And does not exist account "3"
    When transfer 200.53 from "3" to "1"
    Then account is not found
    And account "1" have balance of 200.53

  @validation
  Scenario: Transfer to the same account
    Given an account "1" with a balance of 200.53
    When transfer 50.00 from "1" to "1"
    Then a validation error on transfer occur
    And account "1" have balance of 200.53

  @validation
  Scenario: Transfer negative amount between accounts
    Given an account "1" with a balance of 50.53
    And an account "2" with a balance of 0.00
    When transfer -1.00 from "1" to "2"
    Then a validation error on transfer occur
    And account "1" have balance of 50.53
    And account "2" have balance of 0.00

  @validation
  Scenario: Transfer zero amount between accounts
    Given an account "1" with a balance of 50.53
    And an account "2" with a balance of 0.00
    When transfer 0.00 from "1" to "2"
    Then a validation error on transfer occur
    And account "1" have balance of 50.53
    And account "2" have balance of 0.00