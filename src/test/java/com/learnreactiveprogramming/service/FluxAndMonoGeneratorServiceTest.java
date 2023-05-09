package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();

        StepVerifier.create(namesFlux)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        int stringLength = 3;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxMap(stringLength);

        StepVerifier.create(namesFluxMap)
                .expectNextCount(2)
                .verifyComplete();

        StepVerifier.create(namesFluxMap)
                .expectNext("4 - ALEX", "5 - CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        int stringLength = 3;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength);

        StepVerifier.create(namesFluxMap)
                .expectNextCount(9)
                .verifyComplete();

        StepVerifier.create(namesFluxMap)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

//    @Test
//    void namesFluxFlatMapAsync() {
//        int stringLength = 3;
//        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength);
//
//        StepVerifier.create(namesFluxMap)
//                .expectNextCount(9)
//                .verifyComplete();
//
//        // Can fail due to the async nature of flatMap!
//        StepVerifier.create(namesFluxMap)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
//                .verifyComplete();
//    }

    @Test
    void namesFluxFlatConcatMap() {
        int stringLength = 3;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxFlatConcatMap(stringLength);

        StepVerifier.create(namesFluxMap)
                .expectNextCount(9)
                .verifyComplete();

        // Won't fail, since concatMap preserves order
        StepVerifier.create(namesFluxMap)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatConcatMapVirtualTimer() {
        VirtualTimeScheduler.getOrSet();
        int stringLength = 3;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxFlatConcatMap(stringLength);

        // Won't fail, since concatMap preserves order
        StepVerifier.withVirtualTime(() -> namesFluxMap)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        int stringLength = 3;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        StepVerifier.create(namesFluxMap)
                .expectNextCount(9)
                .verifyComplete();

        StepVerifier.create(namesFluxMap)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformDefault() {
        int stringLength = 6;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        StepVerifier.create(namesFluxMap)
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(namesFluxMap)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        int stringLength = 6;
        var namesFluxMap = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(stringLength);

        StepVerifier.create(namesFluxMap)
                .expectNextCount(7)
                .verifyComplete();

        StepVerifier.create(namesFluxMap)
                .expectNext("D")
                .expectNext("E")
                .expectNext("F")
                .expectNext("A")
                .expectNext("U")
                .expectNext("L")
                .expectNext("T")
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcat();

        StepVerifier.create(concatFlux)
                .expectNextCount(6)
                .verifyComplete();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .expectComplete();
    }

    @Test
    void exploreConcatWith() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWith();

        StepVerifier.create(concatFlux)
                .expectNextCount(2)
                .verifyComplete();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B")
                .expectComplete();
    }

    @Test
    void namesMono() {
        var namesMono = fluxAndMonoGeneratorService.namesMono();

        StepVerifier.create(namesMono)
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(namesMono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        var namesMono = fluxAndMonoGeneratorService.namesMonoFlatMap();

        StepVerifier.create(namesMono)
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(namesMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        var namesMono = fluxAndMonoGeneratorService.namesMonoFlatMapMany();

        StepVerifier.create(namesMono)
                .expectNextCount(4)
                .verifyComplete();

        StepVerifier.create(namesMono)
                .expectNext("A")
                .expectNext("L")
                .expectNext("E")
                .expectNext("X")
                .verifyComplete();
    }


    @Test
    void exceptionFlux() {
        var exceptionFlux = fluxAndMonoGeneratorService.exceptionFlux();

        StepVerifier.create(exceptionFlux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exploreOnErrorReturn() {
        var exceptionFlux = fluxAndMonoGeneratorService.exploreOnErrorReturn();

        StepVerifier.create(exceptionFlux)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorResume() {
        var exceptionFlux1 = fluxAndMonoGeneratorService
                .exploreOnErrorResume(new IllegalStateException("Not a valid state"));

        var exceptionFlux2 = fluxAndMonoGeneratorService
                .exploreOnErrorResume(new ArrayIndexOutOfBoundsException("Array index out of bounds"));

        StepVerifier.create(exceptionFlux1)
                .expectNext("A", "B", "C", "E", "F", "G")
                .verifyComplete();

        StepVerifier.create(exceptionFlux2)
                .expectNext("A", "B", "C")
                .expectError(ArrayIndexOutOfBoundsException.class)
                .verify();
    }
}
