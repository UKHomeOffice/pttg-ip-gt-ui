package steps

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.Fault
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class WireMockTestDataLoader {

    private static Logger LOGGER = LoggerFactory.getLogger(WireMockTestDataLoader.class);

    def dataDirName = 'test-data'

    def WireMockServer wireMockServer

    WireMockTestDataLoader() {
        this(8080)
    }

    WireMockTestDataLoader(int port) {
        wireMockServer = new WireMockServer(port)
        wireMockServer.start()
        WireMock.configureFor("127.0.0.1", port);

        LOGGER.debug("")
        LOGGER.debug("")
        LOGGER.debug("")
        LOGGER.debug("STARTED Wiremock server. Running = {}", wireMockServer.running)
        LOGGER.debug("")
        LOGGER.debug("")
        LOGGER.debug("")
    }

    def stubTestData(String fileName, String url) {

        def json = jsonFromFile(fileName)

        LOGGER.debug("Loaded json from file: {}", json)

        if (json == null) {
            assert false: "No test data file was loaded for $fileName from directory $dataDirName - " +
                    "Please add it or check filename is correct"
        }

        addStub(fileName, json, url)

    }

    private def jsonFromFile(String fileName) {

        println ''
        def fileLocation = "/$dataDirName/$fileName" + ".json"
        LOGGER.debug("Loading test data for {}", fileLocation.toString())

        def file = this.getClass().getResource(fileLocation)

        if (file == null) {
            return null
        }

        return file.text
    }

    private def addStub(String fileName, String json, String url) {

        println ''
        LOGGER.debug("Stubbing Response data with $fileName")

        stubFor(get(urlPathMatching(url))
                .willReturn(
                aResponse()
                        .withBody(json)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));

        println ''
        LOGGER.debug("Completed Stubbing Response data with $fileName")
    }

    def stubErrorData(String fileName, String url, int status) {

        println ''
        LOGGER.debug("Stubbing error Response data with $fileName")

        def json = jsonFromFile(fileName)

        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                .withBody(json)
                .withHeader("Content-Type", "application/json")
                .withStatus(status)));

        println ''
        LOGGER.debug("Completed Stubbing error Response data with $fileName")

    }

    def withDelayedResponse(String url, int delay) {

        println ''
        LOGGER.debug("Stubbing delayed response for $url of $delay seconds")

        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                .withFixedDelay(delay * 1000)
                .withStatus(200)));

        println ''
        LOGGER.debug("Completed stubbing delayed response")
    }

    def withGarbageResponse(String url) {

        println ''
        LOGGER.debug("Stubbing garbage response for $url")

        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        println ''
        LOGGER.debug("Completed stubbing garbage response")
    }

    def withEmptyResponse(String url) {

        println ''
        LOGGER.debug("Stubbing empty response for $url")

        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                .withFault(Fault.EMPTY_RESPONSE)));

        println ''
        LOGGER.debug("Completed stubbing empty response")
    }

    def withResponseStatus(String url, int status) {

        println ''
        LOGGER.debug("Stubbing response for $url with status $status")

        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")));

        println ''
        LOGGER.debug("Completed stubbing response with status")
    }

    def withServiceDown() {
        stop()
    }

    def stop() {
        wireMockServer.stop()
    }

    def verifyGetCount(int count, String url){
        verify(count, getRequestedFor(urlPathMatching(url)))
    }
}
