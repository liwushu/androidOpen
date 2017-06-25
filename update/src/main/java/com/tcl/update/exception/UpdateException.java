package com.tcl.update.exception;


public class UpdateException extends Exception {
    private String errorMessage;
    private int errorCode;

    public UpdateException() {
        super();
    }

    public UpdateException(String detailMessage) {
        super(detailMessage);
        errorMessage = detailMessage;
    }

    public UpdateException(int errorCode, String detailMessage) {
        this(detailMessage);
        this.errorCode = errorCode;
    }

    public UpdateException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        errorMessage = detailMessage;
    }

    public UpdateException(int errorCode, String detailMessage, Throwable throwable) {
        this(detailMessage, throwable);
        this.errorCode = errorCode;
    }

    public UpdateException(Throwable throwable) {
        super(throwable);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
