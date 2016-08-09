package uk.gov.digital.ho.proving.income

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.income.audit.AuditEventType
import uk.gov.digital.ho.proving.income.domain.api.ApiResponse
import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail
import uk.gov.digital.ho.proving.income.domain.api.Individual
import uk.gov.digital.ho.proving.income.exception.ServiceExceptionHandler
import uk.gov.digital.ho.proving.income.integration.RestServiceErrorHandler

import java.time.format.DateTimeFormatter

import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.core.AllOf.allOf
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.StringContains.containsString
import static org.springframework.http.HttpStatus.BAD_GATEWAY
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class ServiceSpec extends Specification {

    final String API_ENDPOINT = "/incomeproving/v1/individual/{nino}/income"
    final String UI_ENDPOINT = "/incomeproving/v1/individual/{nino}/income"
    final String NINO = "AA123456A"
    final String FROM_DATE = "2016-12-12"
    final String TO_DATE = "2016-12-12"
    final String BAD_DATE = "7567/4545"
    final String PAY_DATE_MONTH1 = "23/04/2016"
    final String PAY_DATE_MONTH2 = "23/04/2016"
    final String EMPLOYER_NAME = "Nice Employer"
    final String PAYMENT_AMOUNT = "2000"
    final String PAYMENT_TOTAL = "4000"
    final String FORENAME = "Jo"
    final String SURNAME = "Madness"
    final String MR = "Mr"

    ObjectMapper mapper = new ServiceConfiguration().getMapper()

    MockMvc mockMvc
    MockRestServiceServer mockServer

    ApplicationEventPublisher auditor = Mock()

    def service = new Service()

    def setup() {

        service.apiRoot = ''
        service.apiEndpoint = API_ENDPOINT

        RestTemplate restTemplate = new RestTemplate()
        restTemplate.errorHandler = new RestServiceErrorHandler();

        mockServer = MockRestServiceServer.createServer(restTemplate);

        mockMvc = standaloneSetup(service)
                .setMessageConverters(createMessageConverter())
                .setControllerAdvice(new ServiceExceptionHandler())
                .alwaysDo(print())
                .build()

        service.restTemplate = restTemplate
        service.auditor = auditor
    }

    def MappingJackson2HttpMessageConverter createMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter()
        converter.setObjectMapper(new ServiceConfiguration().getMapper())
        converter
    }

    def apiRespondsWith(responseString) {
        mockServer.expect(requestTo(containsString("incomeproving")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(responseString);
    }

    def "processes valid request and response"() {

        given:
        def apiResultJson = mapper.writeValueAsString(buildResponse(true))
        apiRespondsWith(
                withSuccess(apiResultJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isOk())
            andExpect(content().contentType(APPLICATION_JSON_VALUE))
            andExpect(jsonPath("individual.forename", is(FORENAME)))
            andExpect(jsonPath("incomes", hasSize(2)))
        }
    }

    def "handles nino exists - no incomes"() {

        given:
        def apiResultJson = mapper.writeValueAsString(buildResponse(false))
        apiRespondsWith(
                withSuccess(apiResultJson, APPLICATION_JSON)
        )

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isOk())
            andExpect(content().contentType(APPLICATION_JSON_VALUE))
            andExpect(jsonPath("individual.forename", is(FORENAME)))
            andExpect(jsonPath("incomes", hasSize(0)))
        }
    }

    def "reports errors for missing mandatory parameters"() {

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0001")))
            andExpect(jsonPath("message", allOf(
                    containsString("Missing parameter"),
                    containsString("fromDate"))))
        }
    }

    def "invalid to date is rejected"() {

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', BAD_DATE)
        )
        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0002")))
            andExpect(jsonPath("message", allOf(
                    containsString("Invalid parameter"),
                    containsString("toDate"))))
        }
    }

    @Unroll
    def "invalid nino of #nino is rejected"() {

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, nino)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isBadRequest())
            andExpect(jsonPath("code", is("0003")))
            andExpect(jsonPath("message", allOf(
                    containsString("Invalid parameter format"),
                    containsString("nino"))))
        }

        where:
        nino << ["AD", "0000", "123AAAA4567", "AA123456E"]
    }

    def "reports remote server error from api as internal error"() {

        given:
        apiRespondsWith(
                withServerError()
        )

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0006")))
        }
    }

    def "when 404 at API, returns 404 "() {

        given:
        apiRespondsWith(
                withStatus(NOT_FOUND)
        )

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isNotFound())
        }
    }

    def 'handles unexpected HTTP status from API server'() {

        given:
        apiRespondsWith(
                withStatus(BAD_GATEWAY)
        )

        when:
        def response = mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:
        response.with {
            andExpect(status().isInternalServerError())
            andExpect(jsonPath("code", is("0005")))
            andExpect(jsonPath("message", containsString("API response status")))
            andExpect(jsonPath("message", containsString(BAD_GATEWAY.toString())))
        }
    }

    def 'audits search inputs and response'() {

        given:
        def apiResultJson = mapper.writeValueAsString(buildResponse(true))
        apiRespondsWith(
                withSuccess(apiResultJson, APPLICATION_JSON)
        )

        AuditEvent event1
        AuditEvent event2
        1 * auditor.publishEvent(_) >> {args -> event1 = args[0].auditEvent}
        1 * auditor.publishEvent(_) >> {args -> event2 = args[0].auditEvent}

        when:
        mockMvc.perform(
                get(UI_ENDPOINT, NINO)
                        .param('fromDate', FROM_DATE)
                        .param('toDate', TO_DATE)
        )

        then:

        event1.type == AuditEventType.SEARCH.name()
        event2.type == AuditEventType.SEARCH_RESULT.name()

        event1.data['eventId'] == event2.data['eventId']

        event1.data['fromDate'] == FROM_DATE
        event1.data['toDate'] == TO_DATE
        event1.data['nino'] == NINO

        event2.data['response'].total == PAYMENT_TOTAL
    }

    def ApiResponse buildResponse(boolean includeIncomes) {
        ApiResponse response = new ApiResponse()
        response.setIncomes([])
        if (includeIncomes) {
            IncomeDetail id1 = new IncomeDetail(PAY_DATE_MONTH1, EMPLOYER_NAME, PAYMENT_AMOUNT)
            IncomeDetail id2 = new IncomeDetail(PAY_DATE_MONTH2, EMPLOYER_NAME, PAYMENT_AMOUNT)
            def incomes = [id1, id2]
            response.setIncomes(incomes)
        }
        response.setTotal(PAYMENT_TOTAL)
        response.setIndividual(new Individual(MR, FORENAME, SURNAME, NINO))
        response
    }
}
