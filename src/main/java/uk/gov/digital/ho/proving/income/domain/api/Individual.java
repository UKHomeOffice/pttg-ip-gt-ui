package uk.gov.digital.ho.proving.income.domain.api;

/**
 * Created by andrewmoores on 13/01/2016.
 */
public class Individual {
    private String title;
    private String forename;
    private String surname;
    private String nino;

    public Individual(String title, String forename, String surname, String nino) {
        this.title = title;
        this.forename = forename;
        this.surname = surname;
        this.nino = nino;
    }

    public Individual() {
    }

    public String getTitle() {
        return title;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public String getNino() {
        return nino;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "title='" + title + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                ", nino='" + nino + '\'' +
                '}';
    }
}
