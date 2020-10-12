package com.dcall.core.configuration.app.context.data;

import com.dcall.core.configuration.app.context.transfer.TransferContext;

import java.io.Serializable;

public final class DataContext implements Serializable {
    private final TransferContext transferContext = new TransferContext();

    public TransferContext transferContext() { return transferContext; }
}
