package com.constellation.cancer.param;

import java.io.Serializable;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:37 PM
 */
public class BaseOutput implements Serializable {

    private static final long serialVersionUID = 8959575896024921807L;

    private ResponseData respData = new ResponseData();

    public BaseOutput() {
    }


    public ResponseData getRespData() {
        return respData;
    }

    public void setRespData(ResponseData respData) {
        this.respData = respData;
    }
}
