#@check-applicants-income
Feature: Robert checks a NINO income to understand how much Jon has earned within a given period.
  This feature of the Income Proving API allows a client to ask the question:

  “How much income has the applicant or spouse earned within a given period?"

  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (single job)
    Given Robert is using the IPS Generic Tool
    Given the account data for QQ123456A
    When Robert submits a query:
      | NINO      | QQ123456A  |
      | From Date | 01/01/2015 |
      | To Date   | 30/06/2015 |
    Then The service provides the following result:
      | 03/01/2015 | Flying Pizza Ltd | £1,666.11 |
      | 03/02/2015 | Flying Pizza Ltd | £1,666.11 |
      | 05/03/2015 | Flying Pizza Ltd | £1,666.11 |
      | 03/04/2015 | Flying Pizza Ltd | £1,666.11 |
      | 03/05/2015 | Flying Pizza Ltd | £1,666.11 |
      | 03/06/2015 | Flying Pizza Ltd | £1,666.11 |
      | Total:     |                  | £9,996.66 |
    And The service provides the following Your search results:
      | Your Search Individual Name           | Harry Callahan |
      | Your Search National Insurance Number | QQ123456A      |
      | Your Search From Date                 | 01/01/2015     |
      | Your Search To Date                   | 30/06/2015     |

  Scenario: Robert obtains NINO income details to understand how much they have earned within 12 months (multiple jobs over year period)
    Given Robert is using the IPS Generic Tool
    Given the account data for QQ654321A
    When Robert submits a query:
      | NINO      | QQ654321A  |
      | From Date | 01/01/2015 |
      | To Date   | 31/12/2015 |
    Then The service provides the following result:
      | 11/01/2015 | Sheffield Spice  | £1,000.00  |
      | 11/02/2015 | Sheffield Spice  | £1,000.00  |
      | 11/03/2015 | Sheffield Spice  | £1,000.00  |
      | 11/04/2015 | Sheffield Spice  | £3,000.00  |
      | 11/05/2015 | Sheffield Spice  | £1,000.00  |
      | 11/06/2015 | Sheffield Spice  | £1,000.00  |
      | 13/07/2015 | Sheffield Spice  | £2,500.00  |
      | 11/08/2015 | Sheffield Spice  | £1,000.00  |
      | 11/09/2015 | Flying Pizza Ltd | £1,666.00  |
      | 13/10/2015 | Flying Pizza Ltd | £1,666.00  |
      | 11/11/2015 | Flying Pizza Ltd | £1,666.00  |
      | 11/12/2015 | Flying Pizza Ltd | £1,666.00  |
      | Total:     |                  | £18,164.00 |
    And The service provides the following Your search results:
      | Your Search Individual Name           | Harry Callahan |
      | Your Search National Insurance Number | QQ654321A      |
      | Your Search From Date                 | 01/01/2015     |
      | Your Search To Date                   | 31/12/2015     |

  Scenario: Robert obtains NINO income details to understand how much they have earned within 6 months (multiple jobs per month)
    Given Robert is using the IPS Generic Tool
    Given the account data for QQ023987A
    When Robert submits a query:
      | NINO      | QQ023987A  |
      | From Date | 01/01/2015 |
      | To Date   | 30/06/2015 |
    Then The service provides the following result:
      | 10/01/2015 | Flying Pizza Ltd | £2,000.00  |
      | 17/01/2015 | Halifax PLC      | £1,000.00  |
      | 01/02/2015 | Flying Pizza Ltd | £1,666.00  |
      | 10/02/2015 | Halifax PLC      | £2,000.00  |
      | 01/03/2015 | Flying Pizza Ltd | £1,666.00  |
      | 10/03/2015 | Halifax PLC      | £2,000.00  |
      | 01/04/2015 | Flying Pizza Ltd | £1,666.00  |
      | 10/04/2015 | Halifax PLC      | £2,000.00  |
      | 10/05/2015 | Halifax PLC      | £2,000.00  |
      | 10/06/2015 | Halifax PLC      | £2,000.00  |
      | 17/06/2015 | Halifax PLC      | £2,000.00  |
      | Total:     |                  | £19,998.00 |
    And The service provides the following Your search results:
      | Your Search Individual Name           | Harry Callahan |
      | Your Search National Insurance Number | QQ023987A      |
      | Your Search From Date                 | 01/01/2015     |
      | Your Search To Date                   | 30/06/2015     |

  Scenario: Robert obtains NINO income details to understand how much he has earned within 6 months
    Given Robert is using the IPS Generic Tool
    Given the account data for QQ987654A
    When Robert submits a query:
      | NINO      | QQ987654A  |
      | From Date | 01/01/2015 |
      | To Date   | 30/06/2015 |
    Then The service provides the following result:
      | 04/01/2015 | Flying Pizza Ltd | £1,666.00 |
      | 04/02/2015 | Flying Pizza Ltd | £1,666.00 |
      | 20/05/2015 | Pizza Hut LTD    | £2,500.00 |
      | 20/06/2015 | Pizza Hut LTD    | £1,666.00 |
      | Total:     |                  | £7,498.00 |
    And The service provides the following Your search results:
      | Your Search Individual Name           | Harry Callahan |
      | Your Search National Insurance Number | QQ987654A      |
      | Your Search From Date                 | 01/01/2015     |
      | Your Search To Date                   | 30/06/2015     |

  Scenario: Robert obtains NINO income details to understand how much he has earned within 12 months
    Given Robert is using the IPS Generic Tool
    Given the account data for QQ765432A
    When Robert submits a query:
      | NINO      | QQ765432A  |
      | From Date | 01/02/2015 |
      | To Date   | 31/01/2016 |
    Then The service provides the following result:
      | 01/02/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/03/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/04/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/05/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/06/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/07/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/08/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/09/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/10/2015 | Flying Pizza Ltd | £1,666.00  |
      | 01/11/2015 | Flying Pizza Ltd | £1,500.00  |
      | 01/12/2015 | Flying Pizza Ltd | £1,000.00  |
      | 01/01/2016 | Flying Pizza Ltd | £2,500.00  |
      | Total:     |                  | £19,994.00 |
    And The service provides the following Your search results:
      | Your Search Individual Name           | Harry Callahan |
      | Your Search National Insurance Number | QQ765432A      |
      | Your Search From Date                 | 01/02/2015     |
      | Your Search To Date                   | 31/01/2016     |