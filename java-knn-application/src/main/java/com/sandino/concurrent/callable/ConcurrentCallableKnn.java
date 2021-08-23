package com.sandino.concurrent.callable;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.sandino.utils.DatasetUtils;

public class ConcurrentCallableKnn {

    private String filename;
    private int numberOfThreads;
    private int chunkSize;

    public ConcurrentCallableKnn(String filename, int numberOfThreads, int chunkSize) {
        this.filename = filename;
        this.numberOfThreads = numberOfThreads;
        this.chunkSize = chunkSize;
    }

    private double[][] loadDataset(double[] testRow, int k) {
        List<String> lines = new ArrayList<>();

        List<double[]> dataset = new ArrayList<>();

        List<Future<List<double[]>>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            int lineCounter = 0;

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                lineCounter++;

                lines.add(line);

                if (lineCounter % chunkSize == 0) {
                    ConcurrentCallableLineProcessor processor = new ConcurrentCallableLineProcessor(lines, testRow, k);

                    Future<List<double[]>> task = executorService.submit(processor);

                    futures.add(task);

                    lines.clear();
                }
            }

            if (!lines.isEmpty()) {
                ConcurrentCallableLineProcessor processor = new ConcurrentCallableLineProcessor(lines, testRow, k);

                Future<List<double[]>> task = executorService.submit(processor);

                futures.add(task);
            }

            for (Future<List<double[]>> future : futures) {
                dataset.addAll(future.get());
            }

            executorService.shutdown();

            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("executorService did not terminate");
                }
            }
        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
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
        ConcurrentCallableKnn knn = new ConcurrentCallableKnn("datatrain.csv", 1, 1);

        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        int k = 19;

        Instant start = Instant.now();

        double prediction = knn.predictClassification(initialRow, k);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        System.out.println("Time Elapsed: " + timeElapsed);

        System.out.println("Prediction: " + prediction);
    }
}
