package com.constellation.cancer.exception;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:34 PM
 */
public class BusinessException extends PlatformException {


    private static final long serialVersionUID = -284569984587457671L;
    private Object extraObject;

    public BusinessException() {
    }

    public BusinessException(String errorCode) {
        super(errorCode);
    }

    public BusinessException(String errorCode, Throwable e) {
        super(errorCode, e);
    }

    public BusinessException(String errorCode, String... errorMessageParams) {
        super(errorCode, errorMessageParams);
    }

    public BusinessException(String errorCode, String[] errorMessageParams, Throwable e) {
        super(errorCode, errorMessageParams, e);
    }

    public Object getExtraObject() {
        return this.extraObject;
    }

    public void setExtraObject(Object extraObject) {
        this.extraObject = extraObject;
    }
}
