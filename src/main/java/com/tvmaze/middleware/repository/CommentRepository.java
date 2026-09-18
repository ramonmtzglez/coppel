package com.tvmaze.middleware.repository;

import com.tvmaze.middleware.document.CommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<CommentDocument, String> {
}
