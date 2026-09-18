package com.tvmaze.middleware.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvmaze.middleware.client.TvMazeClient;
import com.tvmaze.middleware.document.ShowDocument;
import com.tvmaze.middleware.dto.CommentDto;
import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.dto.tvmaze.TvMazeSearchItem;
import com.tvmaze.middleware.dto.tvmaze.TvMazeShow;
import com.tvmaze.middleware.exception.ShowNotFoundException;
import com.tvmaze.middleware.mapper.ShowMapper;
import com.tvmaze.middleware.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final TvMazeClient tvMazeClient;
    private final ShowRepository showRepository;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @Override
    public List<ShowResponse> search(String query) {
        List<TvMazeShow> shows = tvMazeClient.searchShows(query).stream()
                .filter(item -> item.show() != null)
                .map(TvMazeSearchItem::show)
                .toList();

        List<Long> showIds = shows.stream()
                .map(TvMazeShow::id)
                .filter(id -> id != null)
                .toList();

        Map<Long, List<CommentDto>> commentsByShow = commentService.findByShowIds(showIds);

        List<ShowResponse> results = shows.stream()
                .map(show -> ShowMapper.toResponse(show, commentsByShow.getOrDefault(show.id(), List.of())))
                .toList();

        try {
            log.info("TV Maze search [query='{}'] -> {} result(s)\n{}",
                    query,
                    results.size(),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(results));
        } catch (JsonProcessingException e) {
            log.warn("No se pudo serializar el resultado para log: {}", e.getMessage());
        }

        return results;
    }

    @Override
    public ShowDetailResponse getShow(Long id) {
        Optional<ShowDocument> cached = showRepository.findById(id);
        if (cached.isPresent()) {
            log.info("Cache HIT para show id={}", id);
            return ShowMapper.toDetailResponse(cached.get(), commentService.findByShowId(id));
        }

        log.info("Cache MISS para show id={} -> consultando TV Maze", id);

        TvMazeShow show;
        try {
            show = tvMazeClient.getShow(id);
        } catch (WebClientResponseException.NotFound ex) {
            throw new ShowNotFoundException(id);
        }

        if (show == null) {
            throw new ShowNotFoundException(id);
        }

        showRepository.save(ShowMapper.toDocument(show));

        ShowDetailResponse result = ShowMapper.toDetailResponse(show, commentService.findByShowId(id));

        try {
            log.info("TV Maze show [id={}] guardado en cache ->\n{}",
                    id,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.warn("No se pudo serializar el resultado para log: {}", e.getMessage());
        }

        return result;
    }
}
