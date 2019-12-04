package com.constellation.cancer.aop;

import com.constellation.cancer.commons.Constants;
import com.constellation.cancer.core.context.CancerThreadContext;
import com.constellation.cancer.exception.PlatformException;
import com.constellation.cancer.utils.ExceptionHandler;
import com.constellation.cancer.utils.PrintArgumentUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:11 PM
 */

public class LogControlAop {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogControlAop.class);
    private static final Logger INOUTPUT = LoggerFactory.getLogger("trans_inoutput");


    public LogControlAop() {

    }

    public Object invoke(ProceedingJoinPoint jp) throws PlatformException {
        boolean printArgument = false;
        Object printArgumentFlag = CancerThreadContext.getContext(Constants.ServiceControl.PRINT_ARGUMENT);
        if (printArgumentFlag != null && printArgumentFlag instanceof Boolean && (Boolean)printArgumentFlag) {
            printArgument = true;
        }

        String interfaceName = jp.getSignature().getDeclaringTypeName();
        if (printArgument) {
            INOUTPUT.info("input={}", PrintArgumentUtil.toJSONString(jp.getArgs()));
        }

        Object result = null;

        try {
            result = jp.proceed();
        } catch (Throwable var11) {
            LOGGER.error("服务 [{}/{}] 处理失败,请求入参为 : {} ", new Object[]{interfaceName, jp.getSignature().getName(), PrintArgumentUtil.toJSONString(jp.getArgs())});
            Class<?> returnType = (MethodSignature.class.cast(jp.getSignature())).getReturnType();
            result = ExceptionHandler.handleException(returnType, var11);
        } finally {
            if (printArgument) {
                INOUTPUT.info("output={}", PrintArgumentUtil.toJSONString(result));
            }

        }

        return result;
    }

}
