package knu.csc.ttp.qualificationwork.mqwb.entities.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;

import javax.validation.constraints.Pattern;

@Entity
@Table(name = "companies")
public class Company extends AbstractEntity {

    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Pattern(regexp = Constants.prettyLineRegexp, message = Constants.prettyLineMessage)
    private String name;

    @Column(name = "developed_amount", nullable = false)
    private Long developedGamesAmount = 0L;

    @Column(name = "published_amount", nullable = false)
    private Long publishedGamesAmount = 0L;

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public Long getDevelopedGamesAmount() {
        return developedGamesAmount;
    }

    public void setDevelopedGamesAmount(Long developedGamesAmount) {
        this.developedGamesAmount = developedGamesAmount;
    }

    @JsonProperty
    public Long getPublishedGamesAmount() {
        return publishedGamesAmount;
    }

    public void setPublishedGamesAmount(Long publishedGamesAmount) {
        this.publishedGamesAmount = publishedGamesAmount;
    }
}
