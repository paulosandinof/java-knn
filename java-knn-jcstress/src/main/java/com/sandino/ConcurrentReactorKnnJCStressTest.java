package com.sandino;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.L_Result;

import reactor.core.publisher.Flux;

@JCStressTest
@Outcome(id = { "2" }, expect = Expect.ACCEPTABLE, desc = "Correct")
@State
public class ConcurrentReactorKnnJCStressTest {

    private double[][] lines = { { 600, 600, 600, 600, 3.0, 112.50 }, { 700, 700, 700, 700, 4.0, 212.50 } };

    private Flux<double[]> filteredDataset = Flux.fromArray(lines);

    @Actor
    public void actor1() {
        //
    }

    @Arbiter
    public void arbiter(L_Result r) {
        r.r1 = filteredDataset.parallel().sequential().count().block();
    }
}
