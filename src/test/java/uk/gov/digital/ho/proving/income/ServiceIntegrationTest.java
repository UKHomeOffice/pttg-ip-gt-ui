package uk.gov.digital.ho.proving.income;


import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@PropertySource("classpath:application.properties")
public class ServiceIntegrationTest {

    private MockRestServiceServer mockServer;

    private RestTemplate restTemplate;

    @Autowired
    private Service service = new Service();


    @Before
    public void setUp() {
        //restTemplate = new RestTemplate();
       // mockServer = MockRestServiceServer.createServer(restTemplate);
    }


    @Test
    public void happypathTodo() throws Exception {
        /*mockServer.expect(requestTo("http://localhost:4567/incomeproving/v1/individual/AA121212A/income"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("resultSuccess", MediaType.TEXT_PLAIN));

        service.checkIncome("AA121212A", LocalDate.now(), LocalDate.now());*/


    }


}
