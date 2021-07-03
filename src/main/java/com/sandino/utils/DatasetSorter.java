package com.sandino.utils;

import java.util.Arrays;
import java.util.Comparator;

public class DatasetSorter {
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
}
