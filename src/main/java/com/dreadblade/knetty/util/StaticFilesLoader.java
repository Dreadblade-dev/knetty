package com.dreadblade.knetty.util;

import com.dreadblade.knetty.exception.StaticFileLoadException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StaticFilesLoader {
    public static String loadStaticFile(String filename) throws StaticFileLoadException {
        try (InputStream inputStream = new FileInputStream(filename)) {

            byte[] fileData = inputStream.readAllBytes();
            return new String(fileData);
        } catch (IOException e) {
            throw new StaticFileLoadException(e.getMessage());
        }
    }

    public static boolean isFileExists(String filename) {
        return new File(filename).exists();
    }
}
