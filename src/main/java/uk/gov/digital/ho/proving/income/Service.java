package uk.gov.digital.ho.proving.income;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.domain.ResponseDetails;
import uk.gov.digital.ho.proving.income.domain.api.APIResponse;
import uk.gov.digital.ho.proving.income.domain.client.IncomeResponse;

import java.net.URI;
import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpMethod.GET;

@RestController
@RequestMapping("/incomeproving/v1/individual/{nino}/income")
@ControllerAdvice
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);


    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.endpoint}")
    private String apiEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    ObjectMapper mapper = new ServiceConfiguration().getMapper();

    //todo confirm we dont need application/json;charset=UTF-8
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity checkIncome(@PathVariable(value = "nino") String nino,
                                      @RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                      @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {


        LOGGER.debug("CheckIncome: Nino - {} From Date - {} To Date - {}", nino, fromDate, toDate);

        APIResponse apiResult = restTemplate.exchange(buildUrl(nino, toDate, fromDate), GET, new HttpEntity<>(getHeaders()), APIResponse.class).getBody();

        LOGGER.debug(apiResult.toString());

        IncomeResponse response = new IncomeResponse();
        response.setIncomes(apiResult.getIncomes());
        response.setTotal(apiResult.getTotal());
        response.setIndividual(apiResult.getIndividual());

        return ResponseEntity.ok(response);
    }

    private URI buildUrl(String nino, LocalDate toDate, LocalDate fromDate) {
        return UriComponentsBuilder.fromUriString(apiRoot + apiEndpoint).queryParam("fromDate", fromDate).queryParam("toDate", toDate).buildAndExpand(nino).toUri();
    }


    private ResponseEntity<ResponseDetails> buildErrorResponse(HttpHeaders headers, String errorCode, String errorMessage, HttpStatus status) {
        ResponseDetails response = new ResponseDetails(errorCode, errorMessage);
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object missingParamterHandler(MissingServletRequestParameterException exception) {
        LOGGER.debug(exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        return buildErrorResponse(headers, "0008", "Missing parameter: " + exception.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setAccept(asList(org.springframework.http.MediaType.APPLICATION_JSON));

        return headers;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}