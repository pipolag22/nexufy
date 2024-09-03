package com.example.nexufy.persistence.entities;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "comments")
public class RatingComment {

    @Id
    private String id;
    private String productId; // Para asociar el comentario con un producto específico
    private String text;
    private LocalDateTime date;
    private int rating; // Nuevo campo para la calificación

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getRating() { // Getter para rating
        return rating;
    }

    public void setRating(int rating) { // Setter para rating
        this.rating = rating;
    }
}
