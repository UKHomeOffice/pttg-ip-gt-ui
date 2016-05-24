package uk.gov.digital.ho.proving.income;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;
import uk.gov.digital.ho.proving.income.domain.*;
import uk.gov.digital.ho.proving.income.domain.ResponseStatus;
import uk.gov.digital.ho.proving.income.domain.api.APIResponse;
import uk.gov.digital.ho.proving.income.domain.client.IncomeRequest;
import uk.gov.digital.ho.proving.income.domain.client.IncomeResponse;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/incomeproving/v1/individual/{nino}/income")
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private Client client = Client.create();

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.endpoint}")
    private String apiEndpoint;


    //todo confirm we dont need application/json;charset=UTF-8
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity checkIncome(@PathVariable(value = "nino") String nino,
                                      @RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                      @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {


        LOGGER.info("CheckIncome: Nino - {} From Date - {} To Date - {}", nino, fromDate, toDate);

        IncomeResponse response = new IncomeResponse();

        client.setConnectTimeout(10000);

        WebResource webResource = buildUrl(nino, toDate, fromDate);

        ClientResponse clientResponse = webResource.accept("application/json").header("content-type", MediaType.APPLICATION_JSON).get(ClientResponse.class);

        APIResponse apiResult = clientResponse.getEntity(APIResponse.class);

        LOGGER.info(apiResult.toString());

        response.setStatus(clientResponse.getStatusInfo().getReasonPhrase());

        if (clientResponse.getStatusInfo().getReasonPhrase().equalsIgnoreCase("404")) {
            response.setStatus("unknown resource");
        } else {
            if (apiResult != null && apiResult.getIncomes() != null) {
                response.setIncomes(apiResult.getIncomes());
                response.setTotal(apiResult.getTotal()); //@TODO where is the total calculated?
                response.setApplicant(apiResult.getApplicant());
            }
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    private WebResource buildUrl(String nino, LocalDate toDate, LocalDate fromDate) {
        final URI expanded = UriComponentsBuilder.fromUriString(apiRoot+apiEndpoint).queryParam("fromDate", fromDate).queryParam("toDate", toDate).buildAndExpand(nino).toUri();
        LOGGER.debug(expanded.toString());
        return client.resource(expanded);
    }


    private ResponseEntity<ResponseStatus> buildErrorResponse(HttpHeaders headers, String errorCode, String errorMessage, HttpStatus status) {
        ResponseStatus response = new ResponseStatus(errorCode, errorMessage);
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object missingParamterHandler(MissingServletRequestParameterException exception) {
        LOGGER.debug(exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        return buildErrorResponse(headers, "0008", "Missing parameter: " + exception.getParameterName() , HttpStatus.BAD_REQUEST);
    }

    protected void setClient(Client client) {
        this.client = client;
    }
}