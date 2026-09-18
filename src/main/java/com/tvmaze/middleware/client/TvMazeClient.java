package com.tvmaze.middleware.client;

import com.tvmaze.middleware.dto.tvmaze.TvMazeSearchItem;
import com.tvmaze.middleware.dto.tvmaze.TvMazeShow;
import com.tvmaze.middleware.exception.UpstreamServiceException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TvMazeClient {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public TvMazeClient(WebClient tvmazeWebClient, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.webClient = tvmazeWebClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("tvmaze");
    }

    public List<TvMazeSearchItem> searchShows(String query) {
        try {
            return circuitBreaker.executeSupplier(() -> searchShowsRemote(query));
        } catch (WebClientResponseException.NotFound ex) {
            throw ex;
        } catch (Throwable throwable) {
            log.warn("Fallback search por falla de TVMaze [query='{}']: {}", query, throwable.getMessage());
            throw new UpstreamServiceException("El servicio TVMaze no está disponible");
        }
    }

    public TvMazeShow getShow(Long id) {
        try {
            return circuitBreaker.executeSupplier(() -> getShowRemote(id));
        } catch (WebClientResponseException.NotFound ex) {
            throw ex;
        } catch (Throwable throwable) {
            log.warn("Fallback show por falla de TVMaze [id={}]: {}", id, throwable.getMessage());
            throw new UpstreamServiceException("El servicio TVMaze no está disponible");
        }
    }

    private List<TvMazeSearchItem> searchShowsRemote(String query) {
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

    private TvMazeShow getShowRemote(Long id) {
        return webClient.get()
                .uri("/shows/{id}", id)
                .retrieve()
                .bodyToMono(TvMazeShow.class)
                .block();
    }
}
