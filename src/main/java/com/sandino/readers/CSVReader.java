package com.sandino.readers;

import java.io.IOException;

public interface CSVReader {
    public String[] readLines() throws IOException;
}
