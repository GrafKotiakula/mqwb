package knu.csc.ttp.qualificationwork.mqwb.exceptions.client;

import jakarta.servlet.http.HttpServletRequest;
import knu.csc.ttp.qualificationwork.mqwb.Constants;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

public class MethodNotAllowedException extends RequestException {
    protected MethodNotAllowedException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }

    @Override
    protected Integer getBaseCode() {
        return Constants.methodNotAllowedBaseCode;
    }

    public static MethodNotAllowedException build(HttpServletRequest request) {
        return build(request, null);
    }

    public static MethodNotAllowedException build(HttpServletRequest request, Throwable cause) {
        return new MethodNotAllowedException(0,
                String.format("Method %s is not supported for %s", request.getMethod(), request.getRequestURI()),
                cause);
    }
}
