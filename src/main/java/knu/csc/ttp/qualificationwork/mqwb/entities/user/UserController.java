package knu.csc.ttp.qualificationwork.mqwb.entities.user;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractCrudController;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.ImageController;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.jackson.UserDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.jpa.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/data/user")
public class UserController extends AbstractCrudController<User, UserService, UserValidator> {
    protected ImageController imageController;

    protected GrantedAuthority updateByIdAuthority = Role.Authority.UPDATE_USER.getAuthority();
    protected GrantedAuthority updatePasswordByIdAuthority = Role.Authority.UPDATE_USER_PASSWORD.getAuthority();

    @Autowired
    public UserController(ApplicationContext context) {
        super(context);
        imageController = context.getBean(ImageController.class);
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

    @SuppressWarnings("SameParameterValue")
    private User updateImage(User user, MultipartFile file, String parameter){
        Image image = imageController.validateAndCreate(String.format("%s img", user.getUsername()), file, parameter);

        imageController.getService().deleteNullable(user.getImage());
        user.setImage(image);

        return service.update(user);
    }

    @SuppressWarnings("UnusedReturnValue")
    private User removeImage(User user) {
        imageController.getService().deleteNullable(user.getImage());
        user.setImage(null);

        return service.update(user);
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
    public User updateById(@PathVariable("id") String strId, @RequestBody JsonNode json) {
        checkAuthority(updateByIdAuthority);
        User user = service.findByIdOrThrow(convertToUUID(strId, "id"));

        return update(user, json);
    }

    // TODO add endpoints for user blocking and role granting

    @PutMapping("/password")
    public User updatePasswordByAuth(@AuthenticationPrincipal User user, @RequestBody JsonNode json) {
        return updatePassword(user, json);
    }

    @PutMapping("/{id}/password")
    public User updatePasswordById(@PathVariable("id") String strId, @RequestBody JsonNode json) {
        checkAuthority(updatePasswordByIdAuthority);
        User user = service.findByIdOrThrow(convertToUUID(strId, "id"));

        return updatePassword(user, json);
    }

    @PutMapping("/image")
    public User updateImageByAuth(@AuthenticationPrincipal User user, @RequestParam("image") MultipartFile file) {
        checkAuthority(updateAuthority);
        return updateImage(user, file, "image");
    }

    @PutMapping("/{id}/image")
    public User updateImageById(@PathVariable("id") String strId, @RequestParam("image") MultipartFile file) {
        checkAuthority(updateByIdAuthority);
        User user = service.findByIdOrThrow(convertToUUID(strId, "id"));

        return updateImage(user, file, "image");
    }

    @DeleteMapping("/image")
    public ResponseEntity<Void> removeImageByAuth(@AuthenticationPrincipal User user) {
        checkAuthority(updateAuthority);

        removeImage(user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> removeImageById(@PathVariable("id") String strId) {
        checkAuthority(updateByIdAuthority);
        User user = service.findByIdOrThrow(convertToUUID(strId, "id"));

        removeImage(user);

        return ResponseEntity.noContent().build();
    }
}
