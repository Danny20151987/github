package com.constellation.cancer.core.component;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 4:04 PM
 */
public class CancerComponent {

    private String id;
    private String version;
    private String configFilePath;

    public CancerComponent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }
}
