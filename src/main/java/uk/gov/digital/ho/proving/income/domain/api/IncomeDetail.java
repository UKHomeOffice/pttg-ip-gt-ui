package uk.gov.digital.ho.proving.income.domain.api;

/**
 * Created by andrewmoores on 09/02/2016.
 */
public class IncomeDetail {
    public String payDate;
    public String employer;
    public String income;

    public IncomeDetail() {
    }

    public IncomeDetail(String payDate, String employer, String income) {
        this.payDate = payDate;
        this.employer = employer;
        this.income = income;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "IncomeDetail{" +
                "payDate='" + payDate + '\'' +
                ", employer='" + employer + '\'' +
                ", income='" + income + '\'' +
                '}';
    }
}
