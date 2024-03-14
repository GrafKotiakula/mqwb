package knu.csc.ttp.qualificationwork.mqwb.user;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractCrudController;
import knu.csc.ttp.qualificationwork.mqwb.user.jackson.UserDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.user.jpa.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/data/user")
public class UserController extends AbstractCrudController<User, UserService, UserValidator> {
    protected GrantedAuthority updateByIdAuthority = Role.Authority.UPDATE_USER.getAuthority();
    protected GrantedAuthority updatePasswordByIdAuthority = Role.Authority.UPDATE_USER_PASSWORD.getAuthority();

    @Autowired
    public UserController(ApplicationContext context) {
        super(context);
        createAuthority = Role.Authority.CREATE_USER.getAuthority();
        deleteAuthority = Role.Authority.DELETE_USER.getAuthority();
    }

    @Override
    public User parseEntityOnCreate(JsonNode json) {
        User user = super.parseEntityOnCreate(json);
        user.setPassword(UserDeserializer.getPassword(json));
        return user;
    }

    private User update(User user, JsonNode json) {
        user = parseEntityOnUpdate(user, json);
        validator.validate(user, UserValidator.UPDATE);
        return service.update(user);
    }

    private User updatePassword(User user, JsonNode json) {
        user.setPassword(UserDeserializer.getPassword(json));
        validator.validate(user, UserValidator.PASSWORD_UPDATE);
        return service.updatePassword(user);
    }

    @GetMapping
    public User getByAuth(@AuthenticationPrincipal User user) {
        return user;
    }

    @PutMapping
    public User updateByAuth(@AuthenticationPrincipal User user, @RequestBody JsonNode json) {
        return update(user, json);
    }

    @Override
    @PutMapping("/{id}")
    public User updateById(@PathVariable("id") UUID id, @RequestBody JsonNode json) {
        checkAuthority(updateByIdAuthority);
        User user = service.findByIdOrThrow(id);
        return update(user, json);
    }

    // TODO add endpoints for user blocking and role granting

    @PutMapping("/password")
    public User updatePasswordByAuth(@AuthenticationPrincipal User user, @RequestBody JsonNode json) {
        return updatePassword(user, json);
    }

    @PutMapping("/{id}/password")
    public User updatePasswordById(@PathVariable("id") UUID id, @RequestBody JsonNode json) {
        checkAuthority(updatePasswordByIdAuthority);
        return updatePassword(service.findByIdOrThrow(id), json);
    }
}
