package com.constellation.cancer.param;


import com.constellation.cancer.commons.Constants;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:39 PM
 */
public class ResponseData implements Serializable {

    private static final long serialVersionUID = 307852242439207818L;

    private String code = Constants.ResponseCode.SUCCESS;
    private String message = null;
    private String serverIp;
    private Date returnTime;
    private Date servcDate;
    private String servcSeqNo;
    private long recordCount;
    private String type = Constants.ResponseType.SUCCESS;

    public ResponseData() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Date getServcDate() {
        return servcDate;
    }

    public void setServcDate(Date servcDate) {
        this.servcDate = servcDate;
    }

    public String getServcSeqNo() {
        return servcSeqNo;
    }

    public void setServcSeqNo(String servcSeqNo) {
        this.servcSeqNo = servcSeqNo;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
