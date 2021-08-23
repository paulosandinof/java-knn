package com.sandino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class ConcurrentForkJoinKnnJCStressTest {
    private double[][] lines = { { 600, 600, 600, 600, 3.0, 112.50 }, { 700, 700, 700, 700, 4.0, 212.50 } };

    private List<double[]> filteredDataset = Arrays.asList(lines);

    private List<double[]> dataset = new ArrayList<>();

    private List<double[]> datasetActor1 = new ArrayList<>();
    private List<double[]> datasetActor2 = new ArrayList<>();

    @Actor
    public void actor1() {
        datasetActor1 = filteredDataset;
    }

    @Actor
    public void actor2() {
        datasetActor2 = filteredDataset;
    }

    @Arbiter
    public void arbiter(I_Result r) {
        dataset.addAll(datasetActor1);
        dataset.addAll(datasetActor2);

        r.r1 = dataset.size();
    }
}
