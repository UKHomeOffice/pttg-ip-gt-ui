package uk.gov.digital.ho.proving.income.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by andrewmoores on 09/02/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResponse implements Serializable {
    private Individual individual;
    private IncomeDetail[] incomes;
    private Link[] links;
    private String total;

    public APIResponse() {
    }

    public Individual getIndividual() { return individual;}

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public IncomeDetail[] getIncomes() { return incomes;}

    public void setIncomes(IncomeDetail[] incomes) { this.incomes = incomes;}

    public Link[] getLinks() {
        return links;
    }

    public void setLinks(Link[] links) {
        this.links = links;
    }

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
                ", incomes=" + Arrays.toString(incomes) +
                ", links=" + Arrays.toString(links) +
                ", total='" + total + '\'' +
                '}';
    }

    //{"incomes":[{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/01/2015"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/12/2014"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/11/2014"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/10/2014"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/09/2014"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/08/2014"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/07/2014"},{"income":"1600.00","employer":"Flying Pizza Ltd","payDate":"15/06/2014"}],"individual":{"forename":"Jon","surname":"Taylor","nino":"AA345678A"},"payFreq":"M1"}
}