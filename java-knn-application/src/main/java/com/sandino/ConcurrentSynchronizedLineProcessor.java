package com.sandino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sandino.utils.DatasetUtils;

public class ConcurrentSynchronizedLineProcessor implements Runnable {

    private List<String> lines;
    private double[] testRow;
    private int k;
    private List<double[]> dataset;

    public ConcurrentSynchronizedLineProcessor(List<String> lines, double[] testRow, int k, List<double[]> dataset) {
        this.lines = new ArrayList<>(lines);
        this.testRow = testRow;
        this.k = k;
        this.dataset = dataset;
    }

    @Override
    public void run() {

        List<double[]> partialDataset = new ArrayList<>();

        for (String line : lines) {
            String[] data = line.split(",");

            double[] row = new double[7];

            row[0] = DatasetUtils.stringToDouble(data[91]);
            row[1] = DatasetUtils.stringToDouble(data[92]);
            row[2] = DatasetUtils.stringToDouble(data[93]);
            row[3] = DatasetUtils.stringToDouble(data[94]);
            row[4] = DatasetUtils.stringToDouble(data[101]);
            row[5] = DatasetUtils.classToDouble(data[107]);
            row[6] = DatasetUtils.calculate(testRow, row);

            partialDataset.add(row);
        }

        double[][] convertedPartialDataset = partialDataset.toArray(new double[0][]);

        DatasetUtils.sortByColumn(convertedPartialDataset, convertedPartialDataset[0].length - 1);

        if (k > convertedPartialDataset.length) {
            k = convertedPartialDataset.length;
        }

        List<double[]> filteredDataset = Arrays.asList(Arrays.copyOf(convertedPartialDataset, k));

        synchronized (dataset) {
            dataset.addAll(filteredDataset);
        }
    }
}
