package knu.csc.ttp.qualificationwork.mqwb.entities.image.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;

public class ImageSerializer extends AbstractEntitySerializer<Image> {
    public ImageSerializer() {
        super(Image.class);
    }
}
