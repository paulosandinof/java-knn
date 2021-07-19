package com.sandino.utils;

import java.util.Arrays;
import java.util.Comparator;

public class DatasetUtils {
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

    public static double stringToDouble(String string) {
        return string.isEmpty() ? 0.0 : Double.parseDouble(string);
    }

    public static double classToDouble(String string) {
        return CodRenda.valueOf(string).getIndex();
    }

    public static double calculate(double[] row1, double[] row2) {
        double distance = 0.0;

        for (int i = 0; i < row1.length - 2; i++) {
            distance += Math.pow(row1[i] - row2[i], 2);
        }

        return Math.sqrt(distance);
    }

    public static void print(double[][] dataset) {

        for (int i = 0; i < dataset.length; i++) {

            System.out.print("[ ");

            for (double cell : dataset[i]) {
                System.out.print(cell + ", ");
            }

            System.out.print("]");
            System.out.println();
        }
    }

    public static double[][] sortByColumn(double[][] array, int col) {
        Comparator<double[]> comparator = (entry1, entry2) -> Double.compare(entry1[col], entry2[col]);

        Arrays.sort(array, comparator);
        return array;
    }
}
