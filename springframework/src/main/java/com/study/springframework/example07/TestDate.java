package com.study.springframework.example07;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 8:59 AM
 */
public class TestDate {

    public static void main(String[] args) {

        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example07.xml");
        UserManger userManger = (UserManger)bf.getBean("userManger");
        System.out.println(userManger);
    }
}
