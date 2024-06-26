package knu.csc.ttp.qualificationwork.mqwb.entities.user.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.jackson.deserialization.AbstractEntityDeserializer;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.Role;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;

@JsonComponent
public class UserDeserializer extends AbstractEntityDeserializer<User> {
    private static final String PASSWORD_FIELD_NAME = "password";
    private static final String ROLE_FIELD_NAME = "role";

    @Autowired
    public UserDeserializer(ApplicationContext context) {
        super(context, User.class);
    }

    public String getPassword(JsonNode node) {
        return getProperty(node, PASSWORD_FIELD_NAME, String.class);
    }
    public Role getRole(JsonNode node) {
        return getProperty(node, ROLE_FIELD_NAME, Role.class);
    }
}
