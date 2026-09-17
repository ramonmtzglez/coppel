package com.tvmaze.middleware.client;

import com.tvmaze.middleware.dto.tvmaze.TvMazeSearchItem;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Component
public class TvMazeClient {

    private final WebClient webClient;

    public TvMazeClient(WebClient tvmazeWebClient) {
        this.webClient = tvmazeWebClient;
    }

    public List<TvMazeSearchItem> searchShows(String query) {
        return Optional.ofNullable(
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/search/shows")
                                .queryParam("q", query)
                                .build())
                        .retrieve()
                        .bodyToFlux(TvMazeSearchItem.class)
                        .collectList()
                        .block()
        ).orElse(List.of());
    }
}
