package com.study.springframework.example11;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hudejian
 * @DESC aop注解方式需要引入spring-aspects.jar
 * @date 2019/10/24 8:54 PM
 */
@Aspect
@Component
public class AspectBean {

    @Pointcut("execution(* *.action(..))")
    public void action(){

    }

    @Before("action()")
    public void beforeAction(){
        System.out.println("before action");
    }

    @After("action()")
    public void afterAction(){
        System.out.println("after action");
    }

    @Around("action()")
    public Object aroundAction(ProceedingJoinPoint p){
        System.out.println("before1");
        Object o =null;
        try{
            o = p.proceed();
        }catch (Throwable e){
            e.printStackTrace();
        }
        System.out.println("after1");
        return o;

    }

    @AfterReturning("action()")
    public void afterReturn(){
        System.out.println("afterReturn action");
    }

    @AfterThrowing("action()")
    public void afterThrowing(){
        System.out.println("afterThrowing action");
    }

    Map<Integer,Integer> countMap = new HashMap<>();


    @Pointcut("execution(* *.trackPlay(int)) && args(trackNum)")
    public void trackPlay(int trackNum){}

    @Around("trackPlay(trackNum)")
    public void aroundTrackPlay(ProceedingJoinPoint p,int trackNum){
        try{
            p.proceed();
            if(countMap.get(trackNum)!=null)
                countMap.put(trackNum,countMap.get(trackNum)+1);
            else
                countMap.put(trackNum,1);
            System.out.println("trackNum："+countMap.get(trackNum));
        }catch (Throwable e){

        }
    }

}
