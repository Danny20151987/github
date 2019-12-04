package com.constellation.cancer.core.utils;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 9:17 AM
 */
public class CoreConstants {

    public static final String SPRING_CONTEXT_MAIN = "spring.context.main";
    public static String PATH_PRE = "classpath*:META-INF/cancer/internal/cancer-component-";
    public static String PATH_SUF = ".xml";
    public static String CANCER_PATH_MAIN = "classpath:config/cancer-main.xml";
    public static String PROPERTIES_FILE_END = ".properties";
    public static final String INITIAL_PROPS_KEY = "mergedConfigProps";


    public static final String BOOTSTRAP_PROPERTIES = "cancer.bootstrap.properties.file";
    public static final String DEFAULT_BOOTSTRAP_PROPERTIES = "/config/bootstrap.properties";
    public static final String DEFAULT_SYSTEM_PROPERTIES = "config/system.properties";
    public static final String DEFAULT_PARAMS_CONFIG = "classpath*:config/*_params.*";
    public static final String APP_NAME = "app.name";
    public static final String APP_VERSION = "app.version";
    public static final String APP_ENV = "app.env";
    public static final String APP_TENANTID = "app.tenantId";
    public static final String APP_SYSNO = "app.sysNo";
    public static final String APP_ERROR_LANGUAGE="app.error.language";


    public static final String LOAD_COMPONENTS = "components";
    public static final String CONFIG_SERVER_ENABLE = "configsvr.enable";
    public static final String CONFIGSVR_URL_PREFIX = "configsvr:";

    public static final String DOCKER_HOST = "DOCKER_HOST";
    public static final String MTAB_PATH = "/etc/mtab";
    public static final String DOCKER_ID_MATCH = "[0-9a-z]{64}";
    public static final String NETWORK_SETTINGS = "NetworkSettings";
    public static final String PORTS = "Ports";
    public static final String HOST_IP = "HostIp";
    public static final String HOST_PORT = "HostPort";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final int VALUE_TYPE_DATE = 1;
    public static final int VALUE_TYPE_BIGDECIMAL = 2;
    public static final int VALUE_TYPE_STRING = 3;
    public static final int VALUE_TYPE_INTEGER = 4;

}
