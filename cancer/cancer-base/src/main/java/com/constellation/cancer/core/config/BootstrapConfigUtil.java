package com.constellation.cancer.core.config;

import com.constellation.cancer.core.utils.CoreConstants;
import com.constellation.cancer.core.utils.IOUtils;
import com.constellation.cancer.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.util.Properties;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 5:15 PM
 */
public class BootstrapConfigUtil {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BootstrapConfigUtil.class);

    private static volatile Properties PROPERTIES;


    public BootstrapConfigUtil() {
    }

    public static String getProperty(String key) {
        Assert.notNull(key, "the key must not be null ");
        String value = System.getenv(key);
        if (value == null || value.trim().length() == 0) {
            value = System.getProperty(key);
        }

        if (value == null || value.trim().length() == 0) {
            value = PROPERTIES.getProperty(key);
        }

        value = StringUtils.replaceProperty(value, PROPERTIES);
        return value;
    }

    public static boolean isTrue(String key) {
        String value = getProperty(key);
        return "true".equalsIgnoreCase(value);
    }

    public static Properties getProperties() {
        return PROPERTIES;
    }

    private static Properties loadProperties(String fileName) {

        Properties properties;
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            properties = IOUtils.stream2props(resource.getInputStream());
        } catch (Exception ex) {
            LOGGER.warn("Failed to load bootstrap properties file from " + fileName + ": " + ex.getMessage(), ex);
            throw new ConfigException("Failed to load bootstrap properties file from " + fileName, ex);
        }

        if (System.getenv(CoreConstants.APP_NAME) != null) {
            System.setProperty(CoreConstants.APP_NAME, System.getenv(CoreConstants.APP_NAME));
        }

        if (System.getenv(CoreConstants.APP_ENV) != null) {
            System.setProperty(CoreConstants.APP_ENV, System.getenv(CoreConstants.APP_ENV));
        }

        if (System.getenv(CoreConstants.APP_VERSION) != null) {
            System.setProperty(CoreConstants.APP_VERSION, System.getenv(CoreConstants.APP_VERSION));
        }

        if (System.getenv(CoreConstants.APP_TENANTID) != null) {
            System.setProperty(CoreConstants.APP_TENANTID, System.getenv(CoreConstants.APP_TENANTID));
        }

        if (System.getenv(CoreConstants.APP_SYSNO) != null) {
            System.setProperty(CoreConstants.APP_SYSNO, System.getenv(CoreConstants.APP_SYSNO));
        }

        if(System.getenv(CoreConstants.APP_ERROR_LANGUAGE) != null){
            System.setProperty(CoreConstants.APP_ERROR_LANGUAGE, System.getenv(CoreConstants.APP_ERROR_LANGUAGE));
        }

        return properties;
    }

    static {
        if (PROPERTIES == null) {
            synchronized (BootstrapConfigUtil.class) {
                if (PROPERTIES == null) {
                    String path = System.getenv(CoreConstants.BOOTSTRAP_PROPERTIES);
                    if (path == null || path.length() == 0) {
                        path = System.getProperty(CoreConstants.BOOTSTRAP_PROPERTIES);
                        if (path == null || path.length() == 0) {
                            path = CoreConstants.DEFAULT_BOOTSTRAP_PROPERTIES;
                        }
                    }

                    PROPERTIES = loadProperties(path);
                }
            }
        }

    }
}