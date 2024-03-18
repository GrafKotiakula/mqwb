package knu.csc.ttp.qualificationwork.mqwb.entities.image.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;

@JsonComponent
public class ImageDeserializer extends AbstractEntityDeserializer<Image> {
    @Autowired
    public ImageDeserializer(ApplicationContext context) {
        super(context, Image.class);
    }
}
