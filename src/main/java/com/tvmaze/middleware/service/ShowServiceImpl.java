package com.tvmaze.middleware.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvmaze.middleware.client.TvMazeClient;
import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.dto.tvmaze.TvMazeSearchItem;
import com.tvmaze.middleware.dto.tvmaze.TvMazeShow;
import com.tvmaze.middleware.exception.ShowNotFoundException;
import com.tvmaze.middleware.mapper.ShowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final TvMazeClient tvMazeClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<ShowResponse> search(String query) {
        List<ShowResponse> results = tvMazeClient.searchShows(query).stream()
                .filter(item -> item.show() != null)
                .map(TvMazeSearchItem::show)
                .map(ShowMapper::toResponse)
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
        TvMazeShow show;
        try {
            show = tvMazeClient.getShow(id);
        } catch (WebClientResponseException.NotFound ex) {
            throw new ShowNotFoundException(id);
        }

        if (show == null) {
            throw new ShowNotFoundException(id);
        }

        ShowDetailResponse result = ShowMapper.toDetailResponse(show);

        try {
            log.info("TV Maze show [id={}] ->\n{}",
                    id,
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.warn("No se pudo serializar el resultado para log: {}", e.getMessage());
        }

        return result;
    }
}
