package com.constellation.cancer.core.container;

import com.alibaba.citrus.springext.support.context.AbstractXmlApplicationContext;
import com.constellation.cancer.core.config.BootstrapConfigUtil;
import com.constellation.cancer.core.config.ConfigSvrResource;
import com.constellation.cancer.core.utils.CoreConstants;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 10:46 AM
 */
public class CancerXmlApplicationContext extends AbstractXmlApplicationContext {

    private ContextReferenceRefresher refresher;


    public CancerXmlApplicationContext(ContextReferenceRefresher refresher, String... locations) throws BeansException {
        this.refresher = refresher;
        this.setConfigLocations(locations);
        this.setParentResolvableDependenciesAccessible(false);
        this.refresh();
    }

    public CancerXmlApplicationContext(ApplicationContext parentContext, ContextReferenceRefresher refresher, String... locations) throws BeansException {
        super(parentContext);
        this.refresher = refresher;
        this.setConfigLocations(locations);
        this.setParentResolvableDependenciesAccessible(false);
        this.refresh();
    }


    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CoreConstants.CONFIGSVR_URL_PREFIX)) {
            location = location.substring(CoreConstants.CONFIGSVR_URL_PREFIX.length());
            if (BootstrapConfigUtil.isTrue(CoreConstants.CONFIG_SERVER_ENABLE)) {
                try {
                    return new ConfigSvrResource(location);
                } catch (Exception ex) {
                    throw new RuntimeException("Could not get config " + location + " from config server!");
                }
            } else {
                if (location.endsWith("#")) {
                    location = location.substring(0, location.length() - 1);
                }

                return new ClassPathResource(location, this.getClassLoader());
            }
        } else {
            return super.getResource(location);
        }
    }

    protected void finishRefresh() {
        if (this.refresher != null) {
            this.refresher.refresh(this);
        }

        super.finishRefresh();
    }
}
