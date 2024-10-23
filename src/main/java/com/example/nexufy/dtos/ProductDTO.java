package com.example.nexufy.dtos;

import java.io.Serializable;

public class ProductDTO implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String urlImage;
    private String state;

    // Almacena solo el ID del cliente asociado
    private String customerId;

    public ProductDTO(String id, String name, String description, double price,
                      String category, String urlImage, String state, String customerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.urlImage = urlImage;
        this.state = state;
        this.customerId = customerId;
    }

    public ProductDTO() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
