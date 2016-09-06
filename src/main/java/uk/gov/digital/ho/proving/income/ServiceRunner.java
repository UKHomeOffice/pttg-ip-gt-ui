package uk.gov.digital.ho.proving.income;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class ServiceRunner {

    private static Logger LOGGER = LoggerFactory.getLogger(ServiceRunner.class);

    //@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //System.setProperty("javax.net.ssl.trustStore", "/data/truststore.jks");
        return application.sources(ServiceRunner.class);
    }

    public static void main(String[] args) throws Exception {
        LOGGER.debug("starting pttg-ip-gt-ui");

        run(ServiceRunner.class, args);
    }

}
