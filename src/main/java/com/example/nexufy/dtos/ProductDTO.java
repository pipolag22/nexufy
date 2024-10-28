package com.example.nexufy.dtos;

import com.example.nexufy.persistence.entities.Product; // Importamos Product para mapear
import java.io.Serializable;
import java.time.LocalDateTime;

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

    private LocalDateTime publicationDate;

    // Constructor completo incluyendo publicationDate
    public ProductDTO(String id, String name, String description, double price,
                      String category, String urlImage, String state, String customerId,
                      LocalDateTime publicationDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.urlImage = urlImage;
        this.state = state;
        this.customerId = customerId;
        this.publicationDate = publicationDate;
    }

    // Constructor sin publicationDate
    public ProductDTO(String id, String name, String description, double price,
                      String category, String urlImage, String state, String customerId) {
        this(id, name, description, price, category, urlImage, state, customerId, null);
    }

    public ProductDTO() {}

    // Constructor que acepta un objeto Product y mapea sus atributos a ProductDTO
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.category = product.getCategory();
        this.urlImage = product.getUrlImage();
        this.state = product.getState();
        this.customerId = product.getCustomerId(); // Asume que Product tiene un campo customerId
        this.publicationDate = product.getPublicationDate(); // Si Product tiene este campo
    }

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

    public LocalDateTime getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDateTime publicationDate) { this.publicationDate = publicationDate; }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", state='" + state + '\'' +
                ", customerId='" + customerId + '\'' +
                ", publicationDate=" + publicationDate +
                '}';
    }
}
