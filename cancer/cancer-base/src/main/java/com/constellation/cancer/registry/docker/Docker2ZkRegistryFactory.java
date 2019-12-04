package com.constellation.cancer.registry.docker;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.support.AbstractRegistryFactory;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperTransporter;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/3 10:57 AM
 */
public class Docker2ZkRegistryFactory extends AbstractRegistryFactory {

    private ZookeeperTransporter zookeeperTransporter;

    public Docker2ZkRegistryFactory() {
    }

    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }

    public Registry createRegistry(URL url) {
        return new Docker2ZkRegistry(url, this.zookeeperTransporter);
    }
}
