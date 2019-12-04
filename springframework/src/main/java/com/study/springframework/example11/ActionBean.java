package com.study.springframework.example11;

import org.springframework.stereotype.Component;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 8:58 PM
 */
@Component
public class ActionBean {

    public void action(){
        System.out.println("do something");
    }

    public void trackPlay(int num){
        System.out.println("play track:"+num);
    }
}
