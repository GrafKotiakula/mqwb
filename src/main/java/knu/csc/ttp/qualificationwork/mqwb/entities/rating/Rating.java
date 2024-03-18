package knu.csc.ttp.qualificationwork.mqwb.entities.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "ratings")
public class Rating extends AbstractEntity {
    @Column(name = "main_rating", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal mainRating;

    @Column(name = "graphics", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal graphics;

    @Column(name = "audio", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal audio;

    @Column(name = "story", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal story;

    @Column(name = "game_time", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal gameTime;

    @Column(name = "gameplay", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal gameplay;

    @Column(name = "difficulty", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal difficulty;

    @Column(name = "grind", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal grind;

    @Column(name = "requirements", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal requirements;

    @Column(name = "bugs", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal bugs;

    @Column(name = "price", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal price;

    @Column(name = "microtransactions", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal microtransactions;

    @Column(name = "mods", precision = 3, scale = 1, nullable = false)
    @Min(1) @Max(10)
    private BigDecimal mods;

    public Rating() {
        this(0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.);
    }

    public Rating(BigDecimal mainRating, BigDecimal graphics, BigDecimal audio, BigDecimal story, BigDecimal gameTime,
                  BigDecimal gameplay, BigDecimal difficulty, BigDecimal grind, BigDecimal requirements,
                  BigDecimal bugs, BigDecimal price, BigDecimal microtransactions, BigDecimal mods) {
        this.mainRating = mainRating;
        this.graphics = graphics;
        this.audio = audio;
        this.story = story;
        this.gameTime = gameTime;
        this.gameplay = gameplay;
        this.difficulty = difficulty;
        this.grind = grind;
        this.requirements = requirements;
        this.bugs = bugs;
        this.price = price;
        this.microtransactions = microtransactions;
        this.mods = mods;
    }

    public Rating(Double mainRating, Double graphics, Double audio, Double story, Double gameTime, Double gameplay,
                  Double difficulty, Double grind, Double requirements, Double bugs, Double price,
                  Double microtransactions, Double mods) {
        this.mainRating = new BigDecimal(mainRating).setScale(1, RoundingMode.CEILING);
        this.graphics = new BigDecimal(graphics).setScale(1, RoundingMode.CEILING);
        this.audio = new BigDecimal(audio).setScale(1, RoundingMode.CEILING);
        this.story = new BigDecimal(story).setScale(1, RoundingMode.CEILING);
        this.gameTime = new BigDecimal(gameTime).setScale(1, RoundingMode.CEILING);
        this.gameplay = new BigDecimal(gameplay).setScale(1, RoundingMode.CEILING);
        this.difficulty = new BigDecimal(difficulty).setScale(1, RoundingMode.CEILING);
        this.grind = new BigDecimal(grind).setScale(1, RoundingMode.CEILING);
        this.requirements = new BigDecimal(requirements).setScale(1, RoundingMode.CEILING);
        this.bugs = new BigDecimal(bugs).setScale(1, RoundingMode.CEILING);
        this.price = new BigDecimal(price).setScale(1, RoundingMode.CEILING);
        this.microtransactions = new BigDecimal(microtransactions).setScale(1, RoundingMode.CEILING);
        this.mods = new BigDecimal(mods).setScale(1, RoundingMode.CEILING);
    }

    @JsonProperty
    public BigDecimal getMainRating() {
        return mainRating;
    }

    @JsonProperty
    public void setMainRating(BigDecimal mainRating) {
        this.mainRating = mainRating;
    }

    @JsonProperty
    public BigDecimal getGraphics() {
        return graphics;
    }

    @JsonProperty
    public void setGraphics(BigDecimal graphics) {
        this.graphics = graphics;
    }

    @JsonProperty
    public BigDecimal getAudio() {
        return audio;
    }

    @JsonProperty
    public void setAudio(BigDecimal audio) {
        this.audio = audio;
    }

    @JsonProperty
    public BigDecimal getStory() {
        return story;
    }

    @JsonProperty
    public void setStory(BigDecimal story) {
        this.story = story;
    }

    @JsonProperty
    public BigDecimal getGameTime() {
        return gameTime;
    }

    @JsonProperty
    public void setGameTime(BigDecimal gameTime) {
        this.gameTime = gameTime;
    }

    @JsonProperty
    public BigDecimal getGameplay() {
        return gameplay;
    }

    @JsonProperty
    public void setGameplay(BigDecimal gameplay) {
        this.gameplay = gameplay;
    }

    @JsonProperty
    public BigDecimal getDifficulty() {
        return difficulty;
    }

    @JsonProperty
    public void setDifficulty(BigDecimal difficulty) {
        this.difficulty = difficulty;
    }

    @JsonProperty
    public BigDecimal getGrind() {
        return grind;
    }

    @JsonProperty
    public void setGrind(BigDecimal grind) {
        this.grind = grind;
    }

    @JsonProperty
    public BigDecimal getRequirements() {
        return requirements;
    }

    @JsonProperty
    public void setRequirements(BigDecimal requirements) {
        this.requirements = requirements;
    }

    @JsonProperty
    public BigDecimal getBugs() {
        return bugs;
    }

    @JsonProperty
    public void setBugs(BigDecimal bugs) {
        this.bugs = bugs;
    }

    @JsonProperty
    public BigDecimal getPrice() {
        return price;
    }

    @JsonProperty
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty
    public BigDecimal getMicrotransactions() {
        return microtransactions;
    }

    @JsonProperty
    public void setMicrotransactions(BigDecimal microtransactions) {
        this.microtransactions = microtransactions;
    }

    @JsonProperty
    public BigDecimal getMods() {
        return mods;
    }

    @JsonProperty
    public void setMods(BigDecimal mods) {
        this.mods = mods;
    }

    // removes JsonProperty annotation
    @Override
    public UUID getId() {
        return super.getId();
    }
}
