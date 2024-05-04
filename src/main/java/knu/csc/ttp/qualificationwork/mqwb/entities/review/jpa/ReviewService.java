package knu.csc.ttp.qualificationwork.mqwb.entities.review.jpa;

import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.jpa.RatingService;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.NotFoundException;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReviewService extends AbstractService<Review, ReviewRepository> {
    private RatingService ratingService;

    @Autowired
    public ReviewService(ReviewRepository repository, RatingService ratingService) {
        super(repository);
        this.ratingService = ratingService;
        defaultSort = Sort.by(Sort.Direction.DESC, "date");
    }

    public Page<Review> getAllByGame(Game game, int page) {
        return repository.getAllByGame(game, pageableOf(page));
    }

    public Page<Review> getAllByUser(User user, int page) {
        return repository.getAllByUser(user, pageableOf(page));
    }

    public Review findByGameAndUserOrThrow(Game game, User user) {
        return repository.getByGameAndUser(game, user)
                .orElseThrow( () -> LoggerUtils.debugException(logger,
                NotFoundException.reviewNotFoundByGameAndUser(game, user)) );
    }

    @Override
    public Review create(Review review, Level logLevel) {
        if(review.getGame() == null) {
            throw new NullPointerException("Game is null");
        }
        review.setRating( ratingService.create(review.getRating(), Level.TRACE) );
        review = super.create(review, logLevel);

        ratingService.updateGameAvgs(review.getGame(), Level.TRACE);

        return review;
    }

    @Override
    public Review update(Review review, Level logLevel) {
        review.setRating( ratingService.update(review.getRating(), Level.TRACE) );
        review = super.update(review, logLevel);

        ratingService.updateGameAvgs(review.getGame(), Level.TRACE);

        return review;
    }

    @Override
    public void delete(Review review) {
        super.delete(review);
        ratingService.delete(review.getRating(), Level.TRACE);
        ratingService.updateGameAvgs(review.getGame(), Level.TRACE);
    }
}
