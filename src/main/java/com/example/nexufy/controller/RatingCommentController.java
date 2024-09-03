package com.example.nexufy.controller;

import com.example.nexufy.persistence.entities.RatingComment;
import com.example.nexufy.service.RatingCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
