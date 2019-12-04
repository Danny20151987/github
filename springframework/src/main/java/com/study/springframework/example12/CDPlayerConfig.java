package com.study.springframework.example12;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/28 4:42 PM
 */
@Configuration
@ComponentScan
@PropertySource("classpath:config/application.properties")
public class CDPlayerConfig {

    @Autowired
    Environment env;

    @Bean
    public CompactDisc sgtPeppers(){
        return new SgtPeppers(env.getProperty("disc.title"),env.getProperty("disc.artist"));
    }
}
