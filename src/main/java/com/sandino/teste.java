package com.sandino;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class teste {
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


    public static void main(String[] args) throws IOException, InterruptedException {
        long begin = System.currentTimeMillis();

        Path path = Paths.get("src/main/resources/dataset.csv");

        List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);

        lines.remove(0);

        int size = lines.size();

        var linesArray = lines.toArray(new String[0]);

        System.out.println(size);

        Processor processor = new Processor(linesArray, 1, 1273817); // Deve ser inclusivo
        var processor2 = new Processor(linesArray, 1273817, 2547635);
        var processor3 = new Processor(linesArray, 2547635, 3821452);
        var processor4 = new Processor(linesArray, 3821452, size);

        processor.start();
        processor2.start();
        processor3.start();
        processor4.start();


        processor.join();
        processor2.join();
        processor3.join();
        processor4.join();

        lines = null;

        System.out.println((System.currentTimeMillis() - begin));
    }
}
