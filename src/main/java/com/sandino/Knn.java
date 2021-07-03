package com.sandino;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sandino.utils.DatasetColumnsConverters;
import com.sandino.utils.DatasetSorter;

public class Knn {

    private double[][] dataset;

    public Knn(String filename) throws IOException {
        this.dataset = this.loadDataset(filename);
    }

    private double[][] loadDataset(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<double[]> list = new ArrayList<>();

        String line = bufferedReader.readLine();
        while ((line = bufferedReader.readLine()) != null) {
            String[] data = line.split(";");

            double[] row = new double[6];

            row[0] = DatasetColumnsConverters.stringToDouble(data[91]);
            
            row[1] = DatasetColumnsConverters.stringToDouble(data[92]);
            row[2] = DatasetColumnsConverters.stringToDouble(data[93]);
            row[3] = DatasetColumnsConverters.stringToDouble(data[94]);
            row[4] = DatasetColumnsConverters.stringToDouble(data[110]);
            row[5] = DatasetColumnsConverters.classToDouble(data[116]);

            list.add(row);
        }

        bufferedReader.close();

        return list.toArray(new double[list.size()][]);
    }

    private double euclideanDistance(double[] row1, double[] row2) {
        double distance = 0.0;

        for (int i = 0; i < row1.length - 1; i++) {
            distance += Math.pow(row1[i] - row2[i], 2);
        }

        return Math.sqrt(distance);
    }

    private double[][] getNeighbors(double[] testRow, int k) {
        int rows = dataset.length;
        int cols = dataset[0].length;

        double[][] distances = new double[rows][cols + 1];

        for (int i = 0; i < dataset.length; i++) {
            double distance = euclideanDistance(testRow, dataset[i]);

            distances[i] = Arrays.copyOf(dataset[i], cols + 1);

            distances[i][cols] = distance;
        }

        DatasetSorter.sortByColumn(distances, cols);

        double[][] neighbors = new double[k][cols];

        for (int i = 0; i < k; i++) {
            neighbors[i] = Arrays.copyOf(distances[i], cols);
        }

        return neighbors;
    }

    public double predictClassification(double[] testRow, int k) {
        int col = dataset[0].length - 1;

        double[][] neighbors = getNeighbors(testRow, k);

        Map<Double, Integer> frequencyMap = new HashMap<>();

        for (double[] row : neighbors) {
            if (frequencyMap.containsKey(row[col])) {
                frequencyMap.put(row[col], frequencyMap.get(row[col]) + 1);
            } else {
                frequencyMap.put(row[col], 1);
            }
        }

        double key = Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();

        return key;
    }
}
