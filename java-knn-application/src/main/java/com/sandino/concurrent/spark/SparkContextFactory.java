package com.sandino.concurrent.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkContextFactory {

    private SparkContextFactory() {
    }

    public static JavaSparkContext buildContext(int numberOfThreads) {
        SparkConf conf = new SparkConf().setAppName("App").setMaster("local[" + numberOfThreads + "]");

        return JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(conf));
    }
}
