package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    private final MovieInfoService movieInfoService = new MovieInfoService();

    @Test
    void retrieveAllMovieInfoRestClient() {
        var movieInfoFlux = this.movieInfoService.retrieveAllMovieInfoRestClient();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void retrieveMovieInfoByIdRestClient() {
        var movieInfoFlux = this.movieInfoService.retrieveMovieInfoByIdRestClient(1L);

        StepVerifier.create(movieInfoFlux)
                .consumeNextWith(movie -> assertEquals(movie.getName(), "Batman Begins"))
                .verifyComplete();
    }
}