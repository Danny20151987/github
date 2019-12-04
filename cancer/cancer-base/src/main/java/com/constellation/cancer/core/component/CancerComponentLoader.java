package com.constellation.cancer.core.component;

import com.alibaba.citrus.util.regex.PathNameWildcardCompiler;
import com.constellation.cancer.core.context.ContextUtils;
import com.constellation.cancer.core.utils.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 4:11 PM
 */
public class CancerComponentLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancerComponentLoader.class);

    public CancerComponentLoader() {
    }

    public void loadComponents(String components) {
        String locationPattern = CoreConstants.PATH_PRE + "*" + CoreConstants.PATH_SUF;
        String[] prefixAndPattern = this.checkComponentConfigurationLocationPattern(locationPattern);
        String pathPattern = prefixAndPattern[1];
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] componentConfigurations = resolver.getResources(locationPattern);
            Pattern pattern = PathNameWildcardCompiler.compilePathName(pathPattern);
            Resource[] resources = componentConfigurations;
            int component_len = componentConfigurations.length;

            for(int i = 0; i < component_len; ++i) {
                Resource resource = resources[i];
                String path = resource.getURL().getPath();
                if (StringUtils.isEmpty(components) || components.indexOf(path.substring(path.indexOf("component-") + "component-".length(), path.indexOf(".xml"))) >= 0) {
                    Matcher matcher = pattern.matcher(path);
                    Assert.isTrue(matcher.find(), "unknown component configuration file: " + path);
                    String componentId = this.covertkey(matcher.group(1));
                    if (componentId != null) {
                        CancerComponent cancerComponent = new CancerComponent();
                        cancerComponent.setId(componentId);
                        cancerComponent.setVersion(this.parseComponentVersion(path));
                        cancerComponent.setConfigFilePath(path);
                        ContextUtils.getCancerContext().addCancerComponent(componentId, cancerComponent);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("*************** 平台组件加载 --->>>组件ID:[" + cancerComponent.getId() + "],组件版本:[" + cancerComponent.getVersion() + "],配置文件路径:[" + cancerComponent.getConfigFilePath() + "] <<<--- ***************");
                        }
                    }
                }
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String[] checkComponentConfigurationLocationPattern(String componentConfigurationLocationPattern) {
        if (componentConfigurationLocationPattern != null) {
            boolean classpath = componentConfigurationLocationPattern.startsWith("classpath*:");
            String pathPattern = componentConfigurationLocationPattern;
            if (classpath) {
                pathPattern = componentConfigurationLocationPattern.substring("classpath*:".length()).trim();
            }

            int index = pathPattern.indexOf("*");
            if (index >= 0) {
                index = pathPattern.indexOf("*", index + 1);
                if (index < 0) {
                    if (pathPattern.startsWith("/")) {
                        pathPattern = pathPattern.substring(1);
                    }

                    return new String[]{classpath ? "classpath:" : "", pathPattern};
                }
            }
        }

        throw new IllegalArgumentException("Invalid componentConfigurationLocationPattern: " + componentConfigurationLocationPattern);
    }

    private String parseComponentVersion(String configFilePath) {
        if (StringUtils.isEmpty(configFilePath)) {
            return "";
        } else {
            int jarIndex = configFilePath.indexOf(".jar");
            if (jarIndex == -1) {
                return "";
            } else {
                String preString = configFilePath.substring(0, jarIndex + 4);
                String fileSeparator = "/";
                int lastFileSeparator = preString.lastIndexOf(fileSeparator);
                String version = preString.substring(lastFileSeparator + 1, jarIndex);
                return version;
            }
        }
    }

    private String covertkey(String key) {
        Assert.notNull(key);
        return key.toLowerCase();
    }

}
