package com.study.springframework.example02;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/21 2:13 PM
 */
public class LookUpTest {

    public static void main(String[] args) {

        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example02.xml");
        GetBeanTest gbt = (GetBeanTest)bf.getBean("getBeanTest");
        gbt.showMe();
    }
}
