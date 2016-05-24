Feature: Tool identifies Applicant meets Category A Financial Requirement

  National Insurance Numbers (NINO) - Format and Security: A NINO is made up of two letters, six numbers and a final letter (which is always A, B, C, or D)
  Date formats: Format should be dd/mm/yyyy or d/m/yyyy

###################################### Section - Check for text on input page ######################################

#New scenario - Added in SD158
  Scenario: Input Page checks for Category A financial text write up (1)

  Given Robert is using the IPS Generic Tool
  Then The service displays the following message:
      | Page sub title | Individual's details                                                                                                                                                           |
      | Page sub text  | Use this tool to query HMRC. It will provide the income of a person using their National Insurance Number. |

###################################### Section - Check for Validation on NINO ######################################

#New scenario - Added in SD158
  Scenario: Caseworker does NOT enter a National Insurance Number (2)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  |            |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a National Insurance Number |
      | Error Field   | nino-error                                 |

#New scenaio - Added in SD158
  Scenario: Caseworker enters incorrect National Insurance Number prefixed with two characters (3)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | 11123456A  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid National Insurance Number |
      | Error Field   | nino-error                                       |

#New scenaio - Added in SD158
  Scenario: Caseworker enters incorrect National Insurance Number with two characters in the middle (4)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ12HR56A  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid National Insurance Number |
      | Error Field   | nino-error                                       |

#New scenaio - Added in SD158
  Scenario: Caseworker enters incorrect National Insurance Number with the last digit being a number (5)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ1235560  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid National Insurance Number |
      | Error Field   | nino-error                                       |

#New scenaio - Added in SD158
  Scenario: Caseworker enters incorrect National Insurance Number is not 9 characters (6)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ12545    |
      | From Date             | 01/01/2015 |
      | To Date               | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid National Insurance Number |
      | Error Field   | nino-error                                       |

###################################### Section - Check for Validation on Date fields ######################################

#New scenaio - Added in SD158
  Scenario: Caseworker enters an incorrect Application From Date (Day)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ129956A  |
      | From Date | 34/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid from Date |
      | Error Field   | application—raised-date-error                  |

#New scenaio - Added in SD158
  Scenario: Caseworker enters an incorrect Application To Date (Day)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ129956A  |
      | From Date | 34/01/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid from Date |
      | Error Field   | application—raised-date-error                  |

#New scenaio - Added in SD158
  Scenario: Caseworker enters an incorrect Application From Date (Month)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ129956A  |
      | From Date | 01/13/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid from Date |
      | Error Field   | application—raised-date-error                  |

#New scenaio - Added in SD158
  Scenario: Caseworker enters an incorrect Application To Date (Month)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ129956A  |
      | From Date | 01/13/2015 |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid from Date |
      | Error Field   | application—raised-date-error                  |

#New scenaio - Added in SD158
  Scenario: Caseworker enters an incorrect Application From Date (Year)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ129956A  |
      | From Date | 34/01/201D |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid from Date |
      | Error Field   | application—raised-date-error                  |

#New scenaio - Added in SD158
  Scenario: Caseworker enters an incorrect Application To Date (Year)
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ129956A  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/201E |
    Then The service displays the following message:
      | Error Message | Please provide a valid to Date |
      | Error Field   | application—raised-date-error                  |

#New scenaio - Added in SD158
  Scenario: Caseworker enters a blank Application From Date
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO                  | QQ128856A  |
      | From Date |            |
      | To Date   | 01/07/2015 |
    Then The service displays the following message:
      | Error Message | Please provide a valid from Date |
      | Error Field   | application—raised-date-error             |

#New scenaio - Added in SD158
  Scenario: Caseworker enters a blank Application To Date
    Given Robert is using the IPS Generic Tool
    When Robert submits a query:
      | NINO      | QQ128856A  |
      | From Date | 01/01/2015 |
      | To Date   |            |
    Then The service displays the following message:
      | Error Message | Please provide a valid to Date |
      | Error Field   | application—raised-date-error             |
