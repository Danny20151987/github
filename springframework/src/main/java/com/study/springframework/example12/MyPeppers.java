package com.study.springframework.example12;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/29 11:06 AM
 */
@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//@Scope(value = WebApplicationContext.SCOPE_SESSION,proxyMode = ScopedProxyMode.INTERFACES)
public class MyPeppers implements CompactDisc {

    @Override
    public void play() {
        System.out.println("MyPeppers");
    }
}
