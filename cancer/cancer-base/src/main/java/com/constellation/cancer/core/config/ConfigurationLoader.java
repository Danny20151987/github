package com.constellation.cancer.core.config;

import com.constellation.cancer.configsvrclient.RegistryObserver;
import com.constellation.cancer.core.context.ContextUtils;
import com.constellation.cancer.core.utils.CoreConstants;
import com.constellation.cancer.core.utils.ResourceUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 3:00 PM
 */
public class ConfigurationLoader extends PropertyPlaceholderConfigurer implements BeanPostProcessor, PriorityOrdered, RegistryObserver {

    private List<String> paths;
    private Resource[] locations;
    private final Map<String, Object> configs = new ConcurrentHashMap();
    private final Hashtable<String, Set<ConfigurationListener>> configListeners = new Hashtable();


    @Override
    public void registryChanged(String configKey) {
        Properties props;
        try {
            Resource res = new ConfigSvrResource(configKey);
            if (res != null) {
                if (res.getFilename().endsWith(CoreConstants.PROPERTIES_FILE_END)) {
                    props = new Properties();
                    props.load(res.getInputStream());
                    this.configs.put(configKey, props);
                } else {
                    this.configs.put(configKey, res);
                }
            }
        } catch (IOException ex) {
            logger.error("ConfigSvrResource:[" + configKey + "] reload error", ex);
        }

        this.notifyConfigListener(configKey);
        if (this.configs.get(configKey) instanceof Properties) {
            Properties updatedConfig = (Properties)this.configs.get(configKey);
            props = (Properties)this.configs.get(CoreConstants.INITIAL_PROPS_KEY);
            props.putAll(updatedConfig);
            this.setToContext(props);
        }
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.setSystemPropertiesMode(2);
        super.processProperties(beanFactoryToProcess, props);
        this.configs.put(CoreConstants.INITIAL_PROPS_KEY, props);
        this.setToContext(props);
        if (this.isConfigServerEnable()) {
            Resource[] resources = this.locations;
            for(Resource r : resources){
                if (r instanceof ConfigSvrResource) {
                    ConfigServerClientFactory.getConfigServerClient().register(r.getFilename(), null, true);
                }
            }
        }

    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!this.isConfigServerEnable()) {
            return bean;
        } else {
            if (bean instanceof ConfigurationListener) {
                ConfigurationListener listener = (ConfigurationListener)bean;
                if (listener.getConfigKeys() != null && listener.getConfigKeys().size() > 0) {

                    listener.getConfigKeys().stream().forEach(
                        configKey->{
                            ConfigServerClientFactory.getConfigServerClient().subscribe(configKey, this);
                            this.addConfigListener(configKey, listener);
                        }
                    );
                }
            }

            return bean;
        }
    }

    public void addConfigListener(String configKey, ConfigurationListener configListener) {
        Set<ConfigurationListener> listeners = this.configListeners.get(configKey);
        if (listeners != null) {
            listeners.add(configListener);
        } else {
            listeners = new HashSet();
            listeners.add(configListener);
            this.configListeners.put(configKey, listeners);
        }

    }

    private boolean isConfigServerEnable() {
        return BootstrapConfigUtil.isTrue(CoreConstants.CONFIG_SERVER_ENABLE);
    }

    public void notifyConfigListener(String configKey) {
        Set<ConfigurationListener> listeners = this.configListeners.get(configKey);
        if (listeners != null) {
            listeners.stream().forEach(
                listener ->{
                    Object config = this.configs.get(configKey);
                    if (config != null && config instanceof Resource) {
                        listener.resourceChanged(configKey, (Resource)config);
                    }

                    if (config != null && config instanceof Properties) {
                        Properties mergedConfigProps = (Properties)this.configs.get(CoreConstants.INITIAL_PROPS_KEY);
                        Properties changedProps = this.getChangedProps((Properties)config, mergedConfigProps);
                        listener.configChanged(configKey, changedProps);
                    }
                }
            );
        }

    }

    private Properties getChangedProps(Properties updated, Properties orginal) {
        Properties changedProps = new Properties();

        updated.keySet().stream().forEach(
            key->{
                if (orginal.get(key) != null && !orginal.get(key).equals(updated.get(key))) {
                    changedProps.put(key, updated.get(key));
                }
            }
        );

        return changedProps;
    }

    private void setToContext(Properties mergedProps) {
        if (logger.isDebugEnabled()) {
            logger.debug("Cancer system properties:");
            Iterator i$ = mergedProps.keySet().iterator();

            while(i$.hasNext()) {
                Object key = i$.next();
                logger.debug(key + "=" + mergedProps.getProperty((String)key));
            }
        }

        Properties configuration = ContextUtils.getCancerContext().configuration;
        if (configuration == null) {
            configuration = new Properties();
        }

        configuration.putAll(mergedProps);
    }

    public List<String> getPaths() {
        return this.paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
        if (this.locations == null) {
            if (this.paths != null) {
                int size = this.paths.size();
                if (size > 0) {
                    this.locations = new Resource[size];

                    for(int i = 0; i < size; ++i) {
                        this.locations[i] = ResourceUtil.getResource(paths.get(i), "config/")[0];
                    }
                }
            }

            this.setLocations(this.locations);
        }
    }

    public void setLocations(Resource... locations) {
        this.locations = locations;
        super.setLocations(locations);
    }


}
