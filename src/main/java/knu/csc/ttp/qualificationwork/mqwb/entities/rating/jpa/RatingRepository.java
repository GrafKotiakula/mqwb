package knu.csc.ttp.qualificationwork.mqwb.entities.rating.jpa;

import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    String ratingClassName = "knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating";
    String ratingAvgConstructor = " new " + ratingClassName + "( AVG(r.mainRating), AVG(r.graphics), AVG(r.audio)," +
            "AVG(r.story), AVG(r.gameTime), AVG(r.gameplay), AVG(r.difficulty), AVG(r.grind), AVG(r.requirements)," +
            "AVG(r.bugs), AVG(r.price), AVG(r.microtransactions), AVG(r.mods) ) ";

    @Query("SELECT " + ratingAvgConstructor + " FROM Review review INNER JOIN review.rating r WHERE review.game = ?1")
    Rating calculateAverageForGame(Game game);
}
