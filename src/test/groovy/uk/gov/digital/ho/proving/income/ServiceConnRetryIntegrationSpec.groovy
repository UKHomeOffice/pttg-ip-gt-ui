package uk.gov.digital.ho.proving.income

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Timeout
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.income.domain.ResponseDetails

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * @Author Home Office Digital
 */
@SpringApplicationConfiguration(classes = ServiceRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(properties = [
    "api.root=http://localhost:8989",
    "rest.connection.connection-request-timeout=200",
    "rest.connection.connect-timeout=200",
    "rest.connection.read-timeout=200",
    "connectionRetryDelay=200",
    "connectionAttemptCount=3"
])
class ServiceConnRetryIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    def port

    def path = "/incomeproving/v1/individual/AA121212A/income?"
    def params = "fromDate=2014-12-01&toDate=2015-01-01"
    def url

    RestTemplate restTemplate

    def apiServerMock
    def incomeUrlRegex = "/incomeproving/v1/individual/AA121212A/income*"

    def setup() {
        restTemplate = new TestRestTemplate()
        url = "http://localhost:" + port + path + params

        apiServerMock = new WireMockTestDataLoader(8989)
    }

    @Timeout(value = 4, unit = SECONDS) // ensure it doesn't accidentally run forever...
    def 'retries API calls when Connection timeout'() {

        given:
        apiServerMock.withDelayedResponse(incomeUrlRegex, 2)

        when:
        restTemplate.getForEntity(url, ResponseDetails.class)

        then:
        verify(3, getRequestedFor(urlPathMatching(incomeUrlRegex)))
    }
}
