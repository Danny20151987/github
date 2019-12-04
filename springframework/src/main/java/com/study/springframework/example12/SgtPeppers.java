package com.study.springframework.example12;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/28 4:35 PM
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SgtPeppers implements CompactDisc{

    private String title = "Sgt club band";
    private String artist = "the beatles";

    public SgtPeppers(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public void play() {
        System.out.println("Playing:"+title+" by "+artist);
    }
}


