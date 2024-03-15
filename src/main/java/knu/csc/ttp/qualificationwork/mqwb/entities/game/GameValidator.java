package knu.csc.ttp.qualificationwork.mqwb.entities.game;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.jpa.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameValidator extends AbstractEntityValidator<Game> {
    protected static final String NAME_FIELD_NAME = "name";

    private final GameService service;

    @Autowired
    public GameValidator(GameService service) {
        super(Game.class);
        this.service = service;
    }

    @Override
    public void validate(Game game, String validationGroup) {
        super.validate(game, validationGroup);
        if(validationGroup == null || validationGroup.equals(CREATE) || validationGroup.equals(UPDATE)) {
            validateUniqueness(game, service.findByName(game.getName()), defaultLogLvl, NAME_FIELD_NAME);
        }
    }
}
