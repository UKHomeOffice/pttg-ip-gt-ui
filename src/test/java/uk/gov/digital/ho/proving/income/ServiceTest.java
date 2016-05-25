package uk.gov.digital.ho.proving.income;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.domain.api.APIResponse;
import uk.gov.digital.ho.proving.income.domain.api.Individual;
import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by lbennett on 23/05/16.
 */
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

    @Mock
    private ClientResponse clientResponse;

    @Before
    public void setup() {
        Service service = new Service();
        service.setClient(mockClient);
        ReflectionTestUtils.setField(service, "apiRoot", API_ROOT);
        ReflectionTestUtils.setField(service, "apiEndpoint", API_ENDPOINT);
        mockMvc = MockMvcBuilders.standaloneSetup(service).build();
    }

    @Test
    public void checkIncome() throws Exception {
        final URI url = withUri(NINO, FROM_DATE, TO_DATE);
        withResponse(url, Response.Status.OK);
        withApiResult();

        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.individual.forename").value(FORENAME))
                .andExpect(jsonPath("$.incomes", hasSize(2))).andReturn();
    }

    @Test
    public void checkIncomeNinoExistsNoIncomes() throws Exception {
        final URI url = withUri(NINO, FROM_DATE, TO_DATE);
        withResponse(url, Response.Status.OK);
        withApiResultNoIncomes();
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

        withResponse(url, Response.Status.BAD_REQUEST);
        withApiResult();

        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isBadRequest());

    }

    @Test
    public void checkIncomeMissingNinoNotFound() throws Exception {
        URI url = withUri("", FROM_DATE, TO_DATE);
        //does not match URL without nino (local API not matched)
        withResponse(url, Response.Status.NOT_FOUND);

        this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .accept(MediaType.parseMediaType("application/json")))
                .andDo(print()).andExpect(status().isNotFound());

    }

    private URI withUri(String nino, String from, String to) {
        return UriComponentsBuilder.fromUriString(API_ROOT + API_ENDPOINT).queryParam("fromDate", from).queryParam("toDate", to).buildAndExpand(nino).toUri();
    }

    private void withApiResult() {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setIndividual(new Individual());
        Mockito.when(clientResponse.getEntity(APIResponse.class)).thenReturn(buildResponse());
    }

    private void withApiResultNoIncomes() {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setIndividual(new Individual());
        final APIResponse response = buildResponse();
        response.setIncomes(new IncomeDetail[0]);
        Mockito.when(clientResponse.getEntity(APIResponse.class)).thenReturn(response);
    }

    private void withResponse(URI url, Response.Status status) {
        Mockito.when(mockClient.resource(url)).thenReturn(webResource);
        Mockito.when(webResource.accept("application/json")).thenReturn(mockBuilder);
        Mockito.when(mockBuilder.header("content-type", "application/json")).thenReturn(mockBuilder);
        Mockito.when(mockBuilder.get(ClientResponse.class)).thenReturn(clientResponse);
        Mockito.when(clientResponse.getStatusInfo()).thenReturn(status);
        Mockito.when(clientResponse.getStatus()).thenReturn(status.getStatusCode());

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

}