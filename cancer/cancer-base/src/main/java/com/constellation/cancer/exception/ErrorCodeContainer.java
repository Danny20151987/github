package com.constellation.cancer.exception;

import com.alibaba.fastjson.JSON;
import com.constellation.cancer.core.config.ConfigurationListener;
import com.constellation.cancer.core.utils.CoreConstants;
import com.constellation.cancer.core.utils.ResourceUtil;
import com.constellation.cancer.exception.xml.ErrorCode;
import com.constellation.cancer.exception.xml.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 4:57 PM
 */
public class ErrorCodeContainer implements ConfigurationListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorCodeContainer.class);

    private static final Map<String, ConcurrentHashMap<String, HashMap<String, String>>> LANGUAGE_ERROR_MAPPINGS = new HashMap();
    private static final Map<String, ConcurrentHashMap<String, ConcurrentHashMap<String, ErrorCode>>> INNER_TO_OUTSIDE_CODE_MAPPING = new HashMap();
    private static final Map<String, ConcurrentHashMap<String, ConcurrentHashMap<String, ErrorCode>>> OUTSIDE_TO_INNER_CODE_MAPPING = new HashMap();
    private static List<String> configKeys;
    private static Resource[] locations;
    private static boolean inited = false;


    @Override
    public void resourceChanged(String configKey, Resource configResource) {
        LOGGER.info("resourceChanged configKey:{}  value: {}", configKey, configResource.getFilename());
        loadErrorCodeConfig(configResource);
    }

    @Override
    public void configChanged(String configKey, Properties propeties) {
        LOGGER.info("configKey:{}  {}", configKey, propeties);
    }

    @Override
    public List<String> getConfigKeys() {
        return configKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private static void init() {
        if (locations == null) {
            locations = ResourceUtil.getResource(ErrorConstants.DEFAULT_ERROR_CODE_CONFIG, "config/");
        }

        if (locations != null && locations.length > 0) {
            configKeys = new ArrayList();
        }

        Resource[] resources = locations;

        for(int i = 0; i < resources.length; ++i) {
            Resource resource = resources[i];
            configKeys.add(resource.getFilename());
            loadErrorCodeConfig(resource);
        }

        inited = true;
        print();
    }

    public static void print() {
        StringBuilder sb = new StringBuilder(200);
        String newLine = "\r\n";
        String tab = "\t";

        LANGUAGE_ERROR_MAPPINGS.forEach(
            (language,errCodes)-> {
                sb.append(language).append(newLine);
                errCodes.forEach(
                    (fileName,errCode) -> {
                        sb.append(tab).append(fileName).append(newLine);
                        errCode.forEach((code,msg)->sb.append(tab).append(tab).append(code).append("=").append(msg).append(newLine));
                    }
                );
            }
        );

        LOGGER.info("\r\n{}", sb);
    }

    private static void loadErrorCodeConfig(Resource res) {
        String file = res.getFilename();
        String language = file.substring(file.indexOf(ErrorConstants.ERR_FILE_MID) + 10, file.lastIndexOf("."));

        ConcurrentHashMap errorFileMap =  LANGUAGE_ERROR_MAPPINGS.get(language);
        if(errorFileMap == null){
            errorFileMap = new ConcurrentHashMap<>();
            LANGUAGE_ERROR_MAPPINGS.putIfAbsent(language,errorFileMap);
        }else{
            errorFileMap.remove(file);
        }

        errorFileMap.put(file, loadNewConfig(res,language));

        LOGGER.debug("error properties config:{}", JSON.toJSONString(errorFileMap));
        LOGGER.info("load error properties file  > {} success !", file);
    }

    private static HashMap<String, String> loadNewConfig(Resource res,String language) {
        InputStream in = null;

        HashMap<String, String> map = new HashMap();
        try {
            in = res.getInputStream();

            if (res.getFilename().endsWith(ErrorConstants.ERR_FILE_SUB)) {
                ErrorCodes errorCodes = parseErrorCodeXml(in);
                for(ErrorCode ec : errorCodes.getErrorCodes()){
                    map.put(ec.getCode(), ec.getMessage());
                    loadInToOutCodeMapping(ec,errorCodes,language);
                    loadOutToInCodeMapping(ec,errorCodes,language);
                }
            } else {
                Properties prop = new Properties();
                prop.load(in);
                prop.keySet().forEach(key-> map.put((String)key, prop.getProperty((String)key)));
            }

        } catch (IOException ex) {
            throw new RuntimeException(String.format("加载%s失败!", res.getFilename()), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LOGGER.warn("", ex);
            }

        }

        return map;
    }

    private static void loadInToOutCodeMapping(ErrorCode errorCode, ErrorCodes errorCodes, String language){

        if(language == null){
            language = ErrorConstants.DEFAULT_LANGUAGE;
        }

        ConcurrentHashMap inToOutMapping = INNER_TO_OUTSIDE_CODE_MAPPING.get(language);
        if(inToOutMapping == null){
            inToOutMapping = new ConcurrentHashMap();
            INNER_TO_OUTSIDE_CODE_MAPPING.put(language,inToOutMapping);
        }

        inToOutMapping.putIfAbsent(errorCode.getCode(), new ConcurrentHashMap());
        ConcurrentHashMap mapping = (ConcurrentHashMap) inToOutMapping.get(errorCode.getCode());
        mapping.put(errorCodes.getSysIndicate(),errorCode);
    }

    private static void loadOutToInCodeMapping(ErrorCode errorCode, ErrorCodes errorCodes, String language){

        if(language == null){
            language = ErrorConstants.DEFAULT_LANGUAGE;
        }

        ConcurrentHashMap outToInMapping = OUTSIDE_TO_INNER_CODE_MAPPING.get(language);
        if(outToInMapping == null){
            outToInMapping = new ConcurrentHashMap();
            OUTSIDE_TO_INNER_CODE_MAPPING.put(language,outToInMapping);
        }

        outToInMapping.putIfAbsent(errorCode.getOutCode(), new ConcurrentHashMap());
        ConcurrentHashMap mapping = (ConcurrentHashMap) outToInMapping.get(errorCode.getOutCode());
        mapping.put(errorCodes.getSysIndicate(),errorCode);
    }

    private static ErrorCodes parseErrorCodeXml(InputStream in) {
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(new Class[]{ErrorCodes.class}, Collections.emptyMap()).createUnmarshaller();
            XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
            xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);
            Object obj = unmarshaller.unmarshal(xmlInputFactory.createXMLStreamReader(new StreamSource(in)));
            return (ErrorCodes)obj;
        } catch (FactoryConfigurationError | XMLStreamException | JAXBException ex) {
            throw new RuntimeException("解析错误码xml文件错误!", ex);
        }
    }

    public static String getErrorMessage(String errorCode, String... pattern) {
        if (!inited) {
            init();
        }

        ConcurrentHashMap<String, HashMap<String, String>> errorFileMap = LANGUAGE_ERROR_MAPPINGS.get(CoreConstants.APP_ERROR_LANGUAGE);
        String message = null;


        Iterator iterator = errorFileMap.keySet().iterator();
        while(iterator.hasNext()){
            HashMap<String,String> errorMap = (HashMap<String,String>)iterator.next();
            if(errorMap.containsKey(errorCode)){
                message = errorMap.get(errorCode);
                break;
            }

        }

        if (!StringUtils.hasLength(message)) {
            LOGGER.warn("未找到错误码[{}]的定义!", errorCode);
            if (pattern != null && pattern.length > 0) {
                message = pattern.length > 1 ? Arrays.toString(pattern) : pattern[0];
            }
        }

        return MessageFormatter.arrayFormat(message, pattern).getMessage();
    }

}

