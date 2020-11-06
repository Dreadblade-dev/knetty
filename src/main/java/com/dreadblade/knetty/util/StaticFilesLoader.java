package com.dreadblade.knetty.util;

import com.dreadblade.knetty.exception.StaticFileLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StaticFilesLoader {
    private static final Logger logger = LoggerFactory.getLogger(StaticFilesLoader.class);

    public static String loadStaticFile(String filename) throws StaticFileLoadException {
        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String temp = reader.readLine();

            while (temp != null) {
                fileContent.append(temp);
                temp = reader.readLine();
            }

        } catch (IOException e) {
            throw new StaticFileLoadException(e.getMessage());
        }
        return fileContent.toString();
    }

}
