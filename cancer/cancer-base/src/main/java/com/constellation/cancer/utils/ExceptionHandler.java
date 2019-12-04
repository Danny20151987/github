package com.constellation.cancer.utils;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.constellation.cancer.commons.Constants;
import com.constellation.cancer.core.context.CancerThreadContext;
import com.constellation.cancer.exception.BusinessException;
import com.constellation.cancer.exception.ErrorCodeContainer;
import com.constellation.cancer.exception.InconformityException;
import com.constellation.cancer.exception.PlatformException;
import com.constellation.cancer.param.BaseOutput;
import com.constellation.cancer.param.ResponseData;
import com.constellation.cancer.param.StringOutput;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:22 PM
 */
public class ExceptionHandler {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);
    private static final Logger ERRORPUT = LoggerFactory.getLogger("trans_error");



    public static Object handleException(Class<?> returnType, Throwable throwable) throws PlatformException {
        LOGGER.error("", throwable);
        return handleExceptionNotPrintThrowable(returnType, throwable);
    }

    public static Object handleExceptionNotPrintThrowable(Class<?> returnType, Throwable throwable) throws PlatformException {
        String respType = Constants.ResponseType.EXCEPTION;
        String errorCode;
        String errorMessage;
        Object extraObject = null;

        try {
            throw throwable;
        } catch (TransactionException ex) {
            if (ex instanceof TransactionSystemException) {
                respType = Constants.ResponseType.INCONFORMITY;
            }

            Throwable causeThrowable = ex.getCause();
            if (causeThrowable == null) {
                causeThrowable = ex;
            }

            if (causeThrowable instanceof PlatformException) {
                errorCode = ((PlatformException)causeThrowable).getErrorCode();
                errorMessage = ((PlatformException)causeThrowable).getErrorMessage();
            } else {
                errorCode = Constants.ResponseCode.EXCEPTION;
                errorMessage = wrapExceptionStackTrace(causeThrowable);
            }

            throwable = causeThrowable;
        } catch (InconformityException ex) {
            respType = Constants.ResponseType.INCONFORMITY;
            errorCode = ex.getErrorCode();
            errorMessage = ex.getErrorMessage();
        } catch (BusinessException ex) {
            respType =  Constants.ResponseType.FAULT;
            errorCode = ex.getErrorCode();
            errorMessage = ex.getErrorMessage();
            if (ex.getExtraObject() != null) {
                extraObject = ex.getExtraObject();
            }
        } catch (PlatformException ex) {
            errorCode = ex.getErrorCode();
            if (ex.getErrorMessage() == null && ex.getCause() != null) {
                errorMessage = wrapExceptionStackTrace(ex.getCause());
            } else {
                errorMessage = ex.getErrorMessage();
            }
        } catch (DataAccessException ex) {
            errorCode = Constants.ResponseCode.DATA_ACCESS_EXCEPTION;
            errorMessage = ErrorCodeContainer.getErrorMessage(errorCode, new String[0]);
        } catch (RpcException ex) {
            if (ex.getCause() instanceof PlatformException) {
                errorCode = ((PlatformException)ex.getCause()).getErrorCode();
                errorMessage = ((PlatformException)ex.getCause()).getErrorMessage();
            } else {
                errorCode = Constants.ResponseCode.PRP_OTHEREXCEPTION;
                errorMessage = ErrorCodeContainer.getErrorMessage(errorCode, new String[0]);
            }
        } catch (Throwable ex) {
            errorCode = Constants.ResponseCode.EXCEPTION;
            errorMessage = wrapExceptionStackTrace(ex);
        }

        ERRORPUT.error("errorCode={},errorMessage={},exceptionClass={}", new Object[]{errorCode, errorMessage, throwable.getClass().getName()});
        if (BaseOutput.class.isAssignableFrom(returnType)) {
            Object resultValue;

            try {
                resultValue = returnType.newInstance();
            } catch (IllegalAccessException | InstantiationException var9) {
                resultValue = new StringOutput();
            }

            ResponseData respData = ((BaseOutput)resultValue).getRespData();
            respData.setType(respType);
            respData.setCode(errorCode);
            respData.setMessage(errorMessage);
            respData.setReturnTime(SysUtil.getSysDate());
            respData.setServerIp(RpcContext.getContext().getLocalHost());
            respData.setServcSeqNo(CancerThreadContext.getSysSeqNo());
            if (extraObject != null) {
                if (resultValue instanceof StringOutput) {
                    ((StringOutput)resultValue).setBody((String)extraObject);
                } else {
                    BeanCopier bc = BeanCopier.create(extraObject.getClass(), resultValue.getClass(), false);
                    bc.copy(extraObject, resultValue, null);
                }
            }

            return resultValue;
        } else if (throwable instanceof PlatformException) {
            throw (PlatformException)throwable;
        } else {
            throw new PlatformException(errorCode, new String[]{errorMessage});
        }
    }

    public static String wrapExceptionStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return ErrorCodeContainer.getErrorMessage(Constants.ResponseCode.EXCEPTION, new String[]{sw.getBuffer().toString()});
    }
}
