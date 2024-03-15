package knu.csc.ttp.qualificationwork.mqwb.entities.game.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;

public class GameSerializer extends AbstractEntitySerializer<Game> {
    public GameSerializer() {
        super(Game.class);
    }
}
