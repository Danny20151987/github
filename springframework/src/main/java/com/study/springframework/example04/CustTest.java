package com.study.springframework.example04;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 2:56 PM
 */
public class CustTest {
    public static void main(String[] args) {
        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example04.xml");
        User user = (User)bf.getBean("testUser");
        System.out.println(user.getUserName()+"---"+user.getEmail());

    }
}
