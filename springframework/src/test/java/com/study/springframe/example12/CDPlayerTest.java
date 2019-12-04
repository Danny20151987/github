package com.study.springframe.example12;

import com.study.springframework.example12.CDPlayerConfig;
import com.study.springframework.example12.CompactDisc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/28 4:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDPlayerConfig.class)
//@ContextConfiguration("classpath:spring/example12.xml")
public class CDPlayerTest {

    @Autowired
    @Qualifier("sgtPeppers")
    private CompactDisc cd;

    @Test
    public void cdShouldNotBeNull(){
        assertNotNull(cd);
    }
}
