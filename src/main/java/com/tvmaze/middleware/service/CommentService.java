package com.tvmaze.middleware.service;

import com.tvmaze.middleware.dto.CommentDto;
import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CommentService {

    CommentResponse create(Long showId, CommentRequest request);

    List<CommentDto> findByShowId(Long showId);

    Map<Long, List<CommentDto>> findByShowIds(Collection<Long> showIds);
}
