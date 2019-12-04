package com.constellation.cancer.core.utils;

import com.constellation.cancer.core.config.BootstrapConfigUtil;
import com.constellation.cancer.core.config.ConfigException;
import com.constellation.cancer.core.config.ConfigSvrResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 4:09 PM
 */
public class ResourceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtil.class);

    public static Resource[] getResource(String path, String prefix) {
        try {
            Resource[] locations = (new PathMatchingResourcePatternResolver()).getResources(path);
            if (BootstrapConfigUtil.isTrue(CoreConstants.CONFIG_SERVER_ENABLE) && path.indexOf(CoreConstants.DEFAULT_BOOTSTRAP_PROPERTIES) < 0) {
                LOGGER.info("using config server ");

                for(int i = 0; i < locations.length; ++i) {
                    String location = prefix.concat(locations[i].getFilename());
                    locations[i] = new ConfigSvrResource(location);
                }
            }

            return locations;
        } catch (IOException ex) {
            throw new ConfigException("加载错误码配置文件失败", ex);
        }
    }


    public static Resource[] getResource(List<String> configKeys) {
        Resource[] locations;

        try {
            locations = new Resource[configKeys.size()];

            for(int i = 0; i < configKeys.size(); ++i) {
                if (BootstrapConfigUtil.isTrue(CoreConstants.CONFIG_SERVER_ENABLE)) {
                    if (i == 0) {
                        LOGGER.info("using config server ");
                    }

                    locations[i] = new ConfigSvrResource(configKeys.get(i));
                } else {
                    locations[i] = (new PathMatchingResourcePatternResolver()).getResource(configKeys.get(i));
                }
            }

            return locations;
        } catch (IOException ex) {
            throw new ConfigException("加载参数配置文件失败", ex);
        }
    }
}
