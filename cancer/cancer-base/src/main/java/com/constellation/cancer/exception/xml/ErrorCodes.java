package com.constellation.cancer.exception.xml;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/22 11:55 AM
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "errorCodes")
public class ErrorCodes {
    @XmlAttribute(required = true)
    private String sysIndicate;
    @XmlElement(name="errorCode")
    private ArrayList<ErrorCode> errorCodes;

    public ErrorCodes() {
    }

    public String getSysIndicate() {
        return sysIndicate;
    }

    public void setSysIndicate(String sysIndicate) {
        this.sysIndicate = sysIndicate;
    }

    public ArrayList<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(ArrayList<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public String toString() {
        return "ErrorCodes [sysIndicate=" + this.sysIndicate + ", errorCode=" + this.errorCodes + "]";
    }
}
