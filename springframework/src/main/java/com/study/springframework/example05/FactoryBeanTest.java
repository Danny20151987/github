package com.study.springframework.example05;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 10:26 PM
 */
public class FactoryBeanTest {
    public static void main(String[] args) throws Exception{
        ApplicationContext bf = new ClassPathXmlApplicationContext("spring/example05.xml");
        Car car = (Car)bf.getBean("carFactoryBean");
        System.out.println(car.getBrand()+"-"+car.getMaxSpeed()+"-"+car.getPrice());

        FactoryBean carFactory = (FactoryBean)bf.getBean("&carFactoryBean");

        System.out.println(carFactory.isSingleton());
        System.out.println(carFactory.getObjectType());
        Car car1 = (Car)carFactory.getObject();
        System.out.println(car1.getBrand()+"-"+car1.getMaxSpeed()+"-"+car1.getPrice());
    }
}
