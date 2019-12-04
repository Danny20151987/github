package com.constellation.cancer.utils;


import java.util.Date;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:46 PM
 */
public class SysUtil {

    public static Date getSysDate() {
        return BeanUtil.getSysDateImpl() != null ? BeanUtil.getSysDateImpl().getSysDate() : new Date();
    }
}
