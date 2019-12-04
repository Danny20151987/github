package com.constellation.cancer.registry.cancer;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/3 4:37 PM
 */
public class CancerRegistry extends FailbackRegistry {
    private static final Logger logger = LoggerFactory.getLogger(CancerRegistry.class);
    private final static String DEFAULT_ROOT = "/dubbo/";
    private final static String CANCER_GROUP = "RegGrp";
    private final String root;
    private final ZookeeperClient zkClient;
    private final Set<String> anyServices = new ConcurrentHashSet();
    private final ConcurrentMap<URL, ConcurrentMap<NotifyListener, ChildListener>> zkListeners = new ConcurrentHashMap();

    public CancerRegistry(URL url, ZookeeperTransporter zookeeperTransporter) {
        super(url);
        if (url.isAnyHost()) {
            throw new IllegalStateException("registry address == null");
        } else {
            String group = url.getParameter(Constants.GROUP_KEY);
            if (!group.startsWith(Constants.PATH_SEPARATOR)) {
                group = DEFAULT_ROOT+  group;
            }

            this.root = group;
            this.zkClient = zookeeperTransporter.connect(url);
            this.zkClient.addStateListener(state -> {
                if (state == StateListener.RECONNECTED) {
                    try {
                        recover();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }

            });
        }
    }

    @Override
    protected void doRegister(URL url) {
        try {
            zkClient.create(toUrlPath(url), url.getParameter(Constants.DYNAMIC_KEY, true));
        } catch (Throwable ex) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
        }
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
            if (url.hasParameter(CANCER_GROUP)) {
                url.removeParameter(CANCER_GROUP);
            }

            final URL url2 = url.addParameter(CANCER_GROUP, root.substring(DEFAULT_ROOT.length()));
            if (Constants.ANY_VALUE.equals(url2.getServiceInterface())) {
                ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url2);
                if (listeners == null) {
                    zkListeners.putIfAbsent(url2, new ConcurrentHashMap());
                    listeners = zkListeners.get(url2);
                }

                ChildListener zkListener = listeners.get(listener);
                if (!url2.hasParameter(CANCER_GROUP)) {
                    if (zkListener == null) {
                        listeners.putIfAbsent(listener, (parentPath, currentChilds) -> {
                            if (currentChilds != null && currentChilds.size() > 0) {
                                currentChilds.forEach(child->{
                                    child = URL.decode(child);
                                    String childPath = DEFAULT_ROOT + child;
                                    if (!anyServices.contains(childPath)) {
                                        anyServices.add(childPath);
                                        subscribe(url2.setPath(child).addParameters(new String[]{CANCER_GROUP, child, Constants.CHECK_KEY, String.valueOf(false)}), listener);
                                    }
                                });
                            }

                        });
                        zkListener = listeners.get(listener);
                    }

                    zkClient.create(root, false);
                    List<String> regGrps = zkClient.addChildListener(Constants.PATH_SEPARATOR + Constants.DEFAULT_DIRECTORY, zkListener);
                    if (regGrps != null && regGrps.size() > 0) {
                        regGrps.forEach(regGrp->subscribe(url2.setPath(regGrp).addParameters(new String[]{CANCER_GROUP, regGrp, Constants.CHECK_KEY, String.valueOf(false)}), listener));
                    }
                } else {
                    final String regGrp = url2.getParameter(CANCER_GROUP);
                    if (zkListener == null) {
                        listeners.putIfAbsent(listener, (parentPath, currentChilds) -> {
                            if (currentChilds != null && currentChilds.size() > 0) {
                                currentChilds.forEach(child->{
                                    child = URL.decode(child);
                                    String childPath = DEFAULT_ROOT + regGrp + Constants.PATH_SEPARATOR+ child;
                                    if (!anyServices.contains(childPath)) {
                                        anyServices.add(childPath);
                                        subscribe(url2.setPath(child).addParameters(new String[]{Constants.INTERFACE_KEY, child, Constants.CHECK_KEY, String.valueOf(false)}), listener);
                                    }
                                });
                            }

                        });
                        zkListener = listeners.get(listener);
                    }

                    zkClient.create(DEFAULT_ROOT + regGrp, false);
                    List<String> services = zkClient.addChildListener(DEFAULT_ROOT + regGrp, zkListener);
                    if (services != null && services.size() > 0) {
                        services.forEach(service->subscribe(url2.setPath(service).addParameters(new String[]{Constants.INTERFACE_KEY, service, Constants.CHECK_KEY, String.valueOf(false)}), listener));
                    }
                }
            } else {
                final String serviceInterface = url2.getServiceInterface();
                if (url2.hasParameter(Constants.GROUP_KEY) && !Constants.ANY_VALUE.equals(url.getParameter(Constants.GROUP_KEY))) {
                    List<URL> urls = new ArrayList();

                    List<String> regGrps = toCategoriesPath(url2);

                    regGrps.forEach(regGrp->{
                        ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url2);
                        if (listeners == null) {
                            zkListeners.putIfAbsent(url2, new ConcurrentHashMap());
                            listeners = zkListeners.get(url2);
                        }
                        ChildListener zkListener = listeners.get(listener);
                        if (zkListener == null) {
                            listeners.putIfAbsent(listener, (parentPath, currentChilds) ->
                                    notify(url2, listener, toUrlsWithEmpty(url2, parentPath, currentChilds)));
                            zkListener = listeners.get(listener);
                        }

                        zkClient.create(regGrp, false);
                        List<String> children = zkClient.addChildListener(regGrp, zkListener);
                        if (children != null) {
                            urls.addAll(toUrlsWithEmpty(url2, regGrp, children));
                        }
                    });

                    this.notify(url2, listener, urls);
                } else {
                    final String regGrp = url2.getParameter(CANCER_GROUP);
                    ConcurrentMap<NotifyListener, ChildListener> listeners = zkListeners.get(url2);
                    if (listeners == null) {
                        zkListeners.putIfAbsent(url2, new ConcurrentHashMap());
                        listeners = zkListeners.get(url2);
                    }

                    ChildListener zkListener = listeners.get(listener);
                    if (zkListener == null) {
                        listeners.putIfAbsent(listener, (parentPath, currentChilds) -> {
                            if (currentChilds != null && currentChilds.size() > 0) {
                                currentChilds.forEach(child->{
                                    child = URL.decode(child);
                                    String childPath = DEFAULT_ROOT + regGrp + Constants.PATH_SEPARATOR + serviceInterface + Constants.PATH_SEPARATOR + child;
                                    if (!anyServices.contains(childPath)) {
                                        anyServices.add(childPath);
                                        subscribe(url2.setPath(child).addParameters(new String[]{Constants.GROUP_KEY, child, Constants.CHECK_KEY, String.valueOf(false)}), listener);
                                    }
                                });
                            }

                        });
                        zkListener = listeners.get(listener);
                    }

                    zkClient.create(DEFAULT_ROOT + regGrp + Constants.PATH_SEPARATOR  + serviceInterface, false);
                    List<String> groups = zkClient.addChildListener(DEFAULT_ROOT + regGrp + Constants.PATH_SEPARATOR + serviceInterface, zkListener);
                    if (groups != null && groups.size() > 0) {
                        groups.forEach(group->subscribe(url2.setPath(group).addParameters(new String[]{Constants.GROUP_KEY, group, Constants.CHECK_KEY, String.valueOf(false)}), listener));
                    }
                }
            }

        } catch (Throwable ex) {
            throw new RpcException("Failed to subscribe " + url + " to zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
        }
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
            logger.warn("Failed to close zookeeper client " + getUrl() + ", cause: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<URL> lookup(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("lookup url == null");
        } else {
            try {
                List<String> providers = new ArrayList();
                List<String> paths = this.toCategoriesPath(url);
                if (paths != null && paths.size() > 0) {
                    paths.forEach(path->{
                        List<String> children = this.zkClient.getChildren(path);
                        if (children != null) {
                            providers.addAll(children);
                        }
                    });
                }

                return toUrlsWithoutEmpty(url, providers);
            } catch (Throwable ex) {
                throw new RpcException("Failed to lookup " + url + " from zookeeper " + getUrl() + ", cause: " + ex.getMessage(), ex);
            }
        }
    }

    private String toUrlPath(URL url) {
        String reggrp = url.getParameter(CANCER_GROUP);
        if (reggrp == null) {
            url = url.addParameter(CANCER_GROUP, root.substring(DEFAULT_ROOT.length()));
        }

        String group = url.getParameter(Constants.GROUP_KEY);
        if (group == null) {
            url = url.addParameter(Constants.GROUP_KEY, Constants.DEFAULT_KEY);
        }

        return toCategoryPath(url) + Constants.PATH_SEPARATOR + URL.encode(url.toFullString());
    }

    private String toCategoryPath(URL url) {
        String group = url.getParameter(Constants.GROUP_KEY);
        if (group == null || Constants.ANY_VALUE.equals(group)) {
            group = Constants.DEFAULT_KEY;
        }

        return toServicePath(url) + Constants.PATH_SEPARATOR + group + Constants.PATH_SEPARATOR
                + url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY);
    }

    private String toServicePath(URL url) {
        String name = url.getServiceInterface();
        String regGrp = url.getParameter(CANCER_GROUP);
        return Constants.ANY_VALUE.equals(name) ? DEFAULT_ROOT + regGrp : DEFAULT_ROOT + regGrp + Constants.PATH_SEPARATOR + URL.encode(name);
    }

    private List<String> toCategoriesPath(URL url) {
        List<String> paths = new ArrayList();
        String[] categroies;
        if (Constants.ANY_VALUE.equals(url.getParameter(Constants.CATEGORY_KEY))) {
            categroies = new String[]{Constants.PROVIDERS_CATEGORY, Constants.CONSUMERS_CATEGORY, Constants.ROUTERS_CATEGORY, Constants.CONFIGURATORS_CATEGORY};
        } else {
            categroies = url.getParameter(Constants.CATEGORY_KEY, new String[]{Constants.PROVIDERS_CATEGORY});
        }

        String group = url.getParameter(Constants.GROUP_KEY);
        if (Constants.ANY_VALUE.equals(group)) {
            List<String> childPaths = zkClient.getChildren(toServicePath(url));
            if (childPaths != null && childPaths.size() > 0) {
                childPaths.forEach(childPath->{
                    for(int i = 0; i < categroies.length; ++i) {
                        String path = toServicePath(url) + Constants.PATH_SEPARATOR + childPath + Constants.PATH_SEPARATOR + categroies[i];
                        paths.add(path);
                    }
                });
            }
        } else {
            if (group == null) {
                group = Constants.DEFAULT_KEY;
            }

            for(int i = 0; i < categroies.length; ++i) {
                String path = toServicePath(url) + Constants.PATH_SEPARATOR + group + Constants.PATH_SEPARATOR + categroies[i];
                paths.add(path);
            }
        }

        return paths;
    }

    private List<URL> toUrlsWithEmpty(URL consumer, String path, List<String> providers) {
        List<URL> urls = toUrlsWithoutEmpty(consumer, providers);
        if (urls == null || urls.isEmpty()) {
            int i = path.lastIndexOf('/');
            String category = i < 0 ? path : path.substring(i + 1);
            URL empty = consumer.setProtocol(Constants.EMPTY_PROTOCOL).addParameter(Constants.CATEGORY_KEY, category);
            urls.add(empty);
        }

        return urls;
    }

    private List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList();
        if (providers != null && providers.size() > 0) {
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
}
