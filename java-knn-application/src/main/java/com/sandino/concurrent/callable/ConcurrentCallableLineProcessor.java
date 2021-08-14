package com.sandino.concurrent.callable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import com.sandino.utils.DatasetUtils;

public class ConcurrentCallableLineProcessor implements Callable<List<double[]>> {
    private List<String> lines;
    private double[] testRow;
    private int k;

    public ConcurrentCallableLineProcessor(List<String> lines, double[] testRow, int k) {
        this.lines = new ArrayList<>(lines);
        this.testRow = testRow;
        this.k = k;
    }

    @Override
    public List<double[]> call() {

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

        return Arrays.asList(Arrays.copyOf(convertedPartialDataset, k));
    }
}
