package knu.csc.ttp.qualificationwork.mqwb.entities.rating.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class RatingSerializer extends AbstractEntitySerializer<Rating> {
    public RatingSerializer() {
        super(Rating.class);
    }
}
