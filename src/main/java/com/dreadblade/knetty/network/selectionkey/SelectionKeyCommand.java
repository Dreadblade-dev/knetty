package com.dreadblade.knetty.network.selectionkey;

import java.nio.channels.SelectionKey;

public interface SelectionKeyCommand {
    void execute(SelectionKey selectionKey);
}
