package uk.gov.digital.ho.proving.income.domain.api;

/**
 * Created by andrewmoores on 13/01/2016.
 */
public class Sponsor {
    private String nino;
    private boolean canSupportApplicant;
    private String applicantNino;

    public String getApplicantNino() {
        return applicantNino;
    }

    public void setApplicantNino(String applicantNino) {
        this.applicantNino = applicantNino;
    }

    public String getNino() {
        return nino;
    }

    public void setNino(String nino) {
        this.nino = nino;
    }

    public boolean isCanSupportApplicant() {
        return canSupportApplicant;
    }

    public void setCanSupportApplicant(boolean canSupportApplicant) {
        this.canSupportApplicant = canSupportApplicant;
    }
}
