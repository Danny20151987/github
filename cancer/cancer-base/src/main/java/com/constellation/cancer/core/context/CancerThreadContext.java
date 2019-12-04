package com.constellation.cancer.core.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 5:01 PM
 */
public class CancerThreadContext {

    public static final String THREAD_SYS_SEQ_NO = "threadSysSeqNo";

    private static final ThreadLocal<Map<String, Object>> CTX_HOLDER = ThreadLocal.withInitial(()-> new HashMap<>());

    public static final  <T> T getContext(String key) {
       return (T)CTX_HOLDER.get().get(key);
    }

    public static String getSysSeqNo() {
        return (String)getContext(THREAD_SYS_SEQ_NO);

    }
}
