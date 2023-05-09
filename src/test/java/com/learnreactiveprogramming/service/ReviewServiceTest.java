package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewServiceTest {

    private final ReviewService reviewService = new ReviewService();
    @Test
    void retrieveReviewsFluxRestClient() {
        var reviewsFlux = this.reviewService.retrieveReviewsFluxRestClient(1L);

        StepVerifier.create(reviewsFlux)
                .consumeNextWith(review -> assertEquals(review.getComment(), "Nolan is the real superhero"))
                .verifyComplete();
    }
}