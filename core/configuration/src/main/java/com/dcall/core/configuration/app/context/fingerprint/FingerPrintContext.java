package com.dcall.core.configuration.app.context.fingerprint;

import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;

import java.util.HashMap;
import java.util.Map;

public final class FingerPrintContext {
    private Map<String, FingerPrint> fingerprints = new HashMap<>();

    // getters
    public Map<String, FingerPrint> getFingerprints() { return fingerprints; }

    // setters
    public FingerPrintContext setFingerprints(Map<String, FingerPrint> fingerprints) { this.fingerprints = fingerprints; return this; }
}
