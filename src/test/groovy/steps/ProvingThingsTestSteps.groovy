package steps

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

import java.text.SimpleDateFormat

@SpringApplicationConfiguration(ServiceRunner.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("test")
class ProvingThingsTestSteps {

    def incomeUriRegex = "/incomeproving/v1/individual/nino/income"

    //http://localhost:8080/incomeproving/v1/individual/QQ123456A/income?fromDate=2015-01-01&toDate=2015-06-30

    def testDataLoader

    @Value('${wiremock}')
    private Boolean wiremock;

    @Managed
    public WebDriver driver;

    private int delay = 500

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
        driver.get("http://localhost:8001");
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


    @Then("^The service provides the following result:\$")
    public void the_service_provides_the_following_results(DataTable expectedResult) {
        String amount
        for (int i = 1; i < expectedResult.raw().size() + 1; i++) {
            String row = expectedResult.raw.get(i - 1)

            String[] column_data = row.split(",")

            def dateXpath = '//*[@id="page2"]/table[2]/tbody[' + i + ']/tr/td[1]'
            assert column_data[0].contains(driver.findElement(By.xpath(dateXpath)).getText())
            println "dateXpath: " + driver.findElement(By.xpath(dateXpath)).getText()

            def amountXpath = '//*[@id="page2"]/table[2]/tbody[' + i + ']/tr/td[3]'

            amount = column_data[2] + "," + column_data[3]
            println "Amount --------->" + amount
            assert amount.contains(driver.findElement(By.xpath(amountXpath)).getText())
            println "amountXpath :" + driver.findElement(By.xpath(amountXpath)).getText()
        }
    }

    @Then("^The service displays the following message:\$")
    public void the_service_displays_the_following_message(DataTable arg1) {

        Map<String, String> entries = arg1.asMap(String.class, String.class)

        entries.each { k, v ->
            assert driver.findElement(By.id(k)).getText() == v
        }

    }

    @Then("^The service provides the following Your search results:\$")
    public void the_service_provides_the_following_Your_search_results(DataTable expectedResult) throws Throwable {

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)

        entries.each{ k, v ->

            def elementText = driver.findElement(By.id(toCamelCase(k))).getText()
            assert elementText.contains(v)

          //  println "$k: $elementText"
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

    @Then("^The service provides the following NINO does not exist result:\$")
    public void the_service_provides_the_following_NINO_does_not_exist_result(DataTable expectedResult) {

        Map<String, String> entries = expectedResult.asMap(String.class, String.class)

        entries.each { k, v ->

            def elementText = driver.findElement(By.id(toCamelCase(k))).getText()
            assert elementText.contains(v)

            //  println "$k: $elementText"
        }
    }

}