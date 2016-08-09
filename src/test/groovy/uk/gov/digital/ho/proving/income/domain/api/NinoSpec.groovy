package uk.gov.digital.ho.proving.income.domain.api

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

/**
 * @Author Home Office Digital
 */
class NinoSpec extends Specification {

    def "generates meaningful toString instead of just a hash"() {

        given:
        def instance = new Nino("AA123456A")

        when:
        def output = instance.toString()

        then:
        output.contains("nino='$instance.nino'")

        and:
        !output.contains('Nino@')
    }

    def 'has valid hashcode and equals'() {

        when:
        EqualsVerifier.forClass(Nino).suppress(Warning.NONFINAL_FIELDS).verify()

        then:
        noExceptionThrown()
    }
}