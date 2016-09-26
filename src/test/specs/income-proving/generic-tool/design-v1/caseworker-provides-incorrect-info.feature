Feature: Input validation

  National Insurance Numbers (NINO) - Format and Security: A NINO is made up of two letters, six numbers and a final letter (which is always A, B, C, or D)
  Date formats: Format should be dd/mm/yyyy or d/m/yyyy

  Background:
    Given Robert is using the IPS Generic Tool

###################################### Section - Check for text on input page ######################################

  Scenario: Input Page checks for Category A financial text write up
    Then The service displays the following message:
      | pageSubTitle | Individual details                                                                                         |
      | pageSubText  | Use this tool to query HMRC. It will provide the income of a person using their National Insurance Number. |


######################### General validation message display #########################

  Scenario: Error summary details are shown when a validation error occurs
    When Robert submits a query:
      | NINO      |  |
      | From Date |  |
      | To Date   |  |
    Then The service displays the following message:
      | validation-error-summary-heading | There's some invalid information                  |
      | validation-error-summary-text    | Make sure that all the fields have been completed |
    And the error summary list contains the text
      | The National Insurance Number is invalid |
      | The from date is invalid                 |
      | The to date is invalid                   |


###################################### Section - Check for Validation on NINO ######################################

  Scenario: Caseworker does NOT enter a National Insurance Number
    When Robert submits a query:
      | NINO      |            |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number prefixed with two characters
    When Robert submits a query:
      | NINO      | 11123456A  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number with two characters in the middle
    When Robert submits a query:
      | NINO      | QQ12HR56A  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number with the last digit being a number
    When Robert submits a query:
      | NINO      | QQ1235560  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

  Scenario: Caseworker enters incorrect National Insurance Number is not 9 characters
    When Robert submits a query:
      | NINO      | QQ12545    |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | nino-error | Enter a valid National Insurance Number |

###################################### Section - Check for Validation on Date fields ######################################

  Scenario: Caseworker enters an incorrect From Date (Day)
    When Robert submits a query:
      | NINO      | QQ129956A  |
      | From Date | 34/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect To Date (Day)
    When Robert submits a query:
      | NINO      | QQ129956A  |
      | From Date | 34/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect From Date (Month)
    When Robert submits a query:
      | NINO      | QQ129956A  |
      | From Date | 01/13/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect To Date (Month)
    When Robert submits a query:
      | NINO      | QQ129956A  |
      | From Date | 01/13/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect From Date (Year)
    When Robert submits a query:
      | NINO      | QQ129956A  |
      | From Date | 34/01/201D |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters an incorrect To Date (Year)
    When Robert submits a query:
      | NINO      | QQ129956A  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/201E |
    Then The service displays the following message:
      | toDate-error | Enter a valid to date |

  Scenario: Caseworker enters a blank From Date
    When Robert submits a query:
      | NINO      | QQ128856A  |
      | From Date |            |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | fromDate-error | Enter a valid from date |

  Scenario: Caseworker enters a blank To Date
    When Robert submits a query:
      | NINO      | QQ128856A  |
      | From Date | 01/01/2015 |
      | To Date   |            |
    Then The service displays the following message:
      | toDate-error | Enter a valid to date |
