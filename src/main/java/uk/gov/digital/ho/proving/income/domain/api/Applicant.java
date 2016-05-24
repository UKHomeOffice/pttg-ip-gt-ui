package uk.gov.digital.ho.proving.income.domain.api;

/**
 * Created by andrewmoores on 13/01/2016.
 */
public class Applicant {
    private String forename;
    private String surname;
    private String nino;

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNino() {
        return nino;
    }

    public void setNino(String nino) {
        this.nino = nino;
    }
}
