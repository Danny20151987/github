package com.constellation.cancer.core.health;

import java.util.Map;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 11:29 AM
 */
public interface HealthManager {


    boolean isApplicationStarted();

    boolean healthcheck();

    Map<String, Health> healthInfo();
}
