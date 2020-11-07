package com.dreadblade.knetty.network.http.selectionkey;

import com.dreadblade.knetty.exception.InvalidSelectionKeyCommandException;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class SelectionKeyCommandFactory {
    private final SelectionKeyCommand accept;
    private final SelectionKeyCommand read;
    private final SelectionKeyCommand write;

    public SelectionKeyCommandFactory(Selector selector) {
        this.accept = new AcceptSelectionKeyCommand(selector);
        this.read = new ReadSelectionKeyCommand();
        this.write = new WriteSelectionKeyCommand();
    }

    public SelectionKeyCommand getSelectionKeyCommand(SelectionKey selectionKey) throws InvalidSelectionKeyCommandException {
        if (selectionKey.isAcceptable()) {
            return accept;
        }

        if (selectionKey.isReadable()) {
            return read;
        }

        if (selectionKey.isWritable()) {
            return write;
        }

        throw new InvalidSelectionKeyCommandException();
    }
}
