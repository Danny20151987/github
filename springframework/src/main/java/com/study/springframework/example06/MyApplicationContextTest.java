package com.study.springframework.example06;

import org.springframework.context.ApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/23 10:48 PM
 */
public class MyApplicationContextTest {
    public static void main(String[] args) {

        ApplicationContext myBf = new MyClassPathXmlApplicationContext("spring/example06.xml");
        MyBean myBean =(MyBean)myBf.getBean("myBean");
        System.out.println(myBean.getTestStr());
    }
}
