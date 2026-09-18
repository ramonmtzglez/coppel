package com.tvmaze.middleware.controller;

import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;
import com.tvmaze.middleware.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void create_returns201_whenValid() throws Exception {
        when(commentService.create(eq(139L), any(CommentRequest.class)))
                .thenReturn(new CommentResponse("abc123", 139L, "Excelente", 5, Instant.now()));

        mockMvc.perform(post("/api/v1/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Excelente\",\"rating\":5}"))
                .andExpect(status().isCreated());
    }

    @Test
    void create_returns400_whenRatingGreaterThan5() throws Exception {
        mockMvc.perform(post("/api/v1/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"mala\",\"rating\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns400_whenRatingNull() throws Exception {
        mockMvc.perform(post("/api/v1/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"sin rating\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns400_whenCommentBlank() throws Exception {
        mockMvc.perform(post("/api/v1/shows/139/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"\",\"rating\":3}"))
                .andExpect(status().isBadRequest());
    }
}
