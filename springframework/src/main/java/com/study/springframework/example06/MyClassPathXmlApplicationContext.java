package com.study.springframework.example06;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/23 10:44 PM
 */
public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

    public MyClassPathXmlApplicationContext(String ...configLocations){
        super(configLocations);
    }

    @Override
    protected void initPropertySources() {
        getEnvironment().setRequiredProperties("VAR");
    }

    @Override
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        super.setAllowBeanDefinitionOverriding(false);
        super.setAllowCircularReferences(false);
        super.customizeBeanFactory(beanFactory);
    }
}
