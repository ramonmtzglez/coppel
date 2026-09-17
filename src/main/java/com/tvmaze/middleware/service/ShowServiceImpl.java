package com.tvmaze.middleware.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvmaze.middleware.client.TvMazeClient;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.dto.tvmaze.TvMazeSearchItem;
import com.tvmaze.middleware.mapper.ShowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                .map(TvMazeSearchItem::show)  //Extrae Show del wrapper
                .map(ShowMapper::toResponse)// Convierte a DTO
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
}
