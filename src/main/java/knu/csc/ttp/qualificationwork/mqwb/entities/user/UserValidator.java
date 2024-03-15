package knu.csc.ttp.qualificationwork.mqwb.entities.user;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.entities.user.jpa.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UserValidator extends AbstractEntityValidator<User> {
    protected static final String USERNAME_FIELD_NAME = "username";
    public static final String PASSWORD_UPDATE = "PASSWORD_UPDATE";

    private final UserService service;

    public UserValidator(ApplicationContext context) {
        super(User.class);
        service = context.getBean(UserService.class);
    }

    @Override
    public void validate(User user, String validationGroup) {
        super.validate(user, validationGroup);
        if(validationGroup == null || validationGroup.equals(CREATE) || validationGroup.equals(UPDATE)) {
            validateUniqueness(user, service.findByUsername(user.getUsername()), defaultLogLvl, USERNAME_FIELD_NAME);
        }
    }
}
