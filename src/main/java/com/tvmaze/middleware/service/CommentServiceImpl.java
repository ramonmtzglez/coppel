package com.tvmaze.middleware.service;

import com.tvmaze.middleware.document.CommentDocument;
import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;
import com.tvmaze.middleware.mapper.CommentMapper;
import com.tvmaze.middleware.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public CommentResponse create(Long showId, CommentRequest request) {
        CommentDocument document = CommentDocument.builder()
                .showId(showId)
                .comment(request.comment())
                .rating(request.rating())
                .createdAt(Instant.now())
                .build();

        CommentDocument saved = commentRepository.save(document);

        log.info("Comentario guardado para show id={} -> rating={}, comment='{}'",
                showId, saved.getRating(), saved.getComment());

        return CommentMapper.toResponse(saved);
    }
}
