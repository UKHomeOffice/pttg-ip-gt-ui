package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.Scenario
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import uk.gov.digital.ho.proving.income.ServiceRunner
import static com.jayway.restassured.RestAssured.given;

import java.text.SimpleDateFormat

import static java.util.concurrent.TimeUnit.SECONDS

@SpringApplicationConfiguration(ServiceRunner.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test")
class ProvingThingsTestSteps {

    def static rootUrl = "http://localhost:8000/"
    //def static rootUrl = "https://pttg-ip-gt-ui-dev.notprod.homeoffice.gov.uk/"
    def healthUriRegex = "/healthz"
    def incomeUriRegex = "/incomeproving/v1/individual/nino/income"
    def defaultNino = "AA123456A"

    def testDataLoader
    def dateParts = ["Day", "Month", "Year"]

    @Value('${wiremock}')
    private Boolean wiremock;

    @Managed
    public WebDriver driver;

    private int delay = 500
    def defaultTimeout = 2000

    def defaultFields

    @Before
    def setUp(Scenario scenario) {
        if (wiremock) {
            testDataLoader = new WireMockTestDataLoader()
        }
    }

    @After
    def tearDown() {
        testDataLoader?.stop()
    }

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

    private def assertDate(String fieldName, String v) {
        String fieldval = ''
        dateParts.each { part ->
            fieldval += '/' + driver.findElement(By.id(fieldName + part)).getAttribute("value").padLeft(2, '0')
        }
        assert fieldval.substring(1) == v
    }

    private void assertTextFieldEqualityForTable(DataTable expectedResult) {
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        assertTextFieldEqualityForMap(entries)
    }

    private Map<String, String> assertTextFieldEqualityForMap(Map<String, String> entries) {

        entries.each { k, v ->
            String fieldName = toCamelCase(k);
            WebElement element = driver.findElement(By.id(fieldName))

            assert element.getText() == v
        }
    }

    private void assertInputValueEqualityForTable(DataTable expectedResult) {
        Map<String, String> entries = expectedResult.asMap(String.class, String.class)
        assertInputValueEqualityForMap(entries)
    }

    private Map<String, String> assertInputValueEqualityForMap(Map<String, String> entries) {

        entries.each { k, v ->
            String fieldName = toCamelCase(k);
            if (fieldName.endsWith("Date") || fieldName.equals("dob")) {
                assertDate(fieldName, v)

            } else if (fieldName.equals("sortCode")) {
                assertSortcode(fieldName, v)

            } else if (fieldName == "inLondon") {
                assertRadioSelection(inLondonRadio, v)

            } else if (fieldName == "studentType") {
                assertRadioSelection(studentTypeRadio, v)

            } else {
                assert driver.findElement(By.id(fieldName)).getAttribute("value") == v
            }
        }
    }


    def responseStatusFor(String url) {
        Response response = given()
                .get(url)
                .then().extract().response();

        return response.getStatusCode();
    }

    @Given("^the account data for (.*)\$")
    def the_account_data_for(String nino) {
        testDataLoader.stubTestData(nino, incomeUriRegex.replaceFirst("nino", nino))
    }

    @Given("^no record for (.*)\$")
    def no_record_for(String nino) {
        testDataLoader.stubErrorData("notfound", incomeUriRegex.replaceFirst("nino", nino), 404)
    }

    @Given("^Robert is using the IPS Generic Tool\$")
    public void robert_is_using_the_IPS_Generic_Tool() throws Throwable {
        driver.get(rootUrl);
    }

    @Given("^the default details are\$")
    public void the_default_details_are(DataTable arg1) throws Throwable {
        defaultFields = arg1
    }

    @Given("^the api response is delayed for (\\d+) seconds\$")
    public void the_api_response_is_delayed_for_seconds(int delay) throws Throwable {
        testDataLoader.withDelayedResponse(incomeUriRegex.replaceFirst("nino", defaultNino), delay)
    }

    @Given("^the api response is garbage\$")
    public void the_api_response_is_garbage() throws Throwable {
        testDataLoader.withGarbageResponse(incomeUriRegex.replaceFirst("nino", defaultNino))
    }

    @Given("^the api response is empty\$")
    public void the_api_response_is_empty() throws Throwable {
        testDataLoader.withEmptyResponse(incomeUriRegex.replaceFirst("nino", defaultNino))
    }

    @Given("^the api response has status (\\d+)\$")
    public void the_api_response_has_status(int status) throws Throwable {
        testDataLoader.withResponseStatus(incomeUriRegex.replaceFirst("nino", defaultNino), status)
    }

    @Given("^the api health check response has status (\\d+)\$")
    public void the_api_healthcheck_response_has_status(int status) throws Throwable {
        testDataLoader.withResponseStatus(healthUriRegex, status)
    }

    @Given("^the api is unreachable\$")
    public void the_api_is_unreachable() throws Throwable {
        testDataLoader.withServiceDown()
    }

    @Given("^the api response is a validation error - (.*) parameter\$")
    public void the_api_response_is_a_validation_error(String type) throws Throwable {
        testDataLoader.stubErrorData("validation-error-$type", incomeUriRegex.replaceFirst("nino", defaultNino), 400)
    }

    @When("^the income check is performed\$")
    def the_income_check_is_performed() {
        if (defaultFields) {
            submitEntries(defaultFields.asMap(String.class, String.class))
        } else {
            Map<String, String> validDefaultEntries = [
                    'From date': '01/05/2016',
                    'To date'  : '30/05/2016',
                    'NINO'     : defaultNino
            ]

            submitEntries(validDefaultEntries)
        }
    }

    @When("^Robert submits a query to IPS Family TM Case Worker Tool:\$")
    public void robert_submits_a_query_to_ips_family_tm_case_worker_tool(DataTable arg1) {
        Map<String, String> entries = arg1.asMap(String.class, String.class)

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


    @When("^the new search button is clicked\$")
    public void the_new_search_button_is_clicked() throws Throwable {
        driver.sleep(delay)
        driver.findElement(By.className("button--newSearch")).click()
    }

    @When("^the edit search button is clicked\$")
    public void the_edit_search_button_is_clicked() throws Throwable {
        driver.sleep(delay)
        driver.findElement(By.className("button--editSearch")).click()
    }

    @When("^Robert submits a query")
    public void robert_submits_a_query(DataTable arg1) {
        Map<String, String> entries = arg1.asMap(String.class, String.class)
        if (defaultFields) {
            Map<String, String> defaultEntries = defaultFields.asMap(String.class, String.class)
            submitEntries(defaultEntries + entries)
        } else {
            submitEntries(entries)
        }
    }


    private void submitEntries(Map<String, String> entries) {

        entries.each { k, v ->
            String key = toCamelCase(k)

            if (key.endsWith("Date")) {
                if (v != null && v.length() != 0) {

                    String day = v.substring(0, v.indexOf("/"))
                    String month = v.substring(v.indexOf("/") + 1, v.lastIndexOf("/"))
                    String year = v.substring(v.lastIndexOf("/") + 1)

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

    @When("^after at least (\\d+) seconds\$")
    def after_at_least_x_seconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
            assert false: 'Sleep interrupted'
        }
    }

    @Then("^the service provides the following result:\$")
    public void the_service_provides_the_following_results(DataTable expectedResult) {
        String amount
        println 'the_service_provides_the_following_results'
        for (int i = 1; i < expectedResult.raw().size() + 1; i++) {
            String row = expectedResult.raw.get(i - 1)

            String[] column_data = row.split(",")

            println i

            def dateXpath = '//*[@id="results"]/tbody/tr[' + (i + 1) + ']/td[1]'
            String dateText = driver.findElement(By.xpath(dateXpath)).getText()
            println dateText
            assert column_data[0].contains(dateText)

            def amountXpath = '//*[@id="results"]/tbody/tr[' + (i + 1) + ']/td[3]'

            amount = column_data[2] + "," + column_data[3]
            println "Amount --------->" + amount
            assert amount.contains(driver.findElement(By.xpath(amountXpath)).getText())
            println "amountXpath :" + driver.findElement(By.xpath(amountXpath)).getText()
        }
    }

    @Then("^the service displays the following message:\$")
    public void the_service_displays_the_following_message(DataTable arg1) {

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        entries.each { k, v ->
            assert driver.findElement(By.id(k)).getText() == v
        }

    }

    @Then("^the service provides the following Your search results:\$")
    public void the_service_provides_the_following_Your_search_results(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForTable(expectedResult)
    }

    @Then("^the service displays the following page content\$")
    public void the_service_displays_the_following_page_content(DataTable expectedResult) throws Throwable {
        assertTextFieldEqualityForTable(expectedResult)
    }

    @Then("^the service provides the following NINO does not exist result:\$")
    public void the_service_provides_the_following_NINO_does_not_exist_result(DataTable expectedResult) {
        assertTextFieldEqualityForTable(expectedResult)
    }

    @Then("^the service displays the following page content within (\\d+) seconds\$")
    public void the_service_displays_the_following_page_content_within_seconds(long timeout, DataTable expectedResult) throws Throwable {
        driver.manage().timeouts().implicitlyWait(timeout, SECONDS)
        assertTextFieldEqualityForTable(expectedResult)
        driver.manage().timeouts().implicitlyWait(defaultTimeout, SECONDS)
    }

    @Then("^the connection attempt count should be (\\d+)\$")
    def the_connection_attempt_count_should_be_count(int count) {
        testDataLoader.verifyGetCount(count, incomeUriRegex.replaceFirst("nino", defaultNino))
    }

    @Then("^the error summary list contains the text\$")
    public void the_error_summary_list_contains_the_text(DataTable expectedText) {

        List<String> errorSummaryTextItems = expectedText.asList(String.class)

        WebElement errorSummaryList = driver.findElement(By.id("error-summary-list"))
        def errorText = errorSummaryList.text

        errorSummaryTextItems.each {
            assert errorText.contains(it): "Error text did not contain: $it"
        }
    }

    @Then("^the inputs will be populated with\$")
    public void the_inputs_will_be_populated_with(DataTable expectedResult) {
        assertInputValueEqualityForTable(expectedResult)
    }

    @Then("^the readiness response status should be (\\d+)\$")
    def the_readiness_response_status_should_be(int expected) {
        assertStatusMatchFor("healthz", expected)
    }

    @Then("^the liveness response status should be (\\d+)\$")
    def the_liveness_response_status_should_be(int expected) {
        assertStatusMatchFor("ping", expected)
    }

    def assertStatusMatchFor(String endpoint, int expected){

        def result = responseStatusFor(rootUrl + endpoint)

        // Sometimes needs a retry, not sure why
        2.times {
            if (result != expected) {
                sleep(500)
                result = responseStatusFor(rootUrl + endpoint)
            }
        }

        assert result == expected
    }

}