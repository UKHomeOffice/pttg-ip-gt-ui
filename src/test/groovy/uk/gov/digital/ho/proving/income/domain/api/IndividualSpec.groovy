package uk.gov.digital.ho.proving.income.domain.api

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

/**
 * @Author Home Office Digital
 */
class IndividualSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new Individual("mr", "first", "last", "nino")

        when:
        def output = instance.toString()

        then:
        output.contains("forename='$instance.forename'")

        and:
        !output.contains('Individual@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(Individual).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}

