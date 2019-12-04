package com.study.springframework.example10;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 2:34 PM
 */
public class AspectBean {

    public void beforeTest(){
        System.out.println("Before Test");
    }

    public void afterTest(){
        System.out.println("After Test");
    }

    public void aroundTest(ProceedingJoinPoint p){

        System.out.println("around before test");

        try {
            p.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.out.println("around after test");
    }

    public void afterReturnTest(){
        System.out.println("after return Test");
    }

    public void afterThrowTest(){
        System.out.println("after Throw Test");
    }

}
