package com.example.nexufy.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "subscriptions")
public class Subscription {

    @Id
    private String id;

    @NotNull
    private String customerId; // Relación con el cliente

    @NotNull
    private Date startDate;

    @NotNull
    private Date endDate;

    @NotNull
    private String type; // Tipo de suscripción, por ejemplo, mensual, anual, etc.

    private double price;

    private boolean isActive;

    // Constructor, getters y setters

    public Subscription() {}

    public Subscription(String customerId, Date startDate, Date endDate, String type, double price, boolean isActive) {
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.price = price;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
