package knu.csc.ttp.qualificationwork.mqwb.entities.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;

import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;

@Entity
@Table(name = "reviews")
public class Review extends AbstractEntity {

    @Column(name = "text", length = 5000)
    @NotBlank
    private String text;

    @Column(name = "date",  nullable = false)
    private ZonedDateTime date = ZonedDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @OneToOne
    @JoinColumn(name = "rating_id", nullable = false, unique = true)
    private Rating rating = new Rating();

    @JsonProperty
    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @JsonProperty
    public String getText() {
        return text;
    }

    @JsonProperty
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty
    public Game getGame() {
        return game;
    }

    @JsonProperty
    public void setGame(Game game) {
        this.game = game;
    }

    @JsonProperty
    public Rating getRating() {
        return rating;
    }

    // explicitly sets in RatingDeserializer
    public void setRating(Rating rating) {
        this.rating = rating;
    }
}
