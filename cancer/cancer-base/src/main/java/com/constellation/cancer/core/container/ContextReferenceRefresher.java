package com.constellation.cancer.core.container;

import org.springframework.context.ApplicationContext;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 10:40 AM
 */
public interface ContextReferenceRefresher {

    void refresh(ApplicationContext applicationContext);

}
