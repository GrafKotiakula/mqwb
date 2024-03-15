package knu.csc.ttp.qualificationwork.mqwb.entities.image.jpa;

import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InsufficientStorageException;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ImageService extends AbstractService<Image, ImageRepository> {
    private final Path filesDir;

    @Autowired
    public ImageService(ImageRepository repository, @Value("${data.image.file.path}") String filesDir) {
        super(repository);
        this.filesDir = Paths.get(filesDir);
        if(!Files.isDirectory(this.filesDir)) {
            try {
                Files.createDirectories(this.filesDir);
                logger.info("Directory {} to store files is created", this.filesDir.toString());
            } catch (IOException ex) {
                throw LoggerUtils.fatalException(logger,
                        InsufficientStorageException.cannotCreateFilesDirectory(this.filesDir, ex) );
            }
        }
    }

    protected Path getFilePath(Image image) {
        return filesDir.resolve(image.getId().toString());
    }

    public byte[] readFile(Image image){
        try {
            return Files.readAllBytes(getFilePath(image));
        } catch (IOException ex) {
            throw LoggerUtils.errorException(logger,
                    InsufficientStorageException.cannotReadFile(image, ex));
        }
    }

    public byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch(IOException ex){
            throw LoggerUtils.warnException(logger, InsufficientStorageException.cannotLoadBytes(ex));
        }
    }

    @Transactional(rollbackFor = InsufficientStorageException.class)
    public Image create(Image image, byte[] bytes, Level logLevel) {
        image = repository.save(image);
        try {
            Files.write(getFilePath(image), bytes);
        } catch (IOException ex){
            throw LoggerUtils.errorException(logger,
                    InsufficientStorageException.cannotSaveFile(ex));
        }
        logger.log(Optional.ofNullable(logLevel).orElse(defaultCreateLogLvl),
                "{} is created by {}", image, getAuthenticatedUser().orElseGet(User::new));
        return image;
    }

    @Transactional(rollbackFor = InsufficientStorageException.class)
    public Image create(Image image, byte[] bytes) {
        return create(image, bytes, defaultCreateLogLvl);
    }

    @Override
    public void delete(Image image, Level logLevel) {
        try {
            Path filePath = getFilePath(image);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            LoggerUtils.errorException(logger,
                    InsufficientStorageException.cannotRemoveFile(image, ex));
        }
        super.delete(image, logLevel);
    }
}
