package com.constellation.cancer.impl;

import com.constellation.cancer.api.ISysDate;

import java.util.Date;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:54 PM
 */
public class DefaultSysDateImpl implements ISysDate {

    @Override
    public Date getSysDate() {
        return new Date();
    }
}
