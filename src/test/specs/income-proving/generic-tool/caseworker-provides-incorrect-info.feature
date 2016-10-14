Feature: Input validation

  National Insurance Numbers (NINO) - Format and Security: A NINO is made up of two letters, six numbers and a final letter (which is always A, B, C, or D)
  Date formats: Format should be dd/mm/yyyy or d/m/yyyy

  Background:
    Given Robert is using the IPS Generic Tool
    And the default details are
      | NINO      | QQ123456A  |
      | From Date | 01/01/2015 |
      | To Date   | 30/06/2015 |

###################################### Section - Check for text on input page ######################################

  Scenario: Input Page checks for Category A financial text write up
    Then the service displays the following message:
      | pageSubTitle | Individual details                                                                                         |
      | pageSubText  | Use this tool to query HMRC. It will provide the income of a person using their National Insurance Number. |


######################### General validation message display #########################

  Scenario: Error summary details are shown when a validation error occurs
    When Robert submits a query:
      | NINO      |  |
      | From Date |  |
      | To Date   |  |
    Then the service displays the following message:
      | validation-error-summary-heading | There's some invalid information                  |
      | validation-error-summary-text    | Make sure that all the fields have been completed |
    And the error summary list contains the text
      | The National Insurance Number is invalid |
      | The from date is invalid                 |
      | The to date is invalid                   |


###################################### Section - Check for Validation on NINO ######################################

  Scenario: Caseworker does NOT enter a National Insurance Number
    When Robert submits a query:
      | NINO |  |
    Then the service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number prefixed with two characters
    When Robert submits a query:
      | NINO | 11123456A |
    Then the service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number with two characters in the middle
    When Robert submits a query:
      | NINO | QQ12HR56A |
    Then the service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number with the last digit being a number
    When Robert submits a query:
      | NINO | QQ1235560 |
    Then the service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number is not 9 characters
    When Robert submits a query:
      | NINO | QQ12545 |
    Then the service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

###################################### Section - Check for Validation on Date fields ######################################

  Scenario: Caseworker enters an incorrect From Date (Day)
    When Robert submits a query:
      | From Date | 34/01/2015 |
    Then the service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect To Date (Day)
    When Robert submits a query:
      | To Date | 34/01/2015 |
    Then the service displays the following message:
      | toDate-error | Enter a valid to date |

  Scenario: Caseworker enters an incorrect From Date (Month)
    When Robert submits a query:
      | From Date | 01/13/2015 |
    Then the service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect To Date (Month)
    When Robert submits a query:
      | NINO    | QQ129956A  |
      | To Date | 01/13/2015 |
    Then the service displays the following message:
      | toDate-error | Enter a valid to date |

  Scenario: Caseworker enters an incorrect From Date (Year)
    When Robert submits a query:
      | From Date | 34/01/201D |
    Then the service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect To Date (Year)
    When Robert submits a query:
      | To Date | 01/07/201E |
    Then the service displays the following message:
      | toDate-error | Enter a valid to date |

  Scenario: Caseworker enters a blank From Date
    When Robert submits a query:
      | From Date |  |
    Then the service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters a blank To Date
    When Robert submits a query:
      | To Date |  |
    Then the service displays the following message:
      | toDate-error | Enter a valid to date |
