package com.example.nexufy.service;

import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.persistence.repository.RatingCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingCommentService {

    private final RatingCommentRepository ratingCommentRepository;

    @Autowired
    public RatingCommentService(RatingCommentRepository ratingCommentRepository) {
        this.ratingCommentRepository = ratingCommentRepository;
    }

    // Método para agregar un nuevo comentario
    public RatingComment addComment(RatingComment ratingComment) {
        return ratingCommentRepository.save(ratingComment);
    }

    // Método para obtener todos los comentarios de un producto específico
    public List<RatingComment> getCommentsByProductId(String productId) {
        return ratingCommentRepository.findByProductId(productId);
    }

    // Método para actualizar un comentario existente
    public Optional<RatingComment> updateComment(String id, RatingComment ratingComment) {
        return ratingCommentRepository.findById(id).map(existingComment -> {
            existingComment.setText(ratingComment.getText());
            existingComment.setRating(ratingComment.getRating());
            existingComment.setDate(ratingComment.getDate());
            return ratingCommentRepository.save(existingComment);
        });
    }

    // Método para eliminar un comentario por su ID
    public void deleteComment(String id) {
        ratingCommentRepository.deleteById(id);
    }

    // Método para obtener un comentario por su ID
    public Optional<RatingComment> getCommentById(String id) {
        return ratingCommentRepository.findById(id);
    }
}
