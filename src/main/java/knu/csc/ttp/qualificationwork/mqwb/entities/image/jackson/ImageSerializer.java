package knu.csc.ttp.qualificationwork.mqwb.entities.image.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class ImageSerializer extends AbstractEntitySerializer<Image> {
    public ImageSerializer() {
        super(Image.class);
    }
}
