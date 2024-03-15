package knu.csc.ttp.qualificationwork.mqwb.entities.game.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class GameDeserializer extends AbstractEntityDeserializer<Game> {
    @Autowired
    public GameDeserializer(ApplicationContext context) {
        super(context, Game.class);
    }
}
