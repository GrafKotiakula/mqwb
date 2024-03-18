package knu.csc.ttp.qualificationwork.mqwb.entities.rating;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import org.springframework.stereotype.Component;

@Component
public class RatingValidator extends AbstractEntityValidator<Rating> {
    public RatingValidator() {
        super(Rating.class);
    }
}
