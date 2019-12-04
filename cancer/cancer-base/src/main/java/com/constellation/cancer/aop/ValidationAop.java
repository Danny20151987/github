package com.constellation.cancer.aop;

import com.constellation.cancer.exception.ErrorCodeContainer;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 5:52 PM
 */
public class ValidationAop {

    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationAop.class);


    public void invoke(JoinPoint jp) throws IllegalArgumentException {
        Object[] arguments = jp.getArgs();
        this.validate(arguments);
    }

    public void validate(Object... arguments) throws IllegalArgumentException {
        Validator validator = this.validatorFactory.getValidator();
        Set<ConstraintViolation<?>> violations = new HashSet();
        Object[] arrgs = arguments;
        int length = arrgs.length;

        for(int i = 0; i < length; ++i) {
            Object arg = arrgs[i];
            this.validate(validator, violations, arg);
        }

        if (violations.size() > 0) {
            StringBuilder message = new StringBuilder("\r\n");
            Iterator iterator = violations.iterator();

            while(iterator.hasNext()) {
                ConstraintViolation<?> cons = (ConstraintViolation)iterator.next();
                message.append(this.transolateMessage(cons)).append("\r\n");
            }

            throw new IllegalArgumentException(message.toString());
        }
    }

    private String transolateMessage(ConstraintViolation<?> constraintViolation) {
        Annotation annotation = constraintViolation.getConstraintDescriptor().getAnnotation();
        String annotationName = annotation.annotationType().getSimpleName();
        String property = constraintViolation.getPropertyPath().toString();
        String message = constraintViolation.getMessage();
        if (null != message && message.trim().length() > 0) {
            property = message;
        }

        String invalidValue = constraintViolation.getInvalidValue() != null ? constraintViolation.getInvalidValue().toString() : "null";
        String[] params;
        byte var9 = -1;
        switch(annotationName.hashCode()) {
            case -501753126:
                if (annotationName.equals("NotNull")) {
                    var9 = 2;
                }
                break;
            case -381793000:
                if (annotationName.equals("DapDateFormat")) {
                    var9 = 7;
                }
                break;
            case 77124:
                if (annotationName.equals("Max")) {
                    var9 = 0;
                }
                break;
            case 77362:
                if (annotationName.equals("Min")) {
                    var9 = 1;
                }
                break;
            case 2439591:
                if (annotationName.equals("Null")) {
                    var9 = 4;
                }
                break;
            case 2577441:
                if (annotationName.equals("Size")) {
                    var9 = 6;
                }
                break;
            case 873562992:
                if (annotationName.equals("Pattern")) {
                    var9 = 5;
                }
                break;
            case 1614161505:
                if (annotationName.equals("NotBlank")) {
                    var9 = 3;
                }
        }

        switch(var9) {
            case 0:
                Max max = (Max)annotation;
                params = new String[]{property, String.valueOf(max.value()), invalidValue};
                break;
            case 1:
                Min min = (Min)annotation;
                params = new String[]{property, String.valueOf(min.value()), invalidValue};
                break;
            case 2:
                params = new String[]{!"不能为null".equals(property) && !"may not be null".equals(property) ? property : constraintViolation.getPropertyPath().toString()};
                break;
            case 3:
                params = new String[]{!"不能为空".equals(property) && !"may not be empty".equals(property) ? property : constraintViolation.getPropertyPath().toString()};
                break;
            case 4:
                params = new String[]{property};
                break;
            case 5:
                Pattern p = (Pattern)annotation;
                params = new String[]{property, p.regexp(), invalidValue};
                break;
            case 6:
                Size size = (Size)annotation;
                params = new String[]{property, String.valueOf(size.min()), String.valueOf(size.max()), invalidValue};
                break;
            case 7:
                params = new String[]{property, invalidValue};
                break;
            default:
                LOGGER.warn("Unsupport transolate annotation [{}]", annotationName);
                return "";
        }

        return ErrorCodeContainer.getErrorMessage(annotationName, params);
    }

}
