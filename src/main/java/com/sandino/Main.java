package com.sandino;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Knn knn = new Knn("/home/sandino/Documents/dataset.csv");

        double[] initialRow = { 600, 600, 600, 600, 600, 600 };

        int k = 10;

        double prediction = knn.predictClassification(initialRow, k);
        
        System.out.println("Prediction: " + prediction);
    }
}
