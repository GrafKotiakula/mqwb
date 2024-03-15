package knu.csc.ttp.qualificationwork.mqwb.entities.image;

import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.UnprocessableEntityException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageValidator extends AbstractEntityValidator<Image> {
    public ImageValidator() {
        super(Image.class);
    }

    public void validateFileBytes(byte[] bytes, String parameter) {
        try (InputStream stream = new ByteArrayInputStream(bytes)){
            ImageIO.read(stream);
        } catch (IOException ex){
            throw LoggerUtils.logException(logger, defaultLogLvl,
                    UnprocessableEntityException.fileIsNotAnImage(parameter, ex));
        }
    }
}
