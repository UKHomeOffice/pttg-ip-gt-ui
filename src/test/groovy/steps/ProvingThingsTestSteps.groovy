package steps

import cucumber.api.DataTable
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import java.text.SimpleDateFormat


class ProvingThingsTestSteps {
    @Managed
    public WebDriver driver;

    private int delay = 500

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    def parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
        Date date = sdf.parse(dateString)
        date
    }

    def sendKeys(WebElement element, String v) {
        element.clear();
        if (v != null && v.length() != 0) {
            element.sendKeys(v);
        }
    }

    // ---------------------------------------
    // Income Proving Service Case Worker Tool
    // ---------------------------------------

    @Given("^Robert is using the IPS Generic Tool\$")
    public void robert_is_using_the_IPS_Generic_Tool() throws Throwable {

        driver.get("http://localhost:8000");
    }

// SD65 This method is empty because the validation (in the Then function) is done on the input page opened in the Given function
    @When("^Robert is displayed the Income Proving Service Case Worker Tool input page:\$")
    public void robert_is_displayed_the_Income_Proving_Service_Case_Worker_Tool_input_page() {


    }

    @When("^Robert submits a query:\$")
    public void robert_submits_a_query(DataTable arg1) {

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        println driver.currentUrl

        List<String, String> entriesList = arg1.asList(String.class)

        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date")) {
                if (v != null && v.length() != 0) {

                    String day = v.substring(0, v.indexOf("/"))
                    String month = v.substring(v.indexOf("/")+1, v.lastIndexOf("/"))
                    String year = v.substring(v.lastIndexOf("/")+1)

                    sendKeys(driver.findElement(By.id(key + "Day")), day)
                    sendKeys(driver.findElement(By.id(key + "Month")), month)
                    sendKeys(driver.findElement(By.id(key + "Year")), year)

                } else {
                    driver.findElement(By.id(key + "Day")).clear()
                    driver.findElement(By.id(key + "Month")).clear()
                    driver.findElement(By.id(key + "Year")).clear()
                }
            } else {
                sendKeys(driver.findElement(By.id(key)), v)
            }
        }

        driver.sleep(delay)
        driver.findElement(By.className("button")).click();

    }

    @When("^the NINO is NOT entered:\$")
    public void the_nino_is_not_entered(DataTable arg1) {

        driver.sleep(delay)

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        String applicationReceivedDate = entries.get("Application Received Date")

        String[] date = applicationReceivedDate.split("/")

        driver.findElement(By.id("nino")).sendKeys(entries.get("NINO"))

        driver.findElement(By.id("applicationReceivedDateDay")).clear()
        driver.findElement(By.id("applicationReceivedDateDay")).sendKeys(date[0])

        driver.findElement(By.id("applicationReceivedDateMonth")).clear()
        driver.findElement(By.id("applicationReceivedDateMonth")).sendKeys(date[1])

        driver.findElement(By.id("applicationReceivedDateYear")).clear()
        driver.findElement(By.id("applicationReceivedDateYear")).sendKeys(date[2])

        driver.findElement(By.className("button")).click();
    }

    @When("^an incorrect NINO is entered:\$")
    public void an_incorrect_nino_is_eneterd(DataTable arg1) {

        driver.sleep(delay)

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        String applicationReceivedDate = entries.get("Application Received Date")

        String[] date = applicationReceivedDate.split("/")

        driver.findElement(By.id("nino")).sendKeys(entries.get("NINO"))

        driver.findElement(By.id("applicationReceivedDateDay")).clear()
        driver.findElement(By.id("applicationReceivedDateDay")).sendKeys(date[0])

        driver.findElement(By.id("applicationReceivedDateMonth")).clear()
        driver.findElement(By.id("applicationReceivedDateMonth")).sendKeys(date[1])

        driver.findElement(By.id("applicationReceivedDateYear")).clear()
        driver.findElement(By.id("applicationReceivedDateYear")).sendKeys(date[2])

        driver.findElement(By.className("button")).click();
    }

    @When("^Application Received Date is not entered:\$")
    public void application_received_date_not_entered(DataTable arg1) {

        driver.sleep(delay)

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        String applicationReceivedDate = entries.get("Application Received Date")

        String[] date = applicationReceivedDate.split("/")

        driver.findElement(By.id("nino")).sendKeys(entries.get("NINO"))

        driver.findElement(By.id("applicationReceivedDateDay")).clear()
        driver.findElement(By.id("applicationReceivedDateDay")).sendKeys(date[0])

        driver.findElement(By.id("applicationReceivedDateMonth")).clear()
        driver.findElement(By.id("applicationReceivedDateMonth")).sendKeys(date[1])

        driver.findElement(By.id("applicationReceivedDateYear")).clear()
        driver.findElement(By.id("applicationReceivedDateYear")).sendKeys(date[2])

        driver.findElement(By.className("button")).click();
    }

    @When("^an incorrect Application Received Date is entered:\$")
    public void an_incorrent_received_date_is_entered(DataTable arg1) {

        driver.sleep(delay)

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        String applicationReceivedDate = entries.get("Application Received Date")

        String[] date = applicationReceivedDate.split("/")

        driver.findElement(By.id("nino")).sendKeys(entries.get("NINO"))

        driver.findElement(By.id("applicationReceivedDateDay")).clear()
        driver.findElement(By.id("applicationReceivedDateDay")).sendKeys(date[0])

        driver.findElement(By.id("applicationReceivedDateMonth")).clear()
        driver.findElement(By.id("applicationReceivedDateMonth")).sendKeys(date[1])

        driver.findElement(By.id("applicationReceivedDateYear")).clear()
        driver.findElement(By.id("applicationReceivedDateYear")).sendKeys(date[2])

        driver.findElement(By.className("button")).click();
    }

    @Then("^The service provides the following result:\$")
    public void the_service_provides_the_following_results(DataTable expectedResult) {
        String amount
        for (int i = 1; i < expectedResult.raw().size()+1; i++) {
            String row = expectedResult.raw.get(i-1)

            String[] column_data = row.split(",")

            driver.sleep(delay)

            def dateXpath = '//*[@id="page2"]/table[2]/tbody['+i+']/tr/td[1]'
            assert column_data[0].contains(driver.findElement(By.xpath(dateXpath)).getText())
            println "dateXpath: " + driver.findElement(By.xpath(dateXpath)).getText()

            def amountXpath = '//*[@id="page2"]/table[2]/tbody['+i+']/tr/td[3]'

            amount = column_data[2] + "," + column_data[3]
            println "Amount --------->" + amount
            assert amount.contains(driver.findElement(By.xpath(amountXpath)).getText())
            println "amountXpath :" + driver.findElement(By.xpath(amountXpath)).getText()
        }
    }

    @Then("^The service displays the following message:\$")
    public void the_service_displays_the_following_message(DataTable arg1) {

        if (driver.currentUrl == "http://localhost:8001/income-proving-tool.html") {
            Map<String, String> entries = arg1.asMap(String.class, String.class)
            driver.sleep(delay)
            assert driver.findElement(By.id(entries.get("Error Field"))).getText() == entries.get("Error Message")
        } else if (driver.currentUrl.startsWith("http://localhost:8000/")) {
            Map<String, String> entries = arg1.asMap(String.class, String.class)
            driver.sleep(delay)
            assert driver.findElement(By.id(entries.get("Error Field"))).getText() == entries.get("Error Message")
        } else {
            assert false
        }
    }

    @Then("^The service provides the following Your search results:\$")
    public void the_service_provides_the_following_Your_search_results(DataTable expectedResult) throws Throwable {

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        WebElement yourSearchIndividualName = driver.findElement(By.id("yourSearchIndividualName"))
        WebElement yourSearchNationalInsuranceNumber = driver.findElement(By.id("yourSearchNationalInsuranceNumber"))
        WebElement yourSearchFromDate = driver.findElement(By.id("yourSearchFromDate"))
        WebElement yourSearchToDate = driver.findElement(By.id("yourSearchToDate"))


        for (String s : tableKey) {

            if (s == "Your Search Individual Name") {
                assert entries.get(s).contains(yourSearchIndividualName.getText())
                println "Your Search Individual Name: " + yourSearchIndividualName.getText()
            }

            if (s == "Your Search National Insurance Number") {
                assert entries.get(s).contains(yourSearchNationalInsuranceNumber.getText())
                println "Your Search National Insurance Number:  " + yourSearchNationalInsuranceNumber.getText()
            }

            if (s == "Your Search From Date") {
                assert entries.get(s).contains(yourSearchFromDate.getText())
                println "Your Search From Date: " + yourSearchFromDate.getText()

            }

            if (s == "Your Search To Date") {
                assert entries.get(s).contains(yourSearchToDate.getText())
                println "Your Search To Date: " + yourSearchToDate.getText()

            }

        }

    }

    // ------------------------------
    // IPS Family TM Case Worker Tool
    // ------------------------------
    @Given("^Caseworker is using the Income Proving Service Case Worker Tool\$")
    public void caseworker_is_using_the_Income_Proving_Service_Case_Worker_Tool() throws Throwable {
        driver.get("http://localhost:8000/");
        driver.manage().deleteAllCookies()
    }


    @When("^Robert submits a query to IPS Family TM Case Worker Tool:\$")
    public void robert_submits_a_query_to_ips_family_tm_case_worker_tool(DataTable arg1) {
        Map<String, String> entries = arg1.asMap(String.class, String.class)

        driver.sleep(delay)

        String applicationReceivedDate = entries.get("Application received date")

        String[] date = applicationReceivedDate.split("/")

        driver.findElement(By.id("nino")).sendKeys(entries.get("NINO"))

        driver.findElement(By.id("applicationReceivedDateDay")).clear()
        driver.findElement(By.id("applicationReceivedDateDay")).sendKeys(date[0])

        driver.findElement(By.id("applicationReceivedDateMonth")).clear()
        driver.findElement(By.id("applicationReceivedDateMonth")).sendKeys(date[1])

        driver.findElement(By.id("applicationReceivedDateYear")).clear()
        driver.findElement(By.id("applicationReceivedDateYear")).sendKeys(date[2])

        entries.get("NINO") + "_" + applicationReceivedDate.replace('/', '-')

        driver.findElement(By.className("button")).click();
    }

    @Then("^The IPS Family TM Case Worker Tool provides the following result:\$")
    public void the_ips_family_tm_case_worker_tool_provides_the_following_results(DataTable expectedResult) {
        //driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS)

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        String[] outcome = entries.keySet()

        // To manually take a screenshot
        // net.serenitybdd.core.Serenity.takeScreenshot()

        for (String s : outcome) {
            if (s != "Outcome") {
                String elementId = toCamelCase(s)
                WebElement element = driver.findElement(By.id(elementId))

                String elementText = element.getText()
                String compareTo = entries.get(s)
                assert element.getText().contains(entries.get(s))

            } else {
                String elementId = toCamelCase(s)
                WebElement element = driver.findElement(By.id(elementId))
                String cssValue = element.getAttribute("class")
                assert cssValue.contains("panel-fail") == false
            }
        }


    }


    @Then("^The IPS Family TM CW Tool output page provides the following result:\$")
    public void the_IPS_Family_TM_CW_Tool_output_page_provides_the_following_result(DataTable expectedResult) {

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)

        String[] tableValue = entries.keySet()

//Page checks for Category A financial text write up SD64
        driver.sleep(delay)
        WebElement pageTitle = driver.findElement(By.className("form-title"))


        if (pageTitle.getText() == entries.get("Page title")) {

            assert true

            println " " + entries.get("Page title")

        } else assert false


        WebElement pageSubText = driver.findElement(By.className("lede"))

        assert pageSubText.getText() == entries.get("Page sub text")


    }

    //Page checks for appendix link - SD64
    @Then("^The IPS Family TM CW Tool output page provides the following result appendix:\$")
    public void the_IPS_Family_TM_CW_Tool_output_page_provides_the_following_result_appendix(DataTable expectedResult) {

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        WebElement pageAppendixTitle = driver.findElement(By.className("summary"))
        WebElement chapterEightLink = driver.findElement(By.xpath('//*[@id="details-content-0"]/p[1]/a'))
        WebElement fM_1_7_link = driver.findElement(By.xpath('//*[@id="details-content-0"]/p[2]/a'))

        for (String s : tableKey) {
            if (tableKey.contains(s)) {
                if (s == "Page appendix title") {
                    assert pageAppendixTitle.getText() == entries.get(s)
                    println "Appendix link: " + pageAppendixTitle.getText()
                    pageAppendixTitle.click()
                }


                if (s == "Chapter 8 link") {
                    assert chapterEightLink.getText() == entries.get(s)
                    println "link: " + chapterEightLink.getText()

                }


                if (s == "FM 1_7 link") {

                    assert fM_1_7_link.getText() == entries.get(s)

                }

            } else if (!tableKey.contains(s)) {
                assert false
            }

        }
    }

//SD41
    @Then("^The service for Cat A Failure provides the following result:\$")
    public void the_service_for_Cat_A_Failure_provides_the_following_result(DataTable expectedResult) {
        driver.sleep(delay)
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        WebElement pagestaticHeading = driver.findElement(By.id("pageStaticHeading"))
        WebElement pageStaticDetail = driver.findElement(By.id("pageStaticDetail"))
        WebElement pageDynamicHeading = driver.findElement(By.className("form-title"))
        WebElement categoryACheckFailureReason = driver.findElement(By.id("categoryACheckFailureReason"))
        WebElement yourSearchIndividualName = driver.findElement(By.id("yourSearchIndividualName"))
        WebElement yourSearchNationalInsuranceNumber = driver.findElement(By.id("yourSearchNationalInsuranceNumber"))
        WebElement yourSearchApplicationReceivedDate = driver.findElement(By.id("yourSearchApplicationReceivedDate"))

        for (String s : tableKey) {

            if (s == "Page static heading") {
                assert pagestaticHeading.getText() == entries.get(s)
                println "Page Static Heading : " + pagestaticHeading.getText()

            }

            if (s == "Page static detail") {
                assert pageStaticDetail.getText() == entries.get(s)
            }

            if (s == "Page dynamic heading") {
                assert pageDynamicHeading.getText().contains(entries.get(s))
                println "Page dynamic heading: " + pageDynamicHeading.getText()
            }

            if (s == "Category A check failure reason") {

                assert entries.get(s).contains(categoryACheckFailureReason.getText())
                println "Category A check failure reason :" + categoryACheckFailureReason.getText()
            }

            if (s == "Your Search Individual Name") {
                assert yourSearchIndividualName.getText() == entries.get(s)
                println "Your Search Individual Name :" + yourSearchIndividualName.getText()
            }

            if (s == "Your Search National Insurance Number") {
                assert yourSearchNationalInsuranceNumber.getText() == entries.get(s)
                println "Your Search National Insurance Number: " + yourSearchNationalInsuranceNumber.getText()
            }

            if (s == "Your Search Application Received Date") {

                assert yourSearchApplicationReceivedDate.getText() == entries.get(s)
                println "Your Search Application Received Date: " + yourSearchApplicationReceivedDate.getText()

            }

        }

    }
//SD63
    @Then("^The service provides the following NINO does not exist result:\$")
    public void the_service_provides_the_following_NINO_does_not_exist_result(DataTable expectedResult) {
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        WebElement pageDynamicHeading = driver.findElement(By.id("pageDynamicHeading"))
        WebElement pageDynamicSubText = driver.findElement(By.id("pageDynamicSubText"))
        driver.sleep(delay)
        WebElement yourSearchNationalInsuranceNumber = driver.findElement(By.id("yourSearchNationalInsuranceNumber"))
        driver.sleep(delay)
        WebElement yourSearchApplicationReceivedDate = driver.findElement(By.id("yourSearchApplicationReceivedDate"))

        for (String s : tableKey) {
            // driver.sleep(delay)
            if (s == "Page dynamic heading") {

                assert entries.get(s).contains(pageDynamicHeading.getText())
                println "Page Dynamic Heading : " + pageDynamicHeading.getText()

            }

            if (s == "Page dynamic sub text") {

                assert entries.get(s).contains(pageDynamicSubText.getText())
                println "Page dynamic sub text: " + pageDynamicSubText.getText()
            }



            if (s == "Your Search National Insurance Number") {

                assert entries.get(s).contains(yourSearchNationalInsuranceNumber.getText())
                println "Your Search National Insurance Number :" + yourSearchNationalInsuranceNumber.getText()
            }

            if (s == "Your Search Application Received Date") {
                driver.sleep(delay)
                assert entries.get(s).contains(yourSearchApplicationReceivedDate.getText())
                println "Your Search Application Received Date :" + yourSearchApplicationReceivedDate.getText()
            }

        }

    }
// SD65
    @Then("^The IPS Family TM Case Worker Tool input page provides the following result:\$")
    public void the_IPS_Family_TM_Case_Worker_Tool_input_page_provides_the_following_result(DataTable expectedResult) {

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()
        WebElement pageSubTitle = driver.findElement(By.id("pageSubTitle"))
        WebElement pageSubText = driver.findElement(By.id("pageSubText"))

        for (String s : tableKey) {

            if (s == "Page sub title") {

                assert pageSubTitle.getText() == entries.get(s)
            }

            if (s == "Page sub text ") {

                assert pageSubText.getText() == entries.get(s)
                println "Page sub text: " + pageSubText.getText()

            }

        }

    }

}