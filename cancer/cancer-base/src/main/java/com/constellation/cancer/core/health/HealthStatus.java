package com.constellation.cancer.core.health;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 11:31 AM
 */
public final class HealthStatus {

    public static final HealthStatus UNKNOWN = new HealthStatus("UNKNOWN");
    public static final HealthStatus UP = new HealthStatus("UP");
    public static final HealthStatus DOWN = new HealthStatus("DOWN");
    public static final HealthStatus OUT_OF_SERVICE = new HealthStatus("OUT_OF_SERVICE");
    private final String code;
    private final String description;

    public HealthStatus(String code) {
        this(code, "");
    }

    public HealthStatus(String code, String description) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(description, "Description must not be null");
        this.code = code;
        this.description = description;
    }

    @JSONField(
            name = "status"
    )
    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.code;
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj != null && obj instanceof HealthStatus ? ObjectUtils.nullSafeEquals(this.code, ((HealthStatus)obj).code) : false;
        }
    }
}
