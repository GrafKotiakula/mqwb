package knu.csc.ttp.qualificationwork.mqwb.exceptions.client;

import jakarta.servlet.http.HttpServletRequest;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractEntity;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.Review;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

import java.util.UUID;

public class NotFoundException extends RequestException {
    public NotFoundException(Integer code, String clientMessage, String message, Throwable cause) {
        super(code, clientMessage, message, cause);
    }

    public NotFoundException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.notFoundBaseCode;
    }

    public static NotFoundException handlerNotFoundException(HttpServletRequest request) {
        return handlerNotFoundException(request, null);
    }

    public static NotFoundException handlerNotFoundException(HttpServletRequest request, Throwable cause) {
        return new NotFoundException(0, String.format("Unknown uri %s", request.getRequestURI()),
                String.format("No handler found for %s", request.getRequestURI()), cause);
    }

    public static NotFoundException idNotFound(Class<? extends AbstractEntity> entity, UUID id) {
        return idNotFound(entity, id, null);
    }

    public static NotFoundException idNotFound(Class<? extends AbstractEntity> entity, UUID id, Throwable cause) {
        return new NotFoundException(1, String.format("%s with id %s not found", entity.getSimpleName(), id),
                String.format("%s[id=%s] not found", entity.getSimpleName(), id), cause);
    }

    public static NotFoundException reviewNotFoundByGameAndUser(Game game, User user) {
        return reviewNotFoundByGameAndUser(game, user, null);
    }

    public static NotFoundException reviewNotFoundByGameAndUser(Game game, User user, Throwable cause) {
        return new NotFoundException(2,
                String.format("%s for %s by %s not found", Review.class.getSimpleName(), game, user),
                cause);
    }
}

