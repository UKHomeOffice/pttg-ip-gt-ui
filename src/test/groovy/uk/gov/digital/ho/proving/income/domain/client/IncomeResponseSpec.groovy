package uk.gov.digital.ho.proving.income.domain.client

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.domain.api.ApiResponse

/**
 * @Author Home Office Digital
 */
class IncomeResponseSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new IncomeResponse()
        instance.setStatus("status")

        when:
        def output = instance.toString()

        then:
        output.contains("status='$instance.status'")

        and:
        !output.contains('IncomeResponse@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(IncomeResponse).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}
