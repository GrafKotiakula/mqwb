package knu.csc.ttp.qualificationwork.mqwb.entities.user.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;

public class UserSerializer extends AbstractEntitySerializer<User> {
    public UserSerializer() {
        super(User.class);
    }
}
