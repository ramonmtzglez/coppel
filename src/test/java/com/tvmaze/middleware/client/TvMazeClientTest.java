package com.tvmaze.middleware.client;

import com.tvmaze.middleware.exception.UpstreamServiceException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TvMazeClientTest {

    @Test
    void getShow_throwsUpstreamServiceException_whenTvMazeFails() {
        TvMazeClient client = clientWithFailingExchange();

        assertThatThrownBy(() -> client.getShow(1L))
                .isInstanceOf(UpstreamServiceException.class);
    }

    @Test
    void searchShows_throwsUpstreamServiceException_whenTvMazeFails() {
        TvMazeClient client = clientWithFailingExchange();

        assertThatThrownBy(() -> client.searchShows("girls"))
                .isInstanceOf(UpstreamServiceException.class);
    }

    private TvMazeClient clientWithFailingExchange() {
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.error(new WebClientRequestException(
                        new ConnectException("Connection refused"),
                        HttpMethod.GET,
                        URI.create("http://localhost"),
                        new HttpHeaders())));

        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.tvmaze.com")
                .exchangeFunction(exchangeFunction)
                .build();

        return new TvMazeClient(webClient, CircuitBreakerRegistry.ofDefaults());
    }
}
