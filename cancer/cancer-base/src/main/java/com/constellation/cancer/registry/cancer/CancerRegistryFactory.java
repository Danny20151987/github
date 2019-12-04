package com.constellation.cancer.registry.cancer;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.support.AbstractRegistryFactory;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperTransporter;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/3 4:35 PM
 */
public class CancerRegistryFactory extends AbstractRegistryFactory {

    private ZookeeperTransporter zookeeperTransporter;

    public CancerRegistryFactory() {
    }

    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }

    @Override
    protected Registry createRegistry(URL url) {
        return new CancerRegistry(url,this.zookeeperTransporter);
    }
}
