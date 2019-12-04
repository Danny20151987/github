package com.constellation.cancer.utils;

import com.constellation.cancer.api.ISysDate;
import com.constellation.cancer.core.context.ContextUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:47 PM
 */
public class BeanUtil {

    private static final String DEFAULT_SYSDATE_BEAN_NAME = "defaultSysDateImpl";
    private static ISysDate sysDateImpl;

    public static ISysDate getSysDateImpl() {
        if (sysDateImpl == null) {
            if (ContextUtils.getSpringCancerContext() == null || ContextUtils.getSpringAppContext() == null) {
                return null;
            }

            Map<String, ISysDate> generateSeqNoImplMap = ContextUtils.getSpringCancerContext().getBeansOfType(ISysDate.class);
            Map<String, ISysDate> generateSeqNoImplAppMap = ContextUtils.getSpringAppContext().getBeansOfType(ISysDate.class);
            if (generateSeqNoImplAppMap.size() > 0) {
                generateSeqNoImplMap.putAll(generateSeqNoImplAppMap);
            }

            if (generateSeqNoImplMap.size() == 1) {
                sysDateImpl = generateSeqNoImplMap.get(DEFAULT_SYSDATE_BEAN_NAME);
            } else {
                Set<Map.Entry<String, ISysDate>> set = generateSeqNoImplMap.entrySet();
                Iterator it = set.iterator();

                while(it.hasNext()) {
                    Map.Entry<String, ISysDate> entry = (Map.Entry)it.next();
                    if (!DEFAULT_SYSDATE_BEAN_NAME.equals(entry.getKey())) {
                        sysDateImpl = entry.getValue();
                        break;
                    }
                }
            }
        }

        return sysDateImpl;
    }
}
