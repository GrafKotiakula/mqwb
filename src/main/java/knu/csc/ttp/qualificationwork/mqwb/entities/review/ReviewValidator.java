package knu.csc.ttp.qualificationwork.mqwb.entities.review;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.RatingValidator;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidator extends AbstractEntityValidator<Review> {
    private final RatingValidator ratingValidator;

    public ReviewValidator(RatingValidator ratingValidator) {
        super(Review.class);
        this.ratingValidator = ratingValidator;
    }

    @Override
    public void validate(Review review, String validationGroup) {
        super.validate(review, validationGroup);
        ratingValidator.validate(review.getRating(), validationGroup);
    }
}
