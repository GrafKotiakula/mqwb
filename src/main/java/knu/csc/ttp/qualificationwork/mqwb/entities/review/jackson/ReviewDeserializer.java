package knu.csc.ttp.qualificationwork.mqwb.entities.review.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.jackson.RatingDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;

@JsonComponent
public class ReviewDeserializer extends AbstractEntityDeserializer<Review> {
    private final String ratingFieldName = "rating";
    private final RatingDeserializer ratingDeserializer;

    @Autowired
    public ReviewDeserializer(ApplicationContext context) {
        super(context, Review.class);
        this.ratingDeserializer = context.getBean(RatingDeserializer.class);
    }

    @Override
    public Review deserialize(JsonNode root, Review review) {
        review = super.deserialize(root, review);

        if(!root.has(ratingFieldName)){
            throw LoggerUtils.debugException(logger, BadRequestException.propertyNotPresented(ratingFieldName));
        }
        JsonNode node = root.get(ratingFieldName);
        if(node.isNull()) {
            review.setRating(null);
        } else {
            review.setRating(ratingDeserializer.deserialize(node, review.getRating()));
        }

        return review;
    }
}
