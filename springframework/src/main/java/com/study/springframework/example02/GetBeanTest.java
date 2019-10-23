package com.study.springframework.example02;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/21 2:10 PM
 */
public abstract class GetBeanTest {

    public void showMe(){
        this.getBean().showMe();
    }

    public abstract User getBean();
}
