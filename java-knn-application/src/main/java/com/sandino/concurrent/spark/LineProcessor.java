package com.sandino.concurrent.spark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sandino.utils.DatasetUtils;

import org.apache.spark.api.java.function.FlatMapFunction;

public class LineProcessor implements FlatMapFunction<Iterator<String>, double[]> {
    private double[] testRow;
    private int k;

    public LineProcessor(double[] testRow, int k) {
        this.testRow = testRow;
        this.k = k;
    }

    @Override
    public Iterator<double[]> call(Iterator<String> iterator) throws Exception {
        List<double[]> datasetPartition = new ArrayList<>();

        while (iterator.hasNext()) {
            String[] data = iterator.next().split(",");

            double[] row = new double[7];

            row[0] = DatasetUtils.stringToDouble(data[91]);
            row[1] = DatasetUtils.stringToDouble(data[92]);
            row[2] = DatasetUtils.stringToDouble(data[93]);
            row[3] = DatasetUtils.stringToDouble(data[94]);
            row[4] = DatasetUtils.stringToDouble(data[101]);
            row[5] = DatasetUtils.classToDouble(data[107]);
            row[6] = DatasetUtils.calculate(testRow, row);

            datasetPartition.add(row);
        }

        datasetPartition.sort((entry1, entry2) -> Double.compare(entry1[6], entry2[6]));

        return datasetPartition.subList(0, k).iterator();
    }
}
