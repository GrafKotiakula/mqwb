package knu.csc.ttp.qualificationwork.mqwb.exceptions.client;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;

public class ForbiddenException extends RequestException {
    protected ForbiddenException(Integer code, String clientMessage, String message, Throwable cause) {
        super(code, clientMessage, message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.forbiddenBaseCode;
    }

    public static ForbiddenException noRequiredAuthority(Authentication auth, GrantedAuthority authority) {
        return noRequiredAuthority(auth, authority, null);
    }

    public static ForbiddenException noRequiredAuthority(Authentication auth, GrantedAuthority authority, Throwable cause) {
        String userDescription = Optional.ofNullable(auth)
                .map(Authentication::getPrincipal)
                .map(Object::toString)
                .orElse(String.format("UNKNOWN_USER[Authentication=%s]", auth));
        return new ForbiddenException(0, "Access forbidden",
                String.format("%s has no required authority %s", userDescription, authority),
                cause);
    }
}
