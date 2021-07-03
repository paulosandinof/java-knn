package com.sandino.utils;

public class DatasetColumnsConverters {
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
}
