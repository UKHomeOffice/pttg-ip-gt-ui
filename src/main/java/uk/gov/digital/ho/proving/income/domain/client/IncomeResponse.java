package uk.gov.digital.ho.proving.income.domain.client;

import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail;
import uk.gov.digital.ho.proving.income.domain.api.Individual;

/**
 * Created by andrewmoores on 10/02/2016.
 */
public class IncomeResponse {
    private String status;
    private IncomeDetail[] incomes;
    private String total;
    private Individual individual;

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

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }
}
