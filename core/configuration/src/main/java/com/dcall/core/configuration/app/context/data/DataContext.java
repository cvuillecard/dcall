package com.dcall.core.configuration.app.context.data;

import com.dcall.core.configuration.app.context.transfer.TransferContext;

public final class DataContext {
    private final TransferContext transferContext = new TransferContext();

    public TransferContext transferContext() { return transferContext; }
}
