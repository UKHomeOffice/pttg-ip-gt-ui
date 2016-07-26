package uk.gov.digital.ho.proving.income.domain.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * @Author Home Office Digital
 */
public final class Nino {

    @NotNull(message = "Missing parameter")
    @Pattern(regexp = "^[a-zA-Z]{2}[0-9]{6}[a-dA-D]{1}$", message = "Invalid parameter format")
    private String nino;

    @JsonCreator
    public Nino(@JsonProperty("nino") String nino) {
        this.nino = nino;
    }

    public String getNino() {
        return nino;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nino nino1 = (Nino) o;
        return Objects.equals(nino, nino1.nino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nino);
    }

    @Override
    public String toString() {
        return "Nino{" +
                "nino='" + nino + '\'' +
                '}';
    }
}
