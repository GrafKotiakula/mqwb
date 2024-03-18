package knu.csc.ttp.qualificationwork.mqwb.entities.review;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.AuthUtils;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractCrudController;
import knu.csc.ttp.qualificationwork.mqwb.entities.review.jpa.ReviewService;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/data/review")
public class ReviewController extends AbstractCrudController<Review, ReviewService, ReviewValidator> {

    @Autowired
    public ReviewController(ApplicationContext context) {
        super(context);
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

    @Override
    @PutMapping("/{id}")
    public Review updateById(String strId, JsonNode json) {
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
