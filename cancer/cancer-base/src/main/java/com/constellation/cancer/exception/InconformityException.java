package com.constellation.cancer.exception;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:33 PM
 */
public class InconformityException extends PlatformException{


    private static final long serialVersionUID = -5421520190884430490L;

    public InconformityException(String errorCode) {
        super(errorCode);
    }

    public InconformityException(String errorCode, Throwable e) {
        super(errorCode, e);
    }

    public InconformityException(String errorCode, String... errorMessageParams) {
        super(errorCode, errorMessageParams);
    }

    public InconformityException(String errorCode, String[] errorMessageParams, Throwable e) {
        super(errorCode, errorMessageParams, e);
    }
}
