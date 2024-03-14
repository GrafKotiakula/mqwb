package knu.csc.ttp.qualificationwork.mqwb.user.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class UserDeserializer extends AbstractEntityDeserializer<User> {
    private static final String PASSWORD_FIELD_NAME = "password";

    @Autowired
    public UserDeserializer(ApplicationContext context) {
        super(context, User.class);
    }

    public static String getPassword(JsonNode node) {
        return getProperty(node, PASSWORD_FIELD_NAME, String.class);
    }
}
