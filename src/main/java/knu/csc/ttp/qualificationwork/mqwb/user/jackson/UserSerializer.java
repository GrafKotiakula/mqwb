package knu.csc.ttp.qualificationwork.mqwb.user.jackson;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.serialization.AbstractEntitySerializer;
import knu.csc.ttp.qualificationwork.mqwb.user.User;

public class UserSerializer extends AbstractEntitySerializer<User> {
    public UserSerializer() {
        super(User.class);
    }
}
