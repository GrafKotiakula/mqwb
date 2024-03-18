package knu.csc.ttp.qualificationwork.mqwb.entities.review.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class ReviewSerializer extends AbstractEntitySerializer<Review> {
    public ReviewSerializer() {
        super(Review.class);

    }
}
