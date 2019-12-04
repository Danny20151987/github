package com.study.springframework.example12;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hudejian
 * @DESC 自定义注解
 * @date 2019/10/29 11:34 AM
 */

@Target({ElementType.CONSTRUCTOR,ElementType.FIELD,
        ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface MyQualifier {
}
