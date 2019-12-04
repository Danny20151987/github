package com.study.springframework.example11;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 9:08 PM
 */
public class AspestAnnotationTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/example11.xml");
        ActionBean actionBean = (ActionBean)context.getBean("actionBean");
        actionBean.action();
    }
}
