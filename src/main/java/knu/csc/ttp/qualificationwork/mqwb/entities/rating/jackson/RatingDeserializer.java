package knu.csc.ttp.qualificationwork.mqwb.entities.rating.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;

@JsonComponent
public class RatingDeserializer extends AbstractEntityDeserializer<Rating> {
    @Autowired
    public RatingDeserializer(ApplicationContext context) {
        super(context, Rating.class);
    }
}
