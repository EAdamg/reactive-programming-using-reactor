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
}