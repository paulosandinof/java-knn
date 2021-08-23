package com.sandino.concurrent.reactor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.sandino.utils.DatasetUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

public class ConcurrentReactorKnn {

    private String filename;
    private int numberOfThreads;

    public ConcurrentReactorKnn(String filename, int numberOfThreads) {
        this.filename = filename;
        this.numberOfThreads = numberOfThreads;
    }

    private static Function<String, double[]> lineProcessor(double[] testRow) {
        return line -> {
            String[] data = line.split(",");

            double[] row = new double[7];

            row[0] = DatasetUtils.stringToDouble(data[91]);
            row[1] = DatasetUtils.stringToDouble(data[92]);
            row[2] = DatasetUtils.stringToDouble(data[93]);
            row[3] = DatasetUtils.stringToDouble(data[94]);
            row[4] = DatasetUtils.stringToDouble(data[101]);
            row[5] = DatasetUtils.classToDouble(data[107]);
            row[6] = DatasetUtils.calculate(testRow, row);

            return row;
        };
    }

    private ParallelFlux<double[]> loadDataset(double[] testRow) throws IOException {
        Path path = Paths.get(filename);

        Stream<String> lines = Files.lines(path);

        return Flux.fromStream(lines).parallel(numberOfThreads).runOn(Schedulers.parallel()).map(lineProcessor(testRow));
    }

    private Flux<double[]> getNeighbors(double[] testRow, int k) throws IOException {
        ParallelFlux<double[]> datasetFlux = loadDataset(testRow);

        return datasetFlux.sorted((entry1, entry2) -> Double.compare(entry1[6], entry2[6])).take(k);
    }

    public double predictClassification(double[] testRow, int k) throws IOException {
        Iterable<double[]> neighbors = getNeighbors(testRow, k).toIterable();

        int classCol = 5;

        Map<Double, Integer> frequencyMap = new HashMap<>();

        for (double[] row : neighbors) {
            if (frequencyMap.containsKey(row[classCol])) {
                frequencyMap.put(row[classCol], frequencyMap.get(row[classCol]) + 1);
            } else {
                frequencyMap.put(row[classCol], 1);
            }
        }

        return Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public static void main(String[] args) throws IOException {
        ConcurrentReactorKnn knn = new ConcurrentReactorKnn("data.csv", 1);

        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        int k = 10;

        Instant start = Instant.now();

        double prediction = knn.predictClassification(initialRow, k);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        System.out.println("Time Elapsed: " + timeElapsed);

        System.out.println("Prediction: " + prediction);
    }
}
