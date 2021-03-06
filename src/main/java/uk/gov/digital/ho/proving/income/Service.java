package uk.gov.digital.ho.proving.income;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.digital.ho.proving.income.audit.AuditActions;
import uk.gov.digital.ho.proving.income.domain.api.ApiResponse;
import uk.gov.digital.ho.proving.income.domain.api.Nino;
import uk.gov.digital.ho.proving.income.domain.client.IncomeResponse;
import uk.gov.digital.ho.proving.income.health.ApiAvailabilityChecker;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.HttpMethod.GET;
import static uk.gov.digital.ho.proving.income.audit.AuditActions.auditEvent;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH;
import static uk.gov.digital.ho.proving.income.audit.AuditEventType.SEARCH_RESULT;

@RestController
@RequestMapping("/incomeproving/v1")
@ControllerAdvice
public class Service {

    private static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.endpoint}")
    private String apiEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationEventPublisher auditor;

    @Autowired
    private ApiAvailabilityChecker apiAvailabilityChecker;


    @Retryable(interceptor = "connectionExceptionInterceptor")
    @RequestMapping(path = "/individual/{nino}/income", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity checkIncome(@Valid Nino nino,
                                      @RequestParam(value = "fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                      @RequestParam(value = "toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                      @CookieValue(value = "kc-access", defaultValue = "") String accessToken) {

        LOGGER.debug("CheckIncome: Nino - {} From Date - {} To Date - {}", value("nino", nino.getNino()), fromDate, toDate);

        UUID eventId = AuditActions.nextId();
        auditor.publishEvent(auditEvent(SEARCH, eventId, auditData(nino, fromDate, toDate)));

        ApiResponse apiResult = restTemplate.exchange(buildUrl(nino.getNino(), toDate, fromDate), GET,
                addTokenToHeaders(entity(), accessToken), ApiResponse.class).getBody();

        LOGGER.debug("Api result: {}", value("checkIncomeApiResult", apiResult));

        IncomeResponse response = new IncomeResponse(apiResult);

        auditor.publishEvent(auditEvent(SEARCH_RESULT, eventId, auditData(response)));

        return ResponseEntity.ok(response);
    }

    private URI buildUrl(String nino, LocalDate toDate, LocalDate fromDate) {
        URI uri = UriComponentsBuilder
                .fromUriString(apiRoot + apiEndpoint)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .buildAndExpand(nino).toUri();

        LOGGER.debug("Constructed URI: {}", uri.toString());

        return uri;
    }

    private HttpEntity<Object> entity() {
        return new HttpEntity<>(getHeaders());
    }

    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private Map<String, Object> auditData(Nino nino, LocalDate fromDate, LocalDate toDate) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "check-income");
        auditData.put("nino", nino.getNino());
        auditData.put("fromDate", fromDate.format(DateTimeFormatter.ISO_DATE));
        auditData.put("toDate", toDate.format(DateTimeFormatter.ISO_DATE));

        return auditData;
    }

    private Map<String, Object> auditData(IncomeResponse response) {

        Map<String, Object> auditData = new HashMap<>();

        auditData.put("method", "check-income");
        auditData.put("response", response);

        return auditData;
    }

    private HttpEntity addTokenToHeaders(HttpEntity<?> entity, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(entity.getHeaders());
        headers.add("Cookie", "kc-access=" + accessToken);
        HttpEntity<?> newEntity = new HttpEntity<>(headers);
        return newEntity;
    }

    @RequestMapping(path = "availability", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity availability() {
        return apiAvailabilityChecker.check();
    }

}