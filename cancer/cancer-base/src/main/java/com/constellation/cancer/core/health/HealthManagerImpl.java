package com.constellation.cancer.core.health;

import com.constellation.cancer.core.context.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 3:08 PM
 */
public class HealthManagerImpl implements HealthManager, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthManagerImpl.class);
    private Map<String, HealthIndicator> healthIndicators = new HashMap();


    public HealthManagerImpl() {
    }

    @Override
    public boolean isApplicationStarted() {
        return ContextUtils.isApplicationStarted();
    }

    @Override
    public boolean healthcheck() {

        AtomicBoolean isHealth = new AtomicBoolean(true);
        Map<String, Health> healthcheckResult = this.healthInfo();

        healthcheckResult.entrySet().forEach(
            entry ->{
                HealthStatus status = (entry.getValue()).getStatus();
                if (!status.equals(HealthStatus.UP)) {
                    LOGGER.info("组件[" + entry.getKey() + "]健康检查不通过!当前状态为[" + status + "]");
                    isHealth.set(false);
                }
            }
        );

        return isHealth.get();
    }

    @Override
    public Map<String, Health> healthInfo() {
        Map<String, Health> healthcheckResult = new HashMap();
        LOGGER.info("HealthIndicators包括：" + this.healthIndicators.keySet());

        this.healthIndicators.entrySet().forEach(
            entry ->{
                String beanName = entry.getKey();
                HealthIndicator healthIndicator = entry.getValue();
                LOGGER.debug("进行healthcheck： " + beanName);
                Health result = healthIndicator.health();
                healthcheckResult.put(beanName, result);
            }
        );

        return healthcheckResult;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.healthIndicators = applicationContext.getBeansOfType(HealthIndicator.class);
    }
}
