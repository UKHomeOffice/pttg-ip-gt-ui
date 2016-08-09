package uk.gov.digital.ho.proving.income.domain.client;

import uk.gov.digital.ho.proving.income.domain.api.ApiResponse;
import uk.gov.digital.ho.proving.income.domain.api.IncomeDetail;
import uk.gov.digital.ho.proving.income.domain.api.Individual;

import java.util.List;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
public final class IncomeResponse {

    private String status;
    private List<IncomeDetail> incomes;
    private String total;
    private Individual individual;

    public IncomeResponse(){

    }

    public IncomeResponse(ApiResponse apiResult) {
        this.incomes = apiResult.getIncomes();
        this.total = apiResult.getTotal();
        this.individual = apiResult.getIndividual();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getIncomes() {
        return incomes;
    }

    public void setIncomes(List incomes) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomeResponse that = (IncomeResponse) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(incomes, that.incomes) &&
                Objects.equals(total, that.total) &&
                Objects.equals(individual, that.individual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, incomes, total, individual);
    }

    @Override
    public String toString() {
        return "IncomeResponse{" +
                "status='" + status + '\'' +
                ", incomes=" + incomes +
                ", total='" + total + '\'' +
                ", individual=" + individual +
                '}';
    }
}
