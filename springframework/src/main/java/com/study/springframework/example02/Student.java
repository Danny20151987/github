package com.study.springframework.example02;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/21 2:40 PM
 */

public class Student extends User {

    @Override
    public void showMe() {
        System.out.println("I am Student");
    }
}
