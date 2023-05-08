package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explorePublishOn() {
        var flux = this.fluxAndMonoSchedulersService.explorePublishOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallel() {
        var flux = this.fluxAndMonoSchedulersService.exploreParallel();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMap() {
        var flux = this.fluxAndMonoSchedulersService.exploreParallelUsingFlatMap();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMapSequential() {
        var flux = this.fluxAndMonoSchedulersService.exploreParallelUsingFlatMapSequential();

        StepVerifier.create(flux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}