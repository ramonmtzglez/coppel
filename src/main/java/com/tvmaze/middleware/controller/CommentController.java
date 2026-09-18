package com.tvmaze.middleware.controller;

import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;
import com.tvmaze.middleware.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{showId}/comments")
    public ResponseEntity<CommentResponse> create(
            @PathVariable @Positive(message = "show id must be positive") Long showId,
            @Valid @RequestBody CommentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(showId, request));
    }
}
