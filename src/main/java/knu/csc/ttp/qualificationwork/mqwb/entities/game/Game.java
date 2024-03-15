package knu.csc.ttp.qualificationwork.mqwb.entities.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.Company;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.jackson.GameDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.jackson.GameSerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;

import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;

@Entity
@Table(name = "games")
@JsonSerialize(using = GameSerializer.class)
@JsonDeserialize(using = GameDeserializer.class)
public class Game extends AbstractEntity {
    @Column(name = "name", length = 50, nullable = false, unique = true)
    @Pattern(regexp = Constants.prettyLineRegexp, message = Constants.prettyLineMessage)
    private String name;

    @Column(name = "release", nullable = false)
    private ZonedDateTime release;

    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Company developer;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Company publisher;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public ZonedDateTime getRelease() {
        return release;
    }

    @JsonProperty
    public void setRelease(ZonedDateTime release) {
        this.release = release;
    }

    @JsonProperty
    public Company getDeveloper() {
        return developer;
    }

    @JsonProperty
    public void setDeveloper(Company developer) {
        this.developer = developer;
    }

    @JsonProperty
    public Company getPublisher() {
        return publisher;
    }

    @JsonProperty
    public void setPublisher(Company publisher) {
        this.publisher = publisher;
    }

    @JsonProperty
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
