package com.constellation.cancer.core.config;

import com.constellation.cancer.core.utils.CoreConstants;
import com.constellation.cancer.core.utils.ResourceUtil;
import com.constellation.cancer.core.utils.StringUtils;
import com.constellation.cancer.exception.PlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/22 8:29 PM
 */
public class ParamsContainer implements ConfigurationListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamsContainer.class);
    final transient ReentrantLock lock = new ReentrantLock();
    private static Map<String, Map<String, String>> params = new ConcurrentHashMap();
    private Resource[] locations;
    private List<String> configKeys;


    public ParamsContainer() {
    }

    @Override
    public void resourceChanged(String configKey, Resource configResource) {
        LOGGER.info("resourceChanged configKey:{}  value: {}", configKey, configResource.getFilename());
        this.loadConfigFile(configResource);
    }

    @Override
    public void configChanged(String configKey, Properties propeties) {
        LOGGER.info("configKey:{}  {}", configKey, propeties);
    }

    @Override
    public List<String> getConfigKeys() {
        return this.configKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (this.locations == null) {
            if (this.configKeys == null) {
                this.locations = ResourceUtil.getResource(CoreConstants.DEFAULT_PARAMS_CONFIG, "config/");
            } else {
                this.locations = ResourceUtil.getResource(this.configKeys);
            }
        } else if (this.configKeys != null) {
            this.locations = ResourceUtil.getResource(this.configKeys);
        }

        if (this.configKeys == null) {
            this.configKeys = new ArrayList();
        }

        if (this.locations != null) {
            Resource[] resources = this.locations;

            for(int i = 0; i < resources.length; i++) {
                Resource res = resources[i];
                this.configKeys.add(res.getFilename());
                this.loadConfigFile(res);
            }
        }

        print();

    }

    private void print(){

        StringBuilder sb = new StringBuilder(200);
        String newLine = "\r\n";
        String tab = "\t";

        if(params != null){

            params.keySet().forEach(
                fileName->{
                    sb.append(fileName).append(newLine);
                    Map param = params.get(fileName);
                    param.keySet().forEach(
                        key->sb.append(tab).append(key).append("=").append(param.get(key))
                    );
                }
            );

            LOGGER.info("\r\n{}", sb);
        }
    }

    private void loadConfigFile(Resource res) {
        if (res != null) {
            InputStream in = null;
            String fileName = res.getFilename();
            this.lock.lock();

            try {
                in = res.getInputStream();
                Properties prop = new Properties();
                prop.load(in);
                ConcurrentHashMap<String, String> newParams = new ConcurrentHashMap();

                prop.keySet().forEach(key-> newParams.put((String)key, prop.getProperty((String)key)));

                params.put(fileName, newParams);
                LOGGER.info("load prams config  file  > {} success !", fileName);
            } catch (IOException ex) {
                LOGGER.warn("", ex);
            } finally {
                this.lock.unlock();
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    LOGGER.warn("", ex);
                }

            }
        }

    }

    public static Object getParam(String key, int type, MathContext mc, String format) throws ParseException{
        if(params == null)
            return null;
        Map<String,String> allParams = new ConcurrentHashMap<>();
        params.keySet().forEach(fileName->allParams.putAll(params.get(fileName)));

        String value = allParams.get(key);

        if(StringUtils.isEmpty(value)){
            return null;
        }else{
            try {
                switch(type) {
                    case CoreConstants.VALUE_TYPE_DATE:
                        if(StringUtils.isEmpty(format))
                            format = CoreConstants.DEFAULT_DATE_FORMAT;
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        return sdf.parse(value);
                    case CoreConstants.VALUE_TYPE_BIGDECIMAL:
                        if (mc != null) {
                            return new BigDecimal(value, mc);
                        }

                        return new BigDecimal(value);
                    case CoreConstants.VALUE_TYPE_STRING:
                        return value;
                    case CoreConstants.VALUE_TYPE_INTEGER:
                        return Integer.parseInt(value);
                    default:
                        throw new IllegalArgumentException("不支持的参数类型，请检查!");
                }
            } catch (ParseException ex) {
                throw new PlatformException(ex);
            }
        }
    }
}
