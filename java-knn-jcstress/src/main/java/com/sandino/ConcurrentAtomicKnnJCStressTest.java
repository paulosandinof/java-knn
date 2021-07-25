package com.sandino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = { "4" }, expect = Expect.ACCEPTABLE, desc = "Correct")
@State
public class ConcurrentAtomicKnnJCStressTest {

    private double[][] lines = { { 600, 600, 600, 600, 3.0, 112.50 }, { 700, 700, 700, 700, 4.0, 212.50 } };

    private List<double[]> filteredDataset = Arrays.asList(lines);

    private List<double[]> dataset = new ArrayList<>();

    private AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    @Actor
    public void actor1() {

        boolean hasFinished = false;

        while (!hasFinished) {
            if (atomicBoolean.compareAndSet(false, true)) {
                dataset.addAll(filteredDataset);
                atomicBoolean.set(false);

                hasFinished = true;
            }
        }
    }

    @Actor
    public void actor2() {
        boolean hasFinished = false;

        while (!hasFinished) {
            if (atomicBoolean.compareAndSet(false, true)) {
                dataset.addAll(filteredDataset);
                atomicBoolean.set(false);

                hasFinished = true;
            }
        }
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = dataset.size();
    }
}
