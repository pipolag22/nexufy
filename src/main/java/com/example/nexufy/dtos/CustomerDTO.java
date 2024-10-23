package com.example.nexufy.dtos;

import com.example.nexufy.persistence.entities.Role;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class CustomerDTO implements Serializable {
    private String id;
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private boolean isSuspended;
    private Set<Role> role;

    // Nueva lista para almacenar los productos asociados al cliente
    private List<ProductDTO> products;

    public CustomerDTO(String id, String username, String name, String lastname, String email,
                       String phone, boolean isSuspended, Set<Role> role, List<ProductDTO> products) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.isSuspended = isSuspended;
        this.role = role;
        this.products = products;
    }


    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isSuspended() { return isSuspended; }
    public void setSuspended(boolean suspended) { isSuspended = suspended; }

    public Set<Role> getRole() { return role; }
    public void setRole(Set<Role> role) { this.role = role; }

    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }

    @Override
    public String toString() {
        return "CustomerDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isSuspended=" + isSuspended +
                ", products=" + products +
                '}';
    }
}
