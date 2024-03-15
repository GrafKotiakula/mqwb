package knu.csc.ttp.qualificationwork.mqwb.entities.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.jackson.ImageDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.jackson.ImageSerializer;

import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "images")
@JsonSerialize(using = ImageSerializer.class)
@JsonDeserialize(using = ImageDeserializer.class)
public class Image extends AbstractEntity {
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "alternate", length = 100)
    @NotBlank
    private String alternate;

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getAlternate() {
        return alternate;
    }

    @JsonProperty
    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }
}
