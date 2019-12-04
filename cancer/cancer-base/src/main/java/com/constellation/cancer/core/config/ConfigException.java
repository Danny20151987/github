package com.constellation.cancer.core.config;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 6:14 PM
 */
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 7816785622415226283L;

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
