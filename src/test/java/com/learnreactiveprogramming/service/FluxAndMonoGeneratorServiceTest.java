package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFLux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
    }
}
