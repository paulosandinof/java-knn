package com.sandino;

import java.util.Arrays;
import java.util.List;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.L_Result;

@JCStressTest
@Outcome(id = { "2" }, expect = Expect.ACCEPTABLE, desc = "Correct")
@State
public class ConcurrentParallelStreamKnnJCStressTest {

    private double[][] lines = { { 600, 600, 600, 600, 3.0, 112.50 }, { 700, 700, 700, 700, 4.0, 212.50 } };

    private List<double[]> filteredDataset = Arrays.asList(lines);

    @Actor
    public void actor1() {
        //
    }

    @Arbiter
    public void arbiter(L_Result r) {
        r.r1 = filteredDataset.stream().parallel().count();
    }
}
