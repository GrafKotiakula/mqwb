package knu.csc.ttp.qualificationwork.mqwb.entities.game;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractCrudController;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.jpa.GameService;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.Image;
import knu.csc.ttp.qualificationwork.mqwb.entities.image.ImageController;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/data/game")
public class GameController extends AbstractCrudController<Game, GameService, GameValidator> {
    protected ImageController imageController;
    protected GrantedAuthority findByNameAuthority = Role.Authority.READ.getAuthority();

    @Autowired
    public GameController(ApplicationContext context) {
        super(context);
        imageController = context.getBean(ImageController.class);
        createAuthority = Role.Authority.MODIFY_GAME.getAuthority();
        updateAuthority = Role.Authority.MODIFY_GAME.getAuthority();
        deleteAuthority = Role.Authority.MODIFY_GAME.getAuthority();
    }

    @GetMapping("/find-by/name")
    public Page<Game> getAllByName(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam("filter") String filter) {
        checkAuthority(findByNameAuthority);
        return service.findAllByNameContains(filter, page);
    }

    @PutMapping("/{id}/image")
    public Game updateImage(@PathVariable("id") String strId, @RequestParam("image") MultipartFile file) {
        checkAuthority(updateAuthority);

        Game game = service.findByIdOrThrow(convertToUUID(strId, "id"));
        Image image = imageController.validateAndCreate(String.format("%s img", game.getName()), file, "image");

        imageController.getService().deleteNullable(game.getImage());
        game.setImage(image);

        return service.update(game);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> removeImage(@PathVariable("id") String strId) {
        checkAuthority(updateAuthority);
        Game game = service.findByIdOrThrow(convertToUUID(strId, "id"));

        imageController.getService().deleteNullable(game.getImage());
        game.setImage(null);
        service.update(game);

        return ResponseEntity.noContent().build();
    }
}
