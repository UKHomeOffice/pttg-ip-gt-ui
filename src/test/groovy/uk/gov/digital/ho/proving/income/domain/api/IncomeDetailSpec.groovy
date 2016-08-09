package uk.gov.digital.ho.proving.income.domain.api

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification
import uk.gov.digital.ho.proving.income.domain.client.IncomeResponse

/**
 * @Author Home Office Digital
 */
class IncomeDetailSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new IncomeDetail()
        instance.setEmployer("employer")

        when:
        def output = instance.toString()

        then:
        output.contains("employer='$instance.employer'")

        and:
        !output.contains('IncomeDetail@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(IncomeDetail).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}
