package com.study.springframework.example04;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/22 2:41 PM
 */
public class UserBeanHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        this.registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
    }
}
