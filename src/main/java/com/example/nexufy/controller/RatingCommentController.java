package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.service.RatingCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rating-comments")
public class RatingCommentController {

    private final RatingCommentService ratingCommentService;

    @Autowired
    public RatingCommentController(RatingCommentService ratingCommentService) {
        this.ratingCommentService = ratingCommentService;
    }

    @PostMapping
    public ResponseEntity<RatingComment> addComment(@RequestBody RatingComment ratingComment) {
        // Validación básica para la calificación
        if (ratingComment.getRating() < 1 || ratingComment.getRating() > 5) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        RatingComment savedRatingComment = ratingCommentService.addComment(ratingComment);
        return new ResponseEntity<>(savedRatingComment, HttpStatus.CREATED);
    }

    // Endpoint para actualizar un comentario existente
    @PutMapping("/{id}")
    public ResponseEntity<RatingComment> updateComment(@PathVariable String id, @RequestBody RatingComment ratingComment) {
        Optional<RatingComment> updatedComment = ratingCommentService.updateComment(id, ratingComment);
        return updatedComment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para obtener todos los comentarios de un producto específico
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RatingComment>> getCommentsByProductId(@PathVariable String productId) {
        List<RatingComment> comments = ratingCommentService.getCommentsByProductId(productId);
        return ResponseEntity.ok(comments);
    }



    // Nuevo método para eliminar un comentario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        // Llama al servicio para eliminar el comentario
        ratingCommentService.deleteComment(id);
        // Devuelve una respuesta con el estado 204 No Content si la eliminación fue exitosa
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
