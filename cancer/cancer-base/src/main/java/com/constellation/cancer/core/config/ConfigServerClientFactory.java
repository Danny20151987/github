package com.constellation.cancer.core.config;

import com.constellation.cancer.configsvrclient.ConfigServerClient;
import com.constellation.cancer.core.utils.CoreConstants;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 11:05 AM
 */
public class ConfigServerClientFactory {
    public ConfigServerClientFactory() {
    }

    public static ConfigServerClient getConfigServerClient() {
        boolean isConfigServerEnabled = BootstrapConfigUtil.isTrue(CoreConstants.CONFIG_SERVER_ENABLE);
        if (!isConfigServerEnabled) {
            throw new RuntimeException("The configserver enable option must be true in the bootstrap properties! ");
        } else {
            ServiceLoader<ConfigServerClient> s = ServiceLoader.load(ConfigServerClient.class);
            Iterator<ConfigServerClient> it = s.iterator();
            if (it.hasNext()) {
                ConfigServerClient c = it.next();
                if (it.hasNext()) {
                    throw new RuntimeException("There are two or more ConfigServerClient implementation! ");
                } else {
                    return c;
                }
            } else {
                throw new RuntimeException("There are no ConfigServerClient implementation! ");
            }
        }
    }
}
