package knu.csc.ttp.qualificationwork.mqwb.controllers.exceptionresolver;

import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;

public class FailureDto {
    private Integer code;
    private String message;

    public FailureDto(RequestException exception) {
        this.code = exception.getCode();
        this.message = exception.getClientMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
