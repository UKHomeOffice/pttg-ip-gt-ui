package uk.gov.digital.ho.proving.income.domain.client;

import uk.gov.digital.ho.proving.income.domain.api.Applicant;
import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail;

/**
 * Created by andrewmoores on 10/02/2016.
 */
public class IncomeResponse {
    private String status;
    private IncomeDetail[] incomes;
    private String total;
    private Applicant applicant;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public IncomeDetail[] getIncomes() {
        return incomes;
    }

    public void setIncomes(IncomeDetail[] incomes) {
        this.incomes = incomes;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
}
