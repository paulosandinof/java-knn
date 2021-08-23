package com.sandino;

import java.util.concurrent.TimeUnit;

import com.sandino.concurrent.parallelstream.ConcurrentParallelStreamKnn;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ConcurrentParallelStreamKnnJMHTest {

    private String filename = "data.csv";

    private double[] testRow = { 600, 600, 600, 600, 600 };

    @Param({ "1", "2", "4", "6", "8", "12", "16", "24", "32", "64" })
    private int numberOfThreads;

    @Param({ "1000" })
    private int k;

    @Benchmark
    public double test() {
        ConcurrentParallelStreamKnn concurrentKnn = new ConcurrentParallelStreamKnn(filename, numberOfThreads);
        return concurrentKnn.predictClassification(testRow, k);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(ConcurrentParallelStreamKnnJMHTest.class.getSimpleName()).build();

        new Runner(opt).run();
    }
}
