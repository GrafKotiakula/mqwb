package knu.csc.ttp.qualificationwork.mqwb.entities.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.jackson.CompanyDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.jackson.CompanySerializer;

import javax.validation.constraints.Pattern;

@Entity
@Table(name = "companies")
@JsonSerialize(using = CompanySerializer.class)
@JsonDeserialize(using = CompanyDeserializer.class)
public class Company extends AbstractEntity {

    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Pattern(regexp = Constants.prettyLineRegexp, message = Constants.prettyLineMessage)
    private String name;

    @Column(name = "developed_amount", nullable = false)
    private Integer developedGamesAmount = 0;

    @Column(name = "published_amount", nullable = false)
    private Integer publishedGamesAmount = 0;

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public Integer getDevelopedGamesAmount() {
        return developedGamesAmount;
    }

    public void setDevelopedGamesAmount(Integer developedGamesAmount) {
        this.developedGamesAmount = developedGamesAmount;
    }

    @JsonProperty
    public Integer getPublishedGamesAmount() {
        return publishedGamesAmount;
    }

    public void setPublishedGamesAmount(Integer publishedGamesAmount) {
        this.publishedGamesAmount = publishedGamesAmount;
    }
}
