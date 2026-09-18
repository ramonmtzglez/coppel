package com.tvmaze.middleware.mapper;

import com.tvmaze.middleware.document.CommentDocument;
import com.tvmaze.middleware.dto.CommentResponse;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentResponse toResponse(CommentDocument doc) {
        return new CommentResponse(
                doc.getId(),
                doc.getShowId(),
                doc.getComment(),
                doc.getRating(),
                doc.getCreatedAt()
        );
    }
}
