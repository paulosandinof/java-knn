package com.sandino.concurrent.parallelstream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sandino.utils.DatasetUtils;

public class ConcurrentParallelStreamKnn {

    private String filename;

    public ConcurrentParallelStreamKnn(String filename) {
        this.filename = filename;
    }

    private double[][] loadDataset(double[] testRow) {
        List<double[]> dataset = new ArrayList<>();

        Path path = Paths.get(filename);

        try (Stream<String> lines = Files.lines(path)) {
            dataset = lines.parallel().map(line -> {
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
            }).sorted((entry1, entry2) -> Double.compare(entry1[6], entry2[6])).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(dataset.size());

        return dataset.toArray(new double[0][]);
    }

    private double[][] getNeighbors(double[] testRow, int k) {
        double[][] dataset = loadDataset(testRow);

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
        ConcurrentParallelStreamKnn knn = new ConcurrentParallelStreamKnn("data.csv");

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
