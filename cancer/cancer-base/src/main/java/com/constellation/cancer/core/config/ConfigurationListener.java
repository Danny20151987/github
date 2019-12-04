package com.constellation.cancer.core.config;

import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Properties;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 4:18 PM
 */
public interface ConfigurationListener {

    void resourceChanged(String var1, Resource var2);

    void configChanged(String var1, Properties var2);

    List<String> getConfigKeys();
}
