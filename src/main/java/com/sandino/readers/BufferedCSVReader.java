package com.sandino.readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BufferedCSVReader implements CSVReader {
    private String path;

    public BufferedCSVReader(String path) {
        this.path = path;
    }

    @Override
    public String[] readLines() throws IOException {

        var fileReader = new FileReader(this.path);
        var bufferedReader = new BufferedReader(fileReader);

        List<String> lines = new ArrayList<>();

        String line = bufferedReader.readLine();
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        bufferedReader.close();

        return lines.toArray(new String[0]);
    }
}
