package com.sandino.concurrent.forkjoin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.sandino.utils.DatasetUtils;

public class ConcurrentForkJoinKnn {

    private String filename;
    private int numberOfThreads;
    private int chunkSize;

    public ConcurrentForkJoinKnn(String filename, int numberOfThreads, int chunkSize) {
        this.filename = filename;
        this.numberOfThreads = numberOfThreads;
        this.chunkSize = chunkSize;
    }

    private double[][] loadDataset(double[] testRow, int k) {
        List<String> lines = new ArrayList<>();

        List<double[]> dataset = new ArrayList<>();

        List<Future<List<double[]>>> futures = new ArrayList<>();

        ForkJoinPool forkJoinPool = new ForkJoinPool(numberOfThreads);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {

            int lineCounter = 0;

            String line;

            while ((line = bufferedReader.readLine()) != null) {

                lineCounter++;

                lines.add(line);

                if (lineCounter % chunkSize == 0) {
                    ConcurrentForkJoinLineProcessor processor = new ConcurrentForkJoinLineProcessor(lines, testRow, k);

                    Future<List<double[]>> task = forkJoinPool.submit(processor);

                    futures.add(task);

                    lines.clear();
                }
            }

            if (!lines.isEmpty()) {
                ConcurrentForkJoinLineProcessor processor = new ConcurrentForkJoinLineProcessor(lines, testRow, k);

                Future<List<double[]>> task = forkJoinPool.submit(processor);

                futures.add(task);
            }

            for (Future<List<double[]>> future : futures) {
                dataset.addAll(future.get());
            }

            forkJoinPool.shutdown();

            if (!forkJoinPool.awaitTermination(60, TimeUnit.SECONDS)) {
                forkJoinPool.shutdownNow();
                if (!forkJoinPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("executorService did not terminate");
                }
            }

        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            forkJoinPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return dataset.toArray(new double[0][]);
    }

    private double[][] getNeighbors(double[] testRow, int k) {
        double[][] dataset = loadDataset(testRow, k);

        int distanceCol = dataset[0].length - 1;

        DatasetUtils.sortByColumn(dataset, distanceCol);

        return Arrays.copyOf(dataset, k);
    }

    public double predictClassification(double[] testRow, int k) {
        double[][] neighbors = getNeighbors(testRow, k);

        int classCol = neighbors[0].length - 2;

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

    public static void main(String[] args) {
        ConcurrentForkJoinKnn knn = new ConcurrentForkJoinKnn("data.csv", 4, 10000);

        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        int k = 1000;

        Instant start = Instant.now();

        double prediction = knn.predictClassification(initialRow, k);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        System.out.println("Time Elapsed: " + timeElapsed);

        System.out.println("Prediction: " + prediction);
    }
}
