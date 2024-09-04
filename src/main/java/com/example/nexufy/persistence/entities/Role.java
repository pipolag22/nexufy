package com.example.nexufy.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Roles")
public class Role {
    @Id
    private String id;

    private EnumRoles name;

    public Role() {

    }

    public Role(EnumRoles name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EnumRoles getName() {
        return name;
    }

    public void setName(EnumRoles name) {
        this.name = name;
    }
}
