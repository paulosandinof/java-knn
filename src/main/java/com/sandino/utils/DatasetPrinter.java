package com.sandino.utils;

public class DatasetPrinter {

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
}
