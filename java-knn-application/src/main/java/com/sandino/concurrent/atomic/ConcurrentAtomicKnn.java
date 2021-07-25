package com.sandino.concurrent.atomic;

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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sandino.utils.DatasetUtils;

public class ConcurrentAtomicKnn {

    private String filename;
    private int numberOfThreads;
    private int chunkSize;

    private AtomicBoolean flag;

    public ConcurrentAtomicKnn(String filename, int numberOfThreads, int chunkSize) {
        this.filename = filename;
        this.numberOfThreads = numberOfThreads;
        this.chunkSize = chunkSize;
        this.flag = new AtomicBoolean(false);
    }

    private double[][] loadDataset(double[] testRow, int k) {
        List<String> lines = new ArrayList<>();

        Queue<double[]> dataset = new ConcurrentLinkedQueue<>();

        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {

            int lineCounter = 0;

            String line;

            while ((line = bufferedReader.readLine()) != null) {

                lineCounter++;

                lines.add(line);

                if (lineCounter % chunkSize == 0) {
                    ConcurrentAtomicLineProcessor processor = new ConcurrentAtomicLineProcessor(lines, testRow, k,
                            dataset, flag);

                    executorService.submit(processor);

                    lines.clear();
                }
            }

            ConcurrentAtomicLineProcessor processor = new ConcurrentAtomicLineProcessor(lines, testRow, k, dataset,
                    flag);

            executorService.submit(processor);

            executorService.shutdown();

            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("executorService did not terminate");
                }
            }

        } catch (IOException e) {
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
        ConcurrentAtomicKnn knn = new ConcurrentAtomicKnn("data.csv", 5, 10000);

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
