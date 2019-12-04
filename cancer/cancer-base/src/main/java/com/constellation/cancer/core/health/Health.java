package com.constellation.cancer.core.health;

import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/21 11:30 AM
 */
public class Health {

    private final HealthStatus status;
    private final Map<String, Object> details;

    private Health(Health.Builder builder) {
        Assert.notNull(builder, "Builder must not be null");
        this.status = builder.status;
        this.details = Collections.unmodifiableMap(builder.details);
    }

    public HealthStatus getStatus() {
        return this.status;
    }

    public Map<String, Object> getDetails() {
        return this.details;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj instanceof Health) {
            Health other = (Health)obj;
            return this.status.equals(other.status) && this.details.equals(other.details);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hashCode = this.status.hashCode();
        return 13 * hashCode + this.details.hashCode();
    }

    public String toString() {
        return this.getStatus() + " " + this.getDetails();
    }

    public static Health.Builder unknown() {
        return status(HealthStatus.UNKNOWN);
    }

    public static Health.Builder up() {
        return status(HealthStatus.UP);
    }

    public static Health.Builder down(Exception ex) {
        return down().withException(ex);
    }

    public static Health.Builder down() {
        return status(HealthStatus.DOWN);
    }

    public static Health.Builder outOfService() {
        return status(HealthStatus.OUT_OF_SERVICE);
    }

    public static Health.Builder status(String statusCode) {
        return status(new HealthStatus(statusCode));
    }

    public static Health.Builder status(HealthStatus status) {
        return new Health.Builder(status);
    }

    public static class Builder {
        private HealthStatus status;
        private final Map<String, Object> details;

        public Builder() {
            this.status = HealthStatus.UNKNOWN;
            this.details = new LinkedHashMap();
        }

        public Builder(HealthStatus status) {
            Assert.notNull(status, "Status must not be null");
            this.status = status;
            this.details = new LinkedHashMap();
        }

        public Builder(HealthStatus status, Map<String, ?> details) {
            Assert.notNull(status, "Status must not be null");
            Assert.notNull(details, "Details must not be null");
            this.status = status;
            this.details = new LinkedHashMap(details);
        }

        public Health.Builder withException(Exception ex) {
            Assert.notNull(ex, "Exception must not be null");
            return this.withDetail("error", ex.getClass().getName() + ": " + ex.getMessage());
        }

        public Health.Builder withDetail(String key, Object data) {
            Assert.notNull(key, "Key must not be null");
            Assert.notNull(data, "Data must not be null");
            this.details.put(key, data);
            return this;
        }

        public Health.Builder unknown() {
            return this.status(HealthStatus.UNKNOWN);
        }

        public Health.Builder up() {
            return this.status(HealthStatus.UP);
        }

        public Health.Builder down(Exception ex) {
            return this.down().withException(ex);
        }

        public Health.Builder down() {
            return this.status(HealthStatus.DOWN);
        }

        public Health.Builder outOfService() {
            return this.status(HealthStatus.OUT_OF_SERVICE);
        }

        public Health.Builder status(String statusCode) {
            return this.status(new HealthStatus(statusCode));
        }

        public Health.Builder status(HealthStatus status) {
            this.status = status;
            return this;
        }

        public Health build() {
            return new Health(this);
        }
    }
}
