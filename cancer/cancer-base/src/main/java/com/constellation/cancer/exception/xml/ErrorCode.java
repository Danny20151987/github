package com.constellation.cancer.exception.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/22 11:53 AM
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "errorCode")
public class ErrorCode {
    @XmlAttribute(required = true)
    private String code;
    @XmlAttribute(required = true)
    private String message;
    @XmlAttribute(required = true)
    private String outCode;
    @XmlAttribute
    private String outMessage;

    public ErrorCode() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOutCode() {
        return this.outCode;
    }

    public void setOutCode(String outCode) {
        this.outCode = outCode;
    }

    public String getOutMessage() {
        return this.outMessage;
    }

    public void setOutMessage(String outMessage) {
        this.outMessage = outMessage;
    }

    public int hashCode() {
        int result = 31 * 1 + (this.code == null ? 0 : this.code.hashCode());
        return result;
    }



    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            ErrorCode other = (ErrorCode)obj;
            if (this.code == null) {
                if (other.code != null) {
                    return false;
                }
            } else if (!this.code.equals(other.code)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return "ErrorCode [code=" + this.code + ", message=" + this.message + ", outCode=" + this.outCode + ", outMessage=" + this.outMessage + "]";
    }
}
