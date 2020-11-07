package com.dreadblade.knetty.util;

import com.dreadblade.knetty.exception.StaticFileLoadException;

import java.io.*;

public class StaticFilesLoader {
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

    public static boolean isFileExists(String filename) {
        return new File(filename).exists();
    }
}
