package com.study.springframework.example01;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/18 2:14 PM
 */
public class MyBeanFactory {

    public static void main(String[] args) {

        BeanFactory bf = new XmlBeanFactory(new ClassPathResource("spring/example01.xml"));
        MyBean myBean = (MyBean)bf.getBean("myBean");
        System.out.println(myBean.getTestStr());


//        BeanDefinition bd = ((XmlBeanFactory) bf).getBeanDefinition("myBean");
//        System.out.println(bd.getAttribute("testStr"));

    }
}
