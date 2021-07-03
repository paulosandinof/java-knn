package com.sandino;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    enum CodRenda {
        A(1), B(2), C(3), D(4), E(5), F(6), G(7), H(8), I(9), J(10), K(11), L(12), M(13), N(14), O(15), P(16), Q(17);

        double index;

        CodRenda(int i) {
            index = i;
        }

        public double getIndex() {
            return index;
        }
    }

    public static void printDataset(double[][] dataset) {
        for (int i = 0; i < dataset.length; i++) {
            System.out.print("[ ");
            for (double cell : dataset[i]) {
                System.out.print(cell + ", ");
            }
            System.out.print("]");
            System.out.println();
        }
    }

    public static void sortByColumn(double array[][], int col) {
        // Using built-in sort function Arrays.sort
        Arrays.sort(array, new Comparator<double[]>() {

            @Override
            // Compare values according to columns
            public int compare(final double[] entry1, final double[] entry2) {

                // To sort in descending order revert
                // the '>' Operator
                if (entry1[col] > entry2[col])
                    return 1;
                else
                    return -1;
            }
        }); // End of function call sort().
    }

    public static double stringToDouble(String string) {
        return string.isEmpty() ? 0.0 : Double.parseDouble(string);
    }

    public static double classToDouble(String string) {
        return CodRenda.valueOf(string).getIndex();
    }

    public static double[][] loadDataset(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        List<double[]> list = new ArrayList<>();

        String line = bufferedReader.readLine();
        while ((line = bufferedReader.readLine()) != null) {
            String[] data = line.split(";");

            double[] row = new double[6];

            row[0] = stringToDouble(data[91]);
            row[1] = stringToDouble(data[92]);
            row[2] = stringToDouble(data[93]);
            row[3] = stringToDouble(data[94]);
            row[4] = stringToDouble(data[110]);
            row[5] = classToDouble(data[116]);

            list.add(row);
        }

        bufferedReader.close();

        return list.toArray(new double[list.size()][]);
    }

    public static double euclideanDistance(double[] row1, double[] row2) {
        double distance = 0.0;

        for (int i = 0; i < row1.length - 1; i++) {
            distance += Math.pow(row1[i] - row2[i], 2);
        }

        return Math.sqrt(distance);
    }

    public static double[][] getNeighbors(double[][] train, double[] testRow, int k) {
        int rows = train.length;
        int cols = train[0].length;

        double[][] distances = new double[rows][cols + 1];

        for (int i = 0; i < train.length; i++) {
            double distance = euclideanDistance(testRow, train[i]);

            distances[i] = Arrays.copyOf(train[i], cols + 1);

            distances[i][cols] = distance;
        }

        sortByColumn(distances, cols);

        double[][] neighbors = new double[k][cols];

        for (int i = 0; i < k; i++) {
            neighbors[i] = Arrays.copyOf(distances[i], cols);
        }

        return neighbors;
    }

    public static double predictClassification(double[][] train, double[] testRow, int k) {
        int col = train[0].length - 1;

        double[][] neighbors = getNeighbors(train, testRow, k);

        printDataset(neighbors);

        Map<Double, Integer> frequencyMap = new HashMap<>();

        for (double[] row : neighbors) {
            if (frequencyMap.containsKey(row[col])) {
                frequencyMap.put(row[col], frequencyMap.get(row[col]) + 1);
            } else {
                frequencyMap.put(row[col], 1);
            }
        }

        // Map<Double, Long> frequencyMap = list.stream()
        // .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        double key = Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();

        return key;
    }

    public static void main(String[] args) throws IOException {
        
        String filename = "/home/sandino/Documents/dataset.csv";
        
        double[][] dataset = loadDataset(filename);
        
        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        // int k = (int) Math.sqrt(dataset.length);
        int k = 10;

        double prediction = predictClassification(dataset, initialRow, k);

        System.out.println(prediction);
    }
}
