package com.tvmaze.middleware.repository;

import com.tvmaze.middleware.document.ShowDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowRepository extends MongoRepository<ShowDocument, Long> {
}
