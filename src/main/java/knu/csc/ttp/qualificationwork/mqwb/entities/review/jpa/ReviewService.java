package knu.csc.ttp.qualificationwork.mqwb.entities.review.jpa;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.jpa.RatingService;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService extends AbstractService<Review, ReviewRepository> {
    private RatingService ratingService;

    @Autowired
    public ReviewService(ReviewRepository repository, RatingService ratingService) {
        super(repository);
        this.ratingService = ratingService;
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
    public Review update(Review review) {
        review.setRating( ratingService.update(review.getRating(), Level.TRACE) );
        review = super.update(review);

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
