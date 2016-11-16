package uk.gov.digital.ho.proving.income

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.income.domain.ResponseDetails

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import static java.time.LocalDateTime.now
import static java.time.temporal.ChronoUnit.MINUTES
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @Author Home Office Digital
 */
@ContextConfiguration
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = [ServiceRunner.class],
        properties = [
                "api.root=http://localhost:8989"
        ])
class AuditIntegrationSpec extends Specification {

    def path = "/incomeproving/v1/individual/QQ023987A/income?"
    def params = "fromDate=2014-12-01&toDate=2015-01-01"
    def url

    @Autowired
    TestRestTemplate restTemplate

    def apiServerMock
    def incomeUrlRegex = "/incomeproving/v1/individual/QQ023987A/income*"

    @Autowired
    AuditEventRepository auditEventRepository

    Appender logAppender = Mock()

    def setup() {
        url = path + params

        apiServerMock = new WireMockTestDataLoader(8989)

        withMockLogAppender()
    }

    def cleanup() {
        apiServerMock.stop()
    }

    def withMockLogAppender() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logAppender);
    }

    def successResponses() {
        apiServerMock.stubTestData("QQ023987A", incomeUrlRegex)
    }


    def "Searches are audited as INFO level log output with AUDIT prefix and SEARCH type with a timestamp"() {

        given:

        List<LoggingEvent> logEntries = []

        _ * logAppender.doAppend(_) >> { arg ->

            if (arg[0].formattedMessage.contains("AUDIT")) {
                logEntries.add(arg[0])
            }
        }

        when:
        restTemplate.getForEntity(url, ResponseDetails.class)
        LoggingEvent logEntry = logEntries[0]

        then:

        logEntry.level == Level.INFO

        // We can capture the SEARCH event log even though the search fails because there is no mongo

        logEntry.formattedMessage.contains("principal=anonymous")
        logEntry.formattedMessage.contains("type=SEARCH")
        logEntry.formattedMessage.contains("method=check-income")

        LocalDateTime timestamp =
                Instant.ofEpochMilli(logEntry.timeStamp).atZone(ZoneId.systemDefault()).toLocalDateTime();

        MINUTES.between(timestamp, now()) < 1;
    }


}
