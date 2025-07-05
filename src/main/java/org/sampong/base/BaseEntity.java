package org.sampong.base;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@MappedSuperclass
public class BaseEntity {
    private Boolean status;
    @CreationTimestamp
    private Date created;
    @UpdateTimestamp
    private Date updated;
    @Version // Optimistic lock
    private Integer version = 0;

    public BaseEntity() {}

    public BaseEntity(Boolean status, Integer version, Date updated, Date created) {
        this.status = status;
        this.version = version;
        this.updated = updated;
        this.created = created;
    }

    public Boolean status() {
        return status;
    }

    public BaseEntity setStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public Integer version() {
        return version;
    }

    public BaseEntity setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Date updated() {
        return updated;
    }

    public BaseEntity setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }

    public Date created() {
        return created;
    }

    public BaseEntity setCreated(Date created) {
        this.created = created;
        return this;
    }
}
