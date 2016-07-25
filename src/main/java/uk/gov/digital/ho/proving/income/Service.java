package uk.gov.digital.ho.proving.income;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.domain.api.APIResponse;
import uk.gov.digital.ho.proving.income.domain.api.Nino;
import uk.gov.digital.ho.proving.income.domain.client.IncomeResponse;

import javax.validation.Valid;
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

    @Retryable(interceptor = "connectionExceptionInterceptor")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity checkIncome(@Valid Nino nino,
                                      @RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                      @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {


        LOGGER.debug("CheckIncome: Nino - {} From Date - {} To Date - {}", nino.getNino(), fromDate, toDate);

        APIResponse apiResult = restTemplate.exchange(buildUrl(nino.getNino(), toDate, fromDate), GET, entity(), APIResponse.class).getBody();

        LOGGER.debug("Api result: {}", apiResult.toString());

        IncomeResponse response = new IncomeResponse();
        response.setIncomes(apiResult.getIncomes());
        response.setTotal(apiResult.getTotal());
        response.setIndividual(apiResult.getIndividual());

        return ResponseEntity.ok(response);
    }

    private URI buildUrl(String nino, LocalDate toDate, LocalDate fromDate) {
        return UriComponentsBuilder
                .fromUriString(apiRoot + apiEndpoint)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .buildAndExpand(nino).toUri();
    }

    private HttpEntity<Object> entity() {
        return new HttpEntity<>(getHeaders());
    }

    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(asList(MediaType.APPLICATION_JSON));

        return headers;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}