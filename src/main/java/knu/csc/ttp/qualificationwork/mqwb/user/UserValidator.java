package knu.csc.ttp.qualificationwork.mqwb.user;

import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.validation.AbstractEntityValidator;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.UnprocessableEntityException;
import knu.csc.ttp.qualificationwork.mqwb.user.jpa.UserService;
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

    protected void validateUsernameUniqueness(User user) {
        service.findByUsername(user.getUsername()).filter( u -> !u.getId().equals(user.getId()) ).ifPresent(u -> {
            throw LoggerUtils.logException(logger, defaultLogLvl,
                    UnprocessableEntityException.duplicatedField(User.class, USERNAME_FIELD_NAME));
        });
    }

    @Override
    public void validate(User user, String validationGroup) {
        super.validate(user, validationGroup);
        if(validationGroup == null || validationGroup.equals(CREATE) || validationGroup.equals(UPDATE)) {
            validateUsernameUniqueness(user);
        }
    }
}
