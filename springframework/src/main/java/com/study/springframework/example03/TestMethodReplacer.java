package com.study.springframework.example03;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/21 2:56 PM
 */
public class TestMethodReplacer implements MethodReplacer {
    @Override
    public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {
        System.out.println("我替换了原来的方法:"+method.getName());
        return null;
    }
}
