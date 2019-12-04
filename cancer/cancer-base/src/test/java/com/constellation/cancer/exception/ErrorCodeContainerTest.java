package com.constellation.cancer.exception;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/22 9:00 AM
 */
public class ErrorCodeContainerTest {

    public static void main(String[] args) throws Exception{
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ErrorCodeContainer.xml");
//        ErrorCodeContainer errorCodeContainer = (ErrorCodeContainer) applicationContext.getBean("errorCodeContainer");
//        errorCodeContainer.afterPropertiesSet();
    }

}
