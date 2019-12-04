package com.constellation.cancer.registry.docker;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.support.FailbackRegistry;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperTransporter;
import com.alibaba.dubbo.rpc.RpcException;
import com.constellation.cancer.core.context.ContextUtils;
import com.constellation.cancer.core.context.DockerContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/3 11:05 AM
 */
public class Docker2ZkRegistry extends FailbackRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docker2ZkRegistry.class);
    private static final String DEFAULT_ROOT = "dubbo";
    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, ChildListener>> zkListeners = new ConcurrentHashMap();
    private final Set<String> anyServices = new ConcurrentHashSet();
    private final String root;
    private final ZookeeperClient zkClient;

    public Docker2ZkRegistry(URL url, ZookeeperTransporter zookeeperTransporter) {
        super(url);

        if (url.isAnyHost()) {
            throw new IllegalStateException("registry address == null");
        } else {
            String group = url.getParameter(Constants.GROUP_KEY, DEFAULT_ROOT);
            if (!group.startsWith(Constants.PATH_SEPARATOR)) {
                group = Constants.PATH_SEPARATOR + group;
            }

            this.root = group;
            this.zkClient = zookeeperTransporter.connect(url);
            this.zkClient.addStateListener(state -> {
                if (state == StateListener.RECONNECTED) {
                    try {
                        recover();
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            });
        }
    }

    @Override
    protected void doRegister(URL url) {
        URL modUrl = url;
        try {
            DockerContext dockerCtx = ContextUtils.getDockerContext();
            if (dockerCtx.isInDocker() && url.getPort() != 0) {
                LOGGER.info("original url == " + url.toFullString() + "; port=" + url.getPort());
                String dockerHostIp = dockerCtx.getDockerHostIp();
                String dockerHostPort = dockerCtx.getMappingPort(String.valueOf(url.getPort()));
                LOGGER.info("dockerHost == " + dockerHostIp + "; port=" + dockerHostPort);
                if (StringUtils.isEmpty(dockerHostPort)) {
                    throw new IllegalStateException("The port: " + url.getPort() + " should be exposed!");
                }

                modUrl = modUrl.setHost(dockerHostIp).setPort(Integer.parseInt(dockerHostPort));
                LOGGER.info("Running in Docker container, change the registered url from " + url + " to " + modUrl);
            }

            zkClient.create(toUrlPath(modUrl), modUrl.getParameter(Constants.DYNAMIC_KEY, true));
        } catch (Throwable ex) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
        }
    }

    private String toUrlPath(URL url) {
        return this.toCategoryPath(url) + Constants.PATH_SEPARATOR + URL.encode(url.toFullString());
    }

    private String toCategoryPath(URL url) {
        return this.toServicePath(url) + Constants.PATH_SEPARATOR + url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY);
    }

    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        return Constants.ANY_VALUE.equals(name) ? toRootPath() : toRootDir() + URL.encode(name);
    }

    private String toRootPath() {
        return root;
    }

    private String toRootDir() {
        return root.equals(Constants.PATH_SEPARATOR) ? this.root : this.root + Constants.PATH_SEPARATOR;
    }


    @Override
    protected void doUnregister(URL url) {
        try {
            zkClient.delete(toUrlPath(url));
        } catch (Throwable ex) {
            throw new RpcException("Failed to unregister " + url + " to zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doSubscribe(URL url, NotifyListener listener) {
        try {
            if (Constants.ANY_VALUE.equals(url.getServiceInterface())) {
                String rootPath = toRootPath();
                ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url);
                if (listeners == null) {
                    zkListeners.putIfAbsent(url, new ConcurrentHashMap());
                    listeners = zkListeners.get(url);
                }

                ChildListener zkListener = listeners.get(listener);
                if (zkListener == null) {
                    listeners.putIfAbsent(listener, (parentPath, currentChilds) -> {
                        currentChilds.forEach(child->{
                            if (!anyServices.contains(child)) {
                                anyServices.add(child);
                                subscribe(url.setPath(child).addParameters(new String[]{Constants.INTERFACE_KEY, child, Constants.CHECK_KEY, String.valueOf(false)}), listener);
                            }
                        });
                    });
                    zkListener = listeners.get(listener);
                }

                zkClient.create(rootPath, false);
                List<String> services = zkClient.addChildListener(rootPath, zkListener);
                if (services != null && !services.isEmpty()) {
                    anyServices.addAll(services);
                    services.forEach(service->subscribe(url.setPath(service).addParameters(new String[]{Constants.INTERFACE_KEY, service, Constants.CHECK_KEY, String.valueOf(false)}), listener));
                }
            } else {
                List<URL> urls = new ArrayList();
                String[] arrgs = this.toCategoriesPath(url);

                for(int i = 0; i < arrgs.length; i++) {
                    String path = arrgs[i];
                    ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url);
                    if (listeners == null) {
                        zkListeners.putIfAbsent(url, new ConcurrentHashMap());
                        listeners = zkListeners.get(url);
                    }

                    ChildListener zkListener = listeners.get(listener);
                    if (zkListener == null) {
                        listeners.putIfAbsent(listener, (parentPath, currentChilds) -> {});
                        zkListener = listeners.get(listener);
                    }

                    zkClient.create(path, false);
                    List<String> children = zkClient.addChildListener(path, zkListener);
                    if (children != null) {
                        urls.addAll(toUrlsWithEmpty(url, path, children));
                    }
                }

                notify(url, listener, urls);
            }

        } catch (Throwable ex) {
            throw new RpcException("Failed to subscribe " + url + " to zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        } else {
            try {
                List<String> providers = new ArrayList();
                String[] arrgs = this.toCategoriesPath(url);

                for(int i = 0; i < arrgs.length; i++) {
                    String path = arrgs[i++];
                    List<String> children = zkClient.getChildren(path);
                    if (children != null) {
                        providers.addAll(children);
                    }
                }

                return this.toUrlsWithoutEmpty(url, providers);
            } catch (Throwable ex) {
                throw new RpcException("Failed to lookup " + url + " from zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
            }
        }
    }

    private String[] toCategoriesPath(URL url) {
        String[] categroies;
        if (Constants.ANY_VALUE.equals(url.getParameter(Constants.CATEGORY_KEY))) {
            categroies = new String[]{Constants.PROVIDERS_CATEGORY, Constants.CONSUMERS_CATEGORY, Constants.ROUTERS_CATEGORY, Constants.CONFIGURATORS_CATEGORY};
        } else {
            categroies = url.getParameter(Constants.CATEGORY_KEY, new String[]{Constants.PROVIDERS_CATEGORY});
        }

        String[] paths = new String[categroies.length];

        for(int i = 0; i < categroies.length; ++i) {
            paths[i] = this.toServicePath(url) + Constants.PATH_SEPARATOR + categroies[i];
        }

        return paths;
    }

    private List<URL> toUrlsWithEmpty(URL consumer, String path, List<String> providers) {
        List<URL> urls = this.toUrlsWithoutEmpty(consumer, providers);
        if (urls.isEmpty()) {
            int i = path.lastIndexOf('/');
            String category = i < 0 ? path : path.substring(i + 1);
            URL empty = consumer.setProtocol(Constants.EMPTY_PROTOCOL).addParameter(Constants.CATEGORY_KEY, category);
            urls.add(empty);
        }

        return urls;
    }

    private List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList();
        if (providers != null && !providers.isEmpty()) {
            providers.forEach(provider->{
                provider = URL.decode(provider);
                if (provider.contains("://")) {
                    URL url = URL.valueOf(provider);
                    if (UrlUtils.isMatch(consumer, url)) {
                        urls.add(url);
                    }
                }
            });
        }

        return urls;
    }

    @Override
    protected void doUnsubscribe(URL url, NotifyListener listener) {
        ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url);
        if (listeners != null) {
            ChildListener zkListener = listeners.get(listener);
            if (zkListener != null) {
                zkClient.removeChildListener(toUrlPath(url), zkListener);
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return zkClient.isConnected();
    }

    @Override
    public void destroy() {
        super.destroy();

        try {
            zkClient.close();
        } catch (Exception ex) {
            LOGGER.warn("Failed to close zookeeper client " + this.getUrl() + ", cause: " + ex.getMessage(), ex);
        }

    }
}
