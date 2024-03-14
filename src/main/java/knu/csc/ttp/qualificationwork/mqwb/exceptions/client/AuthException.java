package knu.csc.ttp.qualificationwork.mqwb.exceptions.client;

import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

public class AuthException  extends RequestException {
    public AuthException(Integer code, String clientMessage, String message, Throwable cause) {
        super(code, clientMessage, message, cause);
    }

    protected AuthException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.authExceptionBaseCode;
    }

    public static AuthException expiredToken(){
        return expiredToken(null);
    }

    public static AuthException expiredToken(Throwable cause){
        return new AuthException(0, "Token is expired", cause);
    }

    public static AuthException invalidToken(){
        return invalidToken(null);
    }

    public static AuthException invalidToken(Throwable cause){
        return new AuthException(1, "Token is invalid", cause);
    }

    public static AuthException wrongUsernameOrPassword(){
        return wrongUsernameOrPassword(null);
    }

    public static AuthException wrongUsernameOrPassword(Throwable cause){
        return new AuthException(2, "Wrong username or password", cause);
    }
}
