package uk.gov.digital.ho.proving.income

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Timeout
import uk.gov.digital.ho.proving.income.domain.ResponseDetails

import static java.util.concurrent.TimeUnit.SECONDS
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @Author Home Office Digital
 */
@ContextConfiguration
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = [ServiceRunner.class],
        properties = [
                "api.root=http://10.255.255.1",
                "rest.connection.connect-timeout=500",
                "connectionRetryDelay=500",
                "connectionAttemptCount=2"
        ])
class ServiceConnTimeoutIntegrationSpec extends Specification {

    def path = "/incomeproving/v1/individual/AA121212A/income?"
    def params = "fromDate=2014-12-01&toDate=2015-01-01"
    def url

    @Autowired
    TestRestTemplate restTemplate

    def setup() {
        url = path + params
    }

    @Timeout(value = 4, unit = SECONDS)
    def 'obeys timeout on slow connection response'() {

        given:
        // we have set the api host to an unresolvable address, which means that the rest call to the API won't return
        // we have also set the connect-timeout property to 1/2 second

        when:
        def entity = restTemplate.getForEntity(url, ResponseDetails.class)

        then:
        entity.getBody().message.contains("connect timed out")
        // If the connection timeout settings aren't being obeyed, then this test will fail when the @Timeout is exceeded
    }


}
