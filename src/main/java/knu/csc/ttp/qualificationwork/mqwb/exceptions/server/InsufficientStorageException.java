package knu.csc.ttp.qualificationwork.mqwb.exceptions.server;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

import java.nio.file.Path;

public class InsufficientStorageException extends RequestException {
    public InsufficientStorageException(String message, Throwable cause) {
        super(0, "Insufficient storage error", message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.insufficientStorageBaseCode;
    }

    public static InternalServerErrorException cannotCreateFilesDirectory(Path dirPath, Throwable cause) {
        return new InternalServerErrorException(
                String.format("Failed to create directory %s to store files", dirPath.toString()), cause);
    }

    public static InsufficientStorageException cannotLoadBytes(Throwable cause) {
        return new InsufficientStorageException("Failed to load a file", cause);
    }

    public static InsufficientStorageException cannotSaveFile(Throwable cause) {
        return new InsufficientStorageException("Failed to save a file", cause);
    }

    public static InsufficientStorageException cannotReadFile(Image image, Throwable cause) {
        return new InsufficientStorageException( String.format("Filed to read file %s", image.toString()), cause );
    }

    public static InsufficientStorageException cannotRemoveFile(Image image, Throwable cause) {
        return new InsufficientStorageException( String.format("Filed to remove file %s", image.toString()), cause );
    }
}
