package com.constellation.cancer.core.context;

import com.constellation.cancer.core.component.CancerComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 9:30 AM
 */
public class CancerContext {

    public final static Map<String, CancerComponent> cancerComponents = new HashMap();
    public Properties configuration = new Properties();

    public CancerContext() {

    }

    public static void addCancerComponent(String componentId,CancerComponent cancerComponent){
        cancerComponents.put(componentId,cancerComponent);
    }


}
