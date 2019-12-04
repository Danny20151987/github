package com.study.springframework.example09;

import org.springframework.context.ApplicationListener;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 11:33 AM
 */
public class TestListener implements ApplicationListener<TestEvent> {

    @Override
    public void onApplicationEvent(TestEvent event) {
        event.print();
    }
}
