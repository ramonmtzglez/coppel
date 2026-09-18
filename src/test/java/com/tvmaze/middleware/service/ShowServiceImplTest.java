package com.tvmaze.middleware.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvmaze.middleware.client.TvMazeClient;
import com.tvmaze.middleware.document.ShowDocument;
import com.tvmaze.middleware.dto.CommentDto;
import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.dto.tvmaze.TvMazeImage;
import com.tvmaze.middleware.dto.tvmaze.TvMazeNetwork;
import com.tvmaze.middleware.dto.tvmaze.TvMazeRating;
import com.tvmaze.middleware.dto.tvmaze.TvMazeSearchItem;
import com.tvmaze.middleware.dto.tvmaze.TvMazeShow;
import com.tvmaze.middleware.exception.ShowNotFoundException;
import com.tvmaze.middleware.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowServiceImplTest {

    @Mock
    private TvMazeClient tvMazeClient;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private CommentService commentService;

    private ShowServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ShowServiceImpl(tvMazeClient, showRepository, commentService, new ObjectMapper());
    }

    @Test
    void search_mapsTvMazeResultsToResponseWithComments() {
        TvMazeShow show = sampleShow();
        when(tvMazeClient.searchShows("girls"))
                .thenReturn(List.of(new TvMazeSearchItem(0.9, show)));
        when(commentService.findByShowIds(any()))
                .thenReturn(Map.of(1L, List.of(new CommentDto("Excelente", 5))));

        List<ShowResponse> result = service.search("girls");

        assertThat(result).hasSize(1);
        ShowResponse first = result.get(0);
        assertThat(first.id()).isEqualTo(1L);
        assertThat(first.name()).isEqualTo("Under the Dome");
        assertThat(first.channel()).isEqualTo("CBS");
        assertThat(first.genres()).containsExactly("Drama", "Science-Fiction", "Thriller");
        assertThat(first.comments()).containsExactly(new CommentDto("Excelente", 5));
    }

    @Test
    void getShow_returnsCachedWithComments() {
        ShowDocument doc = ShowDocument.builder().id(1L).name("Under the Dome").build();
        when(showRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(commentService.findByShowId(1L)).thenReturn(List.of(new CommentDto("Buenisima", 4)));

        ShowDetailResponse result = service.getShow(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Under the Dome");
        assertThat(result.comments()).containsExactly(new CommentDto("Buenisima", 4));
        verify(tvMazeClient, never()).getShow(anyLong());
        verify(showRepository, never()).save(any());
    }

    @Test
    void getShow_fetchesAndCachesOnMiss() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());
        when(tvMazeClient.getShow(1L)).thenReturn(sampleShow());
        when(commentService.findByShowId(1L)).thenReturn(List.of());

        ShowDetailResponse result = service.getShow(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.rating()).isEqualTo(6.6);
        assertThat(result.comments()).isEmpty();
        verify(showRepository).save(any(ShowDocument.class));
    }

    @Test
    void getShow_throwsNotFoundWhenTvMazeReturns404() {
        when(showRepository.findById(1L)).thenReturn(Optional.empty());
        when(tvMazeClient.getShow(1L))
                .thenThrow(WebClientResponseException.create(404, "Not Found", new HttpHeaders(), new byte[0], null));

        assertThatThrownBy(() -> service.getShow(1L))
                .isInstanceOf(ShowNotFoundException.class);
    }

    private TvMazeShow sampleShow() {
        return new TvMazeShow(
                1L,
                "https://www.tvmaze.com/shows/1/under-the-dome",
                "Under the Dome",
                "Scripted",
                "English",
                List.of("Drama", "Science-Fiction", "Thriller"),
                "Ended",
                60,
                "2013-06-24",
                "2015-09-10",
                "http://www.cbs.com/shows/under-the-dome/",
                new TvMazeRating(6.6),
                new TvMazeNetwork("CBS"),
                null,
                new TvMazeImage("medium.jpg", "original.jpg"),
                "<p>summary</p>"
        );
    }
}
