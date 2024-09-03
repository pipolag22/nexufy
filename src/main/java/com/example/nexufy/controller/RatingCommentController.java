package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.service.RatingCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class RatingCommentController {

    private final RatingCommentService ratingCommentService;

    @Autowired
    public RatingCommentController(RatingCommentService ratingCommentService) {
        this.ratingCommentService = ratingCommentService;
    }

    // Endpoint para agregar un nuevo comentario
    @PostMapping
    public ResponseEntity<RatingComment> addComment(@RequestBody RatingComment ratingComment) {
        RatingComment newComment = ratingCommentService.addComment(ratingComment);
        return ResponseEntity.ok(newComment);
    }

    // Endpoint para obtener todos los comentarios de un producto específico
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RatingComment>> getCommentsByProductId(@PathVariable String productId) {
        List<RatingComment> comments = ratingCommentService.getCommentsByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    // Endpoint para actualizar un comentario existente
    @PutMapping("/{id}")
    public ResponseEntity<RatingComment> updateComment(@PathVariable String id, @RequestBody RatingComment ratingComment) {
        Optional<RatingComment> updatedComment = ratingCommentService.updateComment(id, ratingComment);
        return updatedComment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para eliminar un comentario por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        ratingCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para obtener un comentario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<RatingComment> getCommentById(@PathVariable String id) {
        Optional<RatingComment> comment = ratingCommentService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
