package com.tvmaze.middleware.service;

import com.tvmaze.middleware.document.CommentDocument;
import com.tvmaze.middleware.dto.CommentDto;
import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;
import com.tvmaze.middleware.mapper.CommentMapper;
import com.tvmaze.middleware.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<CommentDto> findByShowId(Long showId) {
        return commentRepository.findByShowId(showId).stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @Override
    public Map<Long, List<CommentDto>> findByShowIds(Collection<Long> showIds) {
        if (showIds == null || showIds.isEmpty()) {
            return Map.of();
        }
        return commentRepository.findByShowIdIn(showIds).stream()
                .collect(Collectors.groupingBy(
                        CommentDocument::getShowId,
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));
    }
}
