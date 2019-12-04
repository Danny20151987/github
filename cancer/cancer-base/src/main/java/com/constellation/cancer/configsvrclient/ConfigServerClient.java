package com.constellation.cancer.configsvrclient;

import java.io.InputStream;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 11:07 AM
 */
public interface ConfigServerClient {

    boolean register(String var1, String var2, boolean var3);

    boolean subscribe(String var1, RegistryObserver var2);

    boolean isExist(String var1);

    InputStream getConfigContent(String var1);

    ConfigServerInfo getRegistryInfo();

    public static class ConfigServerInfo {
        public String registryHost;
        public String registryRootPath;

        public ConfigServerInfo() {
        }
    }
}
