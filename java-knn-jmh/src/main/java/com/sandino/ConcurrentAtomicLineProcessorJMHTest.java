package com.sandino;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sandino.concurrent.atomic.ConcurrentAtomicLineProcessor;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ConcurrentAtomicLineProcessorJMHTest {

    private Queue<double[]> dataset = new ConcurrentLinkedQueue<>();

    private List<String> lines = new ArrayList<>();

    private int k = 1000;

    private double[] testRow = { 600, 600, 600, 600, 600 };

    private String line = "190001595656,2019,3552205,Sorocaba,35,SP,36,M,1,3,1,2914802,Itabuna,29,BA,1,11,1,,0,,,,,,,,,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3552205,Sorocaba,35,SP,0,0,0,0,,,,,,,,,,,,,,,,B,B,A,A,2,A,A,B,C,A,A,B,A,A,A,A,A,A,A,A,A,C,A,C,B";

    private AtomicBoolean flag = new AtomicBoolean(false);

    @Setup
    public void setup() {
        for (int i = 0; i < 10000; i++) {
            lines.add(line);
        }
    }

    @Benchmark
    public int test(Blackhole bh) {
        ConcurrentAtomicLineProcessor concurrentAtomicLineProcessor = new ConcurrentAtomicLineProcessor(lines, testRow,
                k, dataset, flag);

        concurrentAtomicLineProcessor.run();

        return dataset.size();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(ConcurrentAtomicLineProcessorJMHTest.class.getSimpleName()).build();

        new Runner(opt).run();
    }
}
