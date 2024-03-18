package knu.csc.ttp.qualificationwork.mqwb.entities.rating.jpa;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService extends AbstractService<Rating, RatingRepository> {
    @Autowired
    public RatingService(RatingRepository repository) {
        super(repository);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Rating updateGameAvgs(Game game, Level logLevel) {
        Rating newRating = repository.calculateAverageForGame(game);
        newRating.setId( Optional.ofNullable(game.getAvgRating()).map(AbstractEntity::getId).orElse(null) );
        newRating = save(newRating, logLevel);

        game.setAvgRating(newRating);
        return newRating;
    }
}
