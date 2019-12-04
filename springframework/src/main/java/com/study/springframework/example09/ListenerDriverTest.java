package com.study.springframework.example09;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 11:34 AM
 */
public class ListenerDriverTest {
    public static void main(String[] args) {
        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example09.xml");
        TestListener testListener = (TestListener)bf.getBean("testListener");
        TestEvent testEvent = new TestEvent("hello","msg");
        testListener.onApplicationEvent(testEvent);

    }
}
