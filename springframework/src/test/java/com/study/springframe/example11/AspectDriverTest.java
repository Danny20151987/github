package com.study.springframe.example11;

import com.study.springframework.example11.ActionBean;
import com.study.springframework.example11.AspectConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/31 4:39 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AspectConfig.class)
public class AspectDriverTest {

    @Autowired
//    @Qualifier("actionBean")
    private ActionBean actionBean;


    @Test
    public void testAspectBean(){
        assertNotNull(actionBean);
        actionBean.action();
    }


    @Test
    public void testAspectAroundArgs(){
        assertNotNull(actionBean);
        actionBean.trackPlay(1);
        actionBean.trackPlay(2);
        actionBean.trackPlay(1);
        actionBean.trackPlay(2);

    }
}
