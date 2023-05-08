package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {

    private final MovieInfoService movieInfoService = new MovieInfoService();
    private final ReviewService reviewService = new ReviewService();
    private final RevenueService revenueService = new RevenueService();
    private final MovieReactiveService movieReactiveService =
            new MovieReactiveService(movieInfoService, reviewService, revenueService);

    @Test
    void getAllMovies() {
        var moviesFLux = movieReactiveService.getAllMovies();

        StepVerifier.create(moviesFLux)
                .assertNext(movie -> {
                   assertEquals(movie.getMovieInfo().getName(), "Batman Begins");
                   assertEquals(movie.getReviewList().size(), 2);
                })
                .assertNext(movie -> {
                    assertEquals(movie.getMovieInfo().getName(), "The Dark Knight");
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .assertNext(movie -> {
                    assertEquals(movie.getMovieInfo().getName(), "Dark Knight Rises");
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .expectComplete();
    }

    @Test
    void getMovieById() {
        long movieId = 100L;

        var movieMono = this.movieReactiveService.getMovieById(movieId);

        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals(movie.getMovieInfo().getName(), "Batman Begins");
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .expectComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        long movieId = 100L;

        var movieMono = this.movieReactiveService.getMovieById(movieId);

        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals(movie.getMovieInfo().getName(), "Batman Begins");
                    assertEquals(movie.getReviewList().size(), 2);
                    assertNotNull(movie.getRevenue());
                })
                .expectComplete();
    }
}