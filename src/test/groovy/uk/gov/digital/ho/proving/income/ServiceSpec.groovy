package uk.gov.digital.ho.proving.income

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpMethod
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.income.domain.api.APIResponse
import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail
import uk.gov.digital.ho.proving.income.domain.api.Individual
import uk.gov.digital.ho.proving.income.exception.ServiceExceptionHandler
import uk.gov.digital.ho.proving.income.integration.RestServiceErrorHandler

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
    final String NINO = "AA"
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
            andExpect(jsonPath("code", is("0008")))
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

    //TODO add validation to incoming path params
    @Ignore
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
                    containsString("sortCode"))))
        }

        where:
        nino << ["AD", "0000", "123AAAA4567"]
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

    def APIResponse buildResponse(boolean includeIncomes) {
        APIResponse response = new APIResponse()
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
