package com.dreadblade.knetty.network.http.selectionkey;

import java.nio.channels.SelectionKey;

public interface SelectionKeyCommand {
    void execute(SelectionKey selectionKey);
}
