package com.sandino.readers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReadAllLinesCSVReader implements CSVReader {

    @Override
    public String[] readLines() throws IOException {
        Path path = Paths.get("src/main/resources/dataset.csv");

        List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);

        lines.remove(0);

        return lines.toArray(new String[0]);
    }
}
