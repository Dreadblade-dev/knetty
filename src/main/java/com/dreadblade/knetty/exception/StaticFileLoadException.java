package com.dreadblade.knetty.exception;

import java.io.IOException;

public class StaticFileLoadException extends IOException {
    public StaticFileLoadException() {

    }

    public StaticFileLoadException(String message) {
        super(message);
    }
}
