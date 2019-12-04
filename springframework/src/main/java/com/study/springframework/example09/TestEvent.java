package com.study.springframework.example09;

import org.springframework.context.ApplicationEvent;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 11:31 AM
 */
public class TestEvent extends ApplicationEvent {

    private String msg;

    public TestEvent(Object source) {
        super(source);
    }

    public TestEvent(Object source, String msg) {
        super(source);
        this.msg = msg;
    }

    public void print(){
        System.out.println(msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
