package uk.gov.digital.ho.proving.income.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ApiResponse implements Serializable {

    private Individual individual;
    private List<IncomeDetail> incomes;
    private String total;

    public ApiResponse() {
    }

    public Individual getIndividual() { return individual;}

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public List getIncomes() { return incomes;}

    public void setIncomes(List incomes) { this.incomes = incomes;}


    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "APIResponse{" +
                "individual=" + individual +
                ", incomes=" + incomes +
                ", total='" + total + '\'' +
                '}';
    }

}
