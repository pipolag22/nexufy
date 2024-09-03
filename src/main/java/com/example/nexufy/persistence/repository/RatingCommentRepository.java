package com.example.nexufy.persistence.repository;

import com.example.nexufy.persistence.entities.RatingComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RatingCommentRepository extends MongoRepository<RatingComment, String> {
    // Cambia el tipo de retorno a RatingComment y el parámetro productId se mantiene igual
    List<RatingComment> findByProductId(String productId);
}
