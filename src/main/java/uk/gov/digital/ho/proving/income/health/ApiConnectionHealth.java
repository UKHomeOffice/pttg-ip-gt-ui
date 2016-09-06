package uk.gov.digital.ho.proving.income.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author Home Office Digital
 */
@Component
public class ApiConnectionHealth implements HealthIndicator {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiConnectionHealth.class);

    @Value("${api.root}")
    private String apiRoot;

    @Value("${api.healthcheck.endpoint}")
    private String apiEndpoint;

    private UrlConnectionTester tester = new UrlConnectionTester();

    @Override
    public Health health() {

        int responseCode = tester.getResponseCodeFor(apiRoot + apiEndpoint);

        LOGGER.debug("Healthcheck response was: {}", responseCode);

        if (responseCode != 200) {
            return Health.down().withDetail("While trying to read income proving service, received:", responseCode).build();
        }

        return Health.up().withDetail("The income proving service API is responding with:", "UP").build();
    }


    public static class UrlConnectionTester {

        public int getResponseCodeFor(String uri){

            LOGGER.debug("Healthcheck calling API URI: {}", uri.toString());

            try {
                URL url = new URL(uri);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                return connection.getResponseCode();

            } catch (Exception e) {

                LOGGER.warn("Healthcheck failed with exception: {}", e.getMessage(), e);
                return 0;
            }
        }
    }
}
