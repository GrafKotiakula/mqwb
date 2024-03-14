package knu.csc.ttp.qualificationwork.mqwb.exceptions;

public abstract class RequestException extends RuntimeException {
    private final Integer code;
    private String clientMessage;

    protected RequestException(Integer code, String clientMessage, String message, Throwable cause) {
        super(message, cause);
        if(code == null) {
            throw new IllegalArgumentException("Code must not bu null");
        }
        if(code >= 100 || code < 0) {
            throw new IllegalArgumentException(
                    String.format("Code must be in [0; 99] interval but was %d", code));
        }
        this.code = getBaseCode() + code;
        this.clientMessage = clientMessage;
    }

    public RequestException(Integer code, String message, Throwable cause) {
        this(code, message, message, cause);
    }

    protected abstract Integer getBaseCode();

    public Integer getCode() {
        return code;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }
}
