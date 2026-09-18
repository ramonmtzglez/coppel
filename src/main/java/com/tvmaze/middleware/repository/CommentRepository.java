package com.tvmaze.middleware.repository;

import com.tvmaze.middleware.document.CommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends MongoRepository<CommentDocument, String> {

    List<CommentDocument> findByShowId(Long showId);

    List<CommentDocument> findByShowIdIn(Collection<Long> showIds);
}
