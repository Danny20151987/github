package com.study.springframework.example08;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/18 2:14 PM
 */
public class MyBeanFactory {

    public static void main(String[] args) {

        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example08.xml");
        MyBean myBean = (MyBean)bf.getBean("myBean");
        System.out.println(myBean.getTestStr());

    }
}
