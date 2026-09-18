package com.tvmaze.middleware.service;

import com.tvmaze.middleware.document.CommentDocument;
import com.tvmaze.middleware.dto.CommentRequest;
import com.tvmaze.middleware.dto.CommentResponse;
import com.tvmaze.middleware.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    private CommentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CommentServiceImpl(commentRepository);
    }

    @Test
    void create_savesCommentAndReturnsResponse() {
        CommentRequest request = new CommentRequest("Muy buena serie", 5);
        when(commentRepository.save(any(CommentDocument.class))).thenAnswer(invocation -> {
            CommentDocument doc = invocation.getArgument(0);
            doc.setId("abc123");
            return doc;
        });

        CommentResponse response = service.create(139L, request);

        assertThat(response.id()).isEqualTo("abc123");
        assertThat(response.showId()).isEqualTo(139L);
        assertThat(response.comment()).isEqualTo("Muy buena serie");
        assertThat(response.rating()).isEqualTo(5);

        ArgumentCaptor<CommentDocument> captor = ArgumentCaptor.forClass(CommentDocument.class);
        verify(commentRepository).save(captor.capture());
        assertThat(captor.getValue().getShowId()).isEqualTo(139L);
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }
}
