package com.sandino.concurrent.spark;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class ConcurrentSparkKnn {
    private JavaSparkContext sc;
    private String filename;

    public ConcurrentSparkKnn(String filename, int numberOfThreads) {
        this.sc = SparkContextFactory.buildContext(numberOfThreads);
        this.filename = filename;
    }

    private JavaRDD<double[]> loadDataset(double[] testRow, int k) {
        return sc.textFile(filename).mapPartitions(iterator -> new LineProcessor(testRow, k).call(iterator), true);
    }

    private List<double[]> getNeighbors(double[] testRow, int k) {
        JavaRDD<double[]> dataset = loadDataset(testRow, k);

        return dataset.takeOrdered(k, new SerializableComparator());
    }

    public double predictClassification(double[] testRow, int k) {
        List<double[]> neighbors = getNeighbors(testRow, k);

        int classCol = neighbors.get(0).length - 2;

        Map<Double, Integer> frequencyMap = new HashMap<>();

        for (double[] row : neighbors) {
            if (frequencyMap.containsKey(row[classCol])) {
                frequencyMap.put(row[classCol], frequencyMap.get(row[classCol]) + 1);
            } else {
                frequencyMap.put(row[classCol], 1);
            }
        }

        sc.close();

        return Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.OFF);

        ConcurrentSparkKnn knn = new ConcurrentSparkKnn("data.csv", 6);

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
