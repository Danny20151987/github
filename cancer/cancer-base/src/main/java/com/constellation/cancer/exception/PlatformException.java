package com.constellation.cancer.exception;

import com.constellation.cancer.commons.Constants;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/22 9:36 PM
 */
public class PlatformException extends RuntimeException{


    private static final long serialVersionUID = -3479253334097724013L;
    private String errorCode;
    private String errorMessage;

    public PlatformException() {
        super("");
    }

    public PlatformException(Throwable cause) {
        super(cause);
        this.setErrorCode(Constants.ResponseCode.EXCEPTION);
    }

    protected PlatformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PlatformException(String errorCode) {
        super(errorCode + ":" + ErrorCodeContainer.getErrorMessage(errorCode, new String[0]));
        this.errorCode = errorCode;
        this.errorMessage = ErrorCodeContainer.getErrorMessage(errorCode, new String[0]);
    }

    public PlatformException(String errorCode, Throwable e) {
        super(errorCode + ":" + ErrorCodeContainer.getErrorMessage(errorCode, new String[0]), e);
        this.errorCode = errorCode;
        this.errorMessage = ErrorCodeContainer.getErrorMessage(errorCode, new String[0]);
    }

    public PlatformException(String errorCode, String... errorMessageParams) {
        super(errorCode + ":" + ErrorCodeContainer.getErrorMessage(errorCode, errorMessageParams));
        this.errorCode = errorCode;
        this.errorMessage = ErrorCodeContainer.getErrorMessage(errorCode, errorMessageParams);
    }

    public PlatformException(String errorCode, String[] errorMessageParams, Throwable e) {
        super(errorCode + ":" + ErrorCodeContainer.getErrorMessage(errorCode, errorMessageParams), e);
        this.errorCode = errorCode;
        this.errorMessage = ErrorCodeContainer.getErrorMessage(errorCode, errorMessageParams);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        return String.format("%s :%s:%s", this.getClass().getName(), this.errorCode, this.errorMessage == null ? "" : this.errorMessage);
    }
}
