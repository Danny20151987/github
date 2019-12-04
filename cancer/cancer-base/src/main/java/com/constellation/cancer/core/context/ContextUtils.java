package com.constellation.cancer.core.context;

import com.constellation.cancer.core.config.BootstrapConfigUtil;
import com.constellation.cancer.core.config.ConfigSvrResource;
import com.constellation.cancer.core.utils.CoreConstants;
import com.constellation.cancer.core.utils.IOUtils;
import com.constellation.cancer.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 8:45 AM
 */
public class ContextUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

    private static final DockerContext DOCKER_CONTEXT = new DockerContext();
    private static final CancerContext CANCER_CONTEXT = new CancerContext();
    private static ApplicationContext SPRING_CANCER_CONTEXT;
    private static ApplicationContext SPRING_APP_CONTEXT;
    private static volatile boolean isApplicationStarted = false;

    public ContextUtils() {
    }


    private static void configDubbo() {
        System.setProperty("com.alibaba.dubbo.common.logger.Level", "slf4j");
        String rpcPropsFile = CoreConstants.DEFAULT_SYSTEM_PROPERTIES;
        boolean isConfigServerEnabled = BootstrapConfigUtil.isTrue(CoreConstants.CONFIG_SERVER_ENABLE);
        Object resource;

        try {
            if (!isConfigServerEnabled) {
                resource = new ClassPathResource(rpcPropsFile);
            } else {
                resource = new ConfigSvrResource(rpcPropsFile);
            }

            Properties props = IOUtils.stream2props(((Resource)resource).getInputStream());

            props.stringPropertyNames().forEach(
                key->{
                    if (key.startsWith("rpc.") && !key.endsWith(".group") && !key.endsWith(".address") && !key.endsWith(".protocol")) {
                        String dubbo_key = "dubbo." + key.substring(4);
                        String value = props.getProperty(key);
                        value = StringUtils.replaceProperty(value, BootstrapConfigUtil.getProperties());
                        LOGGER.info("Setting system property value for dubbo, key=" + dubbo_key + " ,value=" + value);
                        System.setProperty(dubbo_key, value);
                    }
                }
            );
        } catch (IOException ex) {
            LOGGER.warn("Failed to load rpc config properties from " + rpcPropsFile + ": " + ex.getMessage(), ex);
        }

        if (isConfigServerEnabled) {
        }

    }

    public static DockerContext getDockerContext() {
        return DOCKER_CONTEXT;
    }

    public static CancerContext getCancerContext() {
        return CANCER_CONTEXT;
    }

    public static ApplicationContext getSpringCancerContext() {
        return SPRING_CANCER_CONTEXT;
    }


    public static void setSpringCancerContext(ApplicationContext springCancerContext) {
        SPRING_CANCER_CONTEXT = springCancerContext;
    }

    public static ApplicationContext getSpringAppContext() {
        return SPRING_APP_CONTEXT;

    }

    public static void setSpringAppContext(ApplicationContext springAppContext){
        SPRING_APP_CONTEXT = springAppContext;
    }

    public static String getTenantId() {
        return BootstrapConfigUtil.getProperty(CoreConstants.APP_TENANTID);
    }


    public static boolean isApplicationStarted() {
        return isApplicationStarted;
    }

    public static void setApplicationStarted(boolean isApplicationStarted) {
        isApplicationStarted = isApplicationStarted;
    }


    static {
        configDubbo();
    }
}
