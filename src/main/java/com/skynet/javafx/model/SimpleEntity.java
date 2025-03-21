package com.skynet.javafx.model;

import javax.persistence.*;
import java.io.Serializable;
import com.skynet.javafx.constants.CommonConstants;

@MappedSuperclass
public abstract class SimpleEntity implements Serializable {

    protected static final long serialVersionUID = CommonConstants.SERIAL_VERSION_UID;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public SimpleEntity() {
        super();
    }

    public SimpleEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SimpleEntity)) return false;

        SimpleEntity other = (SimpleEntity) obj;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
