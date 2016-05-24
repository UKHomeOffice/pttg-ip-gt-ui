package uk.gov.digital.ho.proving.income.domain.client;

/**
 * Created by andrewmoores on 10/02/2016.
 */
public class IncomeRequest {
    private String nino;
    private String fromDate; // Date
    private String toDate;    // Date

    public String getNino() {
        return nino;
    }

    public void setNino(String nino) {
        this.nino = nino;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
