package com.constellation.cancer.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:58 PM
 */
public class PrintArgumentUtil {

    private static Set<String> excludeProperties = new HashSet();

    private PrintArgumentUtil(String properties) {
        excludeProperties.addAll(Arrays.asList(properties.split(",")));
    }

    public static String toJSONString(Object object) {
        if (object == null) {
            return "";
        } else {
            return object instanceof String ? (String)object : JSON.toJSONString(object, new SimplePropertyPreFilter(new String[0]) {
                public boolean apply(JSONSerializer serializer, Object source, String name) {
                    return !PrintArgumentUtil.excludeProperties.contains(name);
                }
            }, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat});
        }
    }
}
