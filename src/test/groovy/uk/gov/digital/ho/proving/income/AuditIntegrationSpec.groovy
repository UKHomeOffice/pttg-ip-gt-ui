package uk.gov.digital.ho.proving.income

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Ignore
import spock.lang.Specification
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.income.domain.ResponseDetails

import static java.time.LocalDateTime.now
import static java.time.LocalDateTime.parse
import static java.time.temporal.ChronoUnit.MINUTES

/**
 * @Author Home Office Digital
 */
@SpringApplicationConfiguration(classes = ServiceRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(properties = [
    "api.root=http://localhost:8989"
])
@Ignore
class AuditIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    def port

    def path = "/incomeproving/v1/individual/QQ023987A/income?"
    def params = "fromDate=2014-12-01&toDate=2015-01-01"
    def url

    RestTemplate restTemplate

    def apiServerMock
    def incomeUrlRegex = "/incomeproving/v1/individual/QQ023987A/income*"

    @Autowired
    AuditEventRepository auditEventRepository

    Appender logAppender = Mock()

    def setup() {
        restTemplate = new TestRestTemplate()
        url = "http://localhost:" + port + path + params

        apiServerMock = new WireMockTestDataLoader(8989)

        withMockLogAppender()
    }

    def cleanup(){
        apiServerMock.stop()
    }

    def withMockLogAppender() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logAppender);
    }

    def successResponses() {
        apiServerMock.stubTestData("QQ023987A", incomeUrlRegex)
    }


    def "Searches are audited as INFO level log output with AUDIT prefix in json format and SEARCH type with a timestamp"() {

        given:
        successResponses()

        List<LoggingEvent> logEntries = []

        _ * logAppender.doAppend(_) >> { arg ->
            if (arg[0].formattedMessage.contains("AUDIT")) {
                logEntries.add(arg[0])
            }
        }

        when:
        restTemplate.getForEntity(url, ResponseDetails.class)
        LoggingEvent logEntry = logEntries[0]
        def logEntryJson = new JsonSlurper().parseText(logEntry.formattedMessage - "AUDIT:")

        then:

        logEntries.size >= 1
        logEntry.level == Level.INFO

        logEntryJson.principal == "anonymous"
        logEntryJson.type == "SEARCH"
        logEntryJson.data.method == "check-income"

        MINUTES.between(parse(logEntryJson.timestamp), now()) < 1;
    }


}
