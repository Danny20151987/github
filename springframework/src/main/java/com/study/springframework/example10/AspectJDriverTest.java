package com.study.springframework.example10;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 2:43 PM
 */
public class AspectJDriverTest{
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/example10.xml");
        TestBean testBean = (TestBean)context.getBean("testBean");
        testBean.test();

    }
}
