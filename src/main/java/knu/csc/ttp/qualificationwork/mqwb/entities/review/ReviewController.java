package knu.csc.ttp.qualificationwork.mqwb.entities.review;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.AuthUtils;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractCrudController;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.jpa.GameService;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.jpa.ReviewService;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.jpa.UserService;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/data/review")
public class ReviewController extends AbstractCrudController<Review, ReviewService, ReviewValidator> {

    protected GameService gameService;
    protected UserService userService;

    protected GrantedAuthority findAllByGameAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();
    protected GrantedAuthority findByGameAndUserAuthority = Role.Authority.ANONYMOUS_READ.getAuthority();
    protected GrantedAuthority findByGameMyAuthority = Role.Authority.READ.getAuthority();

    @Autowired
    public ReviewController(ApplicationContext context) {
        super(context);

        gameService = context.getBean(GameService.class);
        userService = context.getBean(UserService.class);

        updateAuthority = Role.Authority.UPDATE_REVIEW.getAuthority();
        deleteAuthority = Role.Authority.DELETE_REVIEW.getAuthority();
    }

    @Override
    public Review parseEntityOnCreate(JsonNode json) {
        Review review = super.parseEntityOnCreate(json);
        review.setUser(AuthUtils.getAuthenticatedUser().orElse(null)); // User should always be authenticated
        return review;
    }

    protected void checkAuthorityOrOwner(User owner, GrantedAuthority authority) {
        AuthUtils.getAuthenticatedUser()
                .filter(u -> u.getId().equals(owner.getId())
                        || u.getAuthorities().contains(authority))
                .orElseThrow( () -> LoggerUtils.warnException(logger, ForbiddenException
                        .noRequiredAuthority(SecurityContextHolder.getContext().getAuthentication(), authority)) );
    }

    @GetMapping("/game/{id}/all")
    public Page<Review> findAllReviewsByGame(@PathVariable("id") String strId,
                                             @RequestParam(value = "page", defaultValue = "0") Integer page) {
        checkAuthority(findAllByGameAuthority);
        Game game = gameService.findByIdOrThrow(convertToUUID(strId, "gameId"));
        return service.getAllByGame(game, page);
    }

    @GetMapping("/game/{id}/my")
    public Review findReviewByGameAndAuth(@AuthenticationPrincipal User user, @PathVariable("id") String strId) {
        checkAuthority(findByGameMyAuthority);
        Game game = gameService.findByIdOrThrow(convertToUUID(strId, "gameId"));
        return service.findByGameAndUserOrThrow(game, user);
    }

    @GetMapping("/user/{id}/all")
    public Page<Review> findAllReviewsByUser(@PathVariable("id") String strId,
                                             @RequestParam(value = "page", defaultValue = "0") Integer page) {
        checkAuthority(findAllByGameAuthority);
        User user = userService.findByIdOrThrow(convertToUUID(strId, "gameId"));
        return service.getAllByUser(user, page);
    }

    @GetMapping("/game/{gameId}/{userId}")
    public Review findReviewByGameAndUser(@PathVariable("gameId") String strGameId, @PathVariable("userId") String strUserId) {
        checkAuthority(findByGameAndUserAuthority);
        Game game = gameService.findByIdOrThrow(convertToUUID(strGameId, "gameId"));
        User user = userService.findByIdOrThrow(convertToUUID(strUserId, "userId"));
        return service.findByGameAndUserOrThrow(game, user);
    }

    @Override
    @PutMapping("/{id}")
    public Review updateById(@PathVariable("id") String strId, JsonNode json) {
        UUID id = convertToUUID(strId, "id");

        Review review = service.findByIdOrThrow(id);
        checkAuthorityOrOwner(review.getUser(), updateAuthority);

        review = parseEntityOnUpdate(review, json);
        validator.validate(review, ReviewValidator.UPDATE);
        review = service.update(review);

        return review;
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(String strId) {
        UUID id = convertToUUID(strId, "id");

        service.findById(id).ifPresent(review -> {
            checkAuthorityOrOwner(review.getUser(), updateAuthority);
            service.delete(review);
        });

        return ResponseEntity.noContent().build();
    }
}
