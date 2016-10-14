Feature: Tool identifies applicant NINO does not exist

  Scenario: Caseworker enters a NINO where no records exist within the period stated
    Given Robert is using the IPS Generic Tool
    Given no record for RK123456C
    When Robert submits a query:
      | NINO      | RK123456C  |
      | From Date | 01/01/2015 |
      | To Date   | 01/07/2015 |
    Then the service provides the following NINO does not exist result:
      | Page dynamic heading                  | There is no record for RK123456C with HMRC                                                                                                 |
      | Page dynamic detail                   | We couldn't perform the financial requirement check as no income information exists with HMRC for the National Insurance Number RK123456C. |
      | Your Search National Insurance Number | RK123456C                                                                                                                                  |
      | Your Search From Date                 | 01/01/2015                                                                                                                                 |
      | Your Search To Date                   | 01/07/2015                                                                                                                                 |

  Scenario: Caseworker enters a NINO where no records exist within the period stated
    Given Robert is using the IPS Generic Tool
    Given no record for KR123456C
    When Robert submits a query:
      | NINO      | KR123456C  |
      | From Date | 01/04/2015 |
      | To Date   | 20/12/2015 |
    Then the service provides the following NINO does not exist result:
      | Page dynamic heading                  | There is no record for KR123456C with HMRC                                                                                                 |
      | Page dynamic detail                   | We couldn't perform the financial requirement check as no income information exists with HMRC for the National Insurance Number KR123456C. |
      | Your Search National Insurance Number | KR123456C                                                                                                                                  |
      | Your Search From Date                 | 01/04/2015                                                                                                                                 |
      | Your Search To Date                   | 20/12/2015                                                                                                                                 |
