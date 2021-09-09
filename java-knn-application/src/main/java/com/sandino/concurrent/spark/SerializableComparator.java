package com.sandino.concurrent.spark;

import java.io.Serializable;
import java.util.Comparator;

public class SerializableComparator implements Comparator<double[]>, Serializable {
    @Override
    public int compare(double[] o1, double[] o2) {
        return Double.compare(o1[6], o2[6]);
    }
}
