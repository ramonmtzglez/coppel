package com.tvmaze.middleware.service;

import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;

public interface CommentService {

    CommentResponse create(Long showId, CommentRequest request);
}
