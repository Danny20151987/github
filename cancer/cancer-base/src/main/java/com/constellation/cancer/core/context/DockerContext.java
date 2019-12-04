package com.constellation.cancer.core.context;

import com.alibaba.fastjson.JSON;
import com.constellation.cancer.core.utils.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 8:46 AM
 */
public class DockerContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerContext.class);

    private final String dockerServer = System.getenv(CoreConstants.DOCKER_HOST);
    private String dockerContainerId;
    private boolean isInDocker = false;
    private String dockerHostIp;
    private Map portMap;


    public DockerContext() {
    }

    public void init() {
        if (this.dockerServer != null) {
            String containerId = this.initContainerId();
            if (containerId != null) {
                this.isInDocker = true;
                LOGGER.info("The application is running in docker, the environment DOCKER_HOST=" + this.dockerServer);
                String container_json = this.retrieveContainerInfo(this.dockerServer, containerId);
                this.getPortsMap(container_json);
                if (this.dockerHostIp == null) {
                    this.dockerHostIp = this.dockerServer.substring(0, this.dockerServer.indexOf(":"));
                }
            } else {
                LOGGER.error("Can not get the mapping port number in Docker container, the environment DOCKER_HOST=" + this.dockerServer);
            }
        }

    }

    protected String initContainerId() {
        if (this.dockerContainerId != null) {
            return this.dockerContainerId;
        } else {
            File hosts = new File(CoreConstants.MTAB_PATH);
            if (hosts.exists() && !hosts.isDirectory()) {
                BufferedReader reader = null;

                Pattern p;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(hosts)));
                    String line;

                    while((line = reader.readLine()) != null && line.indexOf("docker") == -1) {
                        continue;
                    }

                    if (line != null) {
                        p = Pattern.compile(CoreConstants.DOCKER_ID_MATCH);
                        Matcher m = p.matcher(line);
                        boolean found = m.find();
                        if (!found) {
                            LOGGER.info("No container id found!");
                            return null;
                        }

                        this.dockerContainerId = m.group();
                        return this.dockerContainerId;
                    }

                    return null;
                } catch (Exception ex) {
                    LOGGER.error("InitContainerId Exception", ex);
                    return null;
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        LOGGER.error("InitContainerId close resource Exception", ex);
                    }

                }
            } else {
                LOGGER.error(CoreConstants.MTAB_PATH + " file not found!");
                return null;
            }
        }
    }

    protected Map getPortsMap(String json) {
        LOGGER.info("json = " + json);
        if (this.portMap != null) {
            return this.portMap;
        } else {
            try {
                Map<String, Map<String, Object>> maps = (Map) JSON.parseObject(json, Map.class);
                if (maps != null && maps.size() != 0) {
                    Map nws = maps.get(CoreConstants.NETWORK_SETTINGS);
                    if (nws != null && nws.size() != 0) {
                        Map ports = (Map)nws.get(CoreConstants.PORTS);
                        if (ports != null && ports.size() != 0) {
                            Iterator it = ports.keySet().iterator();
                            this.portMap = new HashMap();

                            while(it.hasNext()) {
                                String port = (String)it.next();
                                List mapping = (List)ports.get(port);
                                if (mapping != null && mapping.size() > 0) {
                                    Map portMap = (Map)mapping.get(0);
                                    String mappedHost = (String)portMap.get(CoreConstants.HOST_IP);
                                    String mappedPort = (String)portMap.get(CoreConstants.HOST_PORT);
                                    if (this.dockerHostIp == null && !"0.0.0.0".equals(mappedHost)) {
                                        this.dockerHostIp = mappedHost;
                                    }

                                    this.portMap.put(port.replaceAll("/tcp", ""), mappedPort);
                                }
                            }

                            return this.portMap;
                        } else {
                            LOGGER.warn("No Ports!");
                            return null;
                        }
                    } else {
                        LOGGER.warn("No NetworkSettings!");
                        return null;
                    }
                } else {
                    LOGGER.warn("Cannot change json to map!");
                    return null;
                }
            } catch (Exception ex) {
                LOGGER.error("GetPortsMap Exception", ex);
                return null;
            }
        }
    }

    private String retrieveContainerInfo(String dockerServer, String containerId) {
        String json = null;
        return (String)json;
    }

    public boolean isInDocker() {
        return this.isInDocker;
    }

    public String getDockerHostIp() {
        return this.dockerHostIp;
    }

    public String getMappingPort(String port) {
        return (String)this.portMap.get(port);
    }

    public String getContainerId() {
        return this.dockerContainerId;
    }

    public String getShortContainerId() {
        String containerId = this.getContainerId();
        return containerId != null && containerId.length() > 12 ? containerId.substring(0, 12) : null;
    }

}
