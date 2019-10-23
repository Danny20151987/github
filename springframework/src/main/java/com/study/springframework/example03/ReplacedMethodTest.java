package com.study.springframework.example03;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/21 3:01 PM
 */
public class ReplacedMethodTest {

    public static void main(String[] args) {
        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example03.xml");
        TestChangeMethod cm = (TestChangeMethod)bf.getBean("testChangeMethod");
        cm.changeMe();
    }
}
