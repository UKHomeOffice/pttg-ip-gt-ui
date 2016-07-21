package uk.gov.digital.ho.proving.income;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.domain.api.APIResponse;
import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail;
import uk.gov.digital.ho.proving.income.domain.api.Individual;
import uk.gov.digital.ho.proving.income.exception.ServiceExceptionHandler;
import uk.gov.digital.ho.proving.income.integration.RestServiceErrorHandler;

import java.net.URI;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class ServiceTest {


    public static final String API_ROOT = "http://localhost:8081";
    public static final String API_ENDPOINT = "/incomeproving/v1/individual/{nino}/income";
    public static final String NINO = "AA";
    public static final String FROM_DATE = "2016-12-12";
    public static final String TO_DATE = "2016-12-12";
    public static final String BAD_DATE = "7567/4545";
    public static final String PAY_DATE_MONTH1 = "23/04/2016";
    public static final String PAY_DATE_MONTH2 = "23/04/2016";
    public static final String EMPLOYER_NAME = "Nice Employer";
    public static final String PAYMENT_AMOUNT = "2000";
    public static final String PAYMENT_TOTAL = "4000";
    public static final String FORENAME = "Jo";
    public static final String SURNAME = "Madness";
    public static final String MR = "Mr";

    private MockMvc mockMvc;

    @Mock
    private Client mockClient;

    @Mock
    private WebResource webResource;

    @Mock
    private WebResource.Builder mockBuilder;


    private MockRestServiceServer mockServer;

    ObjectMapper mapper = new ServiceConfiguration().getMapper();

    @Before
    public void setup() {
        Service service = new Service();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestServiceErrorHandler());
        mockServer = MockRestServiceServer.createServer(restTemplate);

        service.setRestTemplate(restTemplate);

        ReflectionTestUtils.setField(service, "apiRoot", API_ROOT);
        ReflectionTestUtils.setField(service, "apiEndpoint", API_ENDPOINT);

        mockMvc = standaloneSetup(service)
                .setMessageConverters(createMessageConverter())
                .setControllerAdvice(new ServiceExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    private  MappingJackson2HttpMessageConverter createMessageConverter() {
        org.springframework.http.converter.json.MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ServiceConfiguration().getMapper());
        return converter;
    }

    @Test
    public void checkIncome() throws Exception {
        String apiResultJson = mapper.writeValueAsString(buildResponse());
        final URI url = withUri(NINO, FROM_DATE, TO_DATE);
        withSuccessResponse(url, apiResultJson);

        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.individual.forename").value(FORENAME))
                .andExpect(jsonPath("$.incomes", hasSize(2))).andReturn();
    }

    @Test
    public void checkIncomeNinoExistsNoIncomes() throws Exception {
        String apiResultJson = mapper.writeValueAsString(buildResponseNoIncomes());
        final URI url = withUri(NINO, FROM_DATE, TO_DATE);
        withSuccessResponse(url, apiResultJson);
        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.individual.forename").value(FORENAME))
                .andExpect(jsonPath("$.incomes", hasSize(0))).andReturn();
    }


    @Test
    public void checkIncomeBadDate() throws Exception {
        URI url = withUri(NINO, BAD_DATE, TO_DATE);

        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void checkIncomeMissingNinoNotFound() throws Exception {
        URI url = withUri("AA999999A", FROM_DATE, TO_DATE);
        //does not match URL without nino (local API not matched)
        withNotFoundResponse(url);

        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isNotFound());

    }

    private URI withUri(String nino, String from, String to) {
        return UriComponentsBuilder.fromUriString(API_ROOT + API_ENDPOINT).queryParam("fromDate", from).queryParam("toDate", to).buildAndExpand(nino).toUri();
    }


    private void withSuccessResponse(URI url, String response) {
        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.APPLICATION_JSON));

    }


    private void withNotFoundResponse(URI url) {
        mockServer.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

    }

    private APIResponse buildResponse(){
        APIResponse response = new APIResponse();
        IncomeDetail id1 = new IncomeDetail(PAY_DATE_MONTH1, EMPLOYER_NAME, PAYMENT_AMOUNT);
        IncomeDetail id2 = new IncomeDetail(PAY_DATE_MONTH2, EMPLOYER_NAME, PAYMENT_AMOUNT);
        IncomeDetail[] incomes = {id1, id2};
        response.setIncomes(incomes);
        response.setTotal(PAYMENT_TOTAL);
        response.setIndividual(new Individual(MR, FORENAME, SURNAME, NINO));
        return response;
    }

    private APIResponse buildResponseNoIncomes(){
        APIResponse response = new APIResponse();
        response.setIncomes(new IncomeDetail[0]);
        response.setTotal(PAYMENT_TOTAL);
        response.setIndividual(new Individual(MR, FORENAME, SURNAME, NINO));
        return response;
    }

}