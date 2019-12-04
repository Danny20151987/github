package com.constellation.cancer.param;

import java.io.Serializable;

/**
 * @author hudejian
 * @DESC
 * @date 2019/12/2 4:44 PM
 */
public class StringOutput implements Serializable {
    private static final long serialVersionUID = -1017423620112539151L;

    private String body;

    public StringOutput() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
