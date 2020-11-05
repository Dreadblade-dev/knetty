package com.dreadblade.knetty.util;

import com.dreadblade.knetty.exception.StaticFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StaticFilesLoader {
    private static final Logger logger = LoggerFactory.getLogger(StaticFilesLoader.class);

    public static String loadStaticFile(String filename) throws StaticFileNotFoundException {
        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String temp = reader.readLine();

            while (temp != null) {
                fileContent.append(temp);
                temp = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            logger.info("Static file not found!");
            throw new StaticFileNotFoundException();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return fileContent.toString();
    }

}
