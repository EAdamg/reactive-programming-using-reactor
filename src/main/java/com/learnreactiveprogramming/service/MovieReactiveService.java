package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {

    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;
    private final RevenueService revenueService;


    public MovieReactiveService(
            MovieInfoService movieInfoService,
            ReviewService reviewService,
            RevenueService revenueService
    ) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {
        var moviesInfoFlux = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    throw new MovieException(ex.getMessage());
                });
    }

    public Flux<Movie> getAllMoviesWithRetries() {
        var moviesInfoFlux = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    throw new MovieException(ex.getMessage());
                }).retry(3);
    }

    public Flux<Movie> getAllMoviesWithRetryWhen() {
        var moviesInfoFlux = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    }
                    throw new ServiceException(ex.getMessage());
                }).retryWhen(getRetryBackoffSpec());
    }

    public Flux<Movie> getAllMoviesWithRepeat() {
        var moviesInfoFlux = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    }
                    throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat();
    }

    public Flux<Movie> getAllMoviesWithRepeatN(long n) {
        var moviesInfoFlux = this.movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    }
                    throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat(n);
    }

    private static RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                    Exceptions.propagate(retrySignal.failure())));
    }

    public Mono<Movie> getMovieById(long movieId) {
        var movieInfoMono = this.movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = this.reviewService.retrieveReviewsFlux(movieId).collectList();

        return movieInfoMono.zipWith(reviewsFlux, Movie::new);
    }

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {
        var movieInfoMono = this.movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = this.reviewService.retrieveReviewsFlux(movieId).collectList();

        var revenueMono = Mono.fromCallable(() -> this.revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono.zipWith(reviewsFlux, Movie::new)
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                });
    }

}
