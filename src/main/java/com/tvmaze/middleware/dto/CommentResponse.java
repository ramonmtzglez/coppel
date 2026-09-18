package com.tvmaze.middleware.dto;

import java.time.Instant;

public record CommentResponse(
        String id,
        Long showId,
        String comment,
        Integer rating,
        Instant createdAt
) {
}
