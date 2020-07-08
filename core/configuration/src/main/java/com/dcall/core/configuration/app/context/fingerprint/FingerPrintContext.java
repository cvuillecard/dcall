package com.dcall.core.configuration.app.context.fingerprint;

import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;

import java.util.*;

public final class FingerPrintContext {
    private Map<String, FingerPrint> fingerprints = new HashMap<>();
    private Iterator<String> iterator = fingerprints.keySet().iterator();
    private FingerPrint current;

    // getters
    public Map<String, FingerPrint> getFingerprints() { return fingerprints; }

    // setters
    public FingerPrintContext setFingerprints(Map<String, FingerPrint> fingerprints) { this.fingerprints = fingerprints; return this; }
    public Iterator<String> iterator() { return (iterator = fingerprints.keySet().iterator()); }
    public FingerPrint next(final Iterator<String> iterator) { return (current = fingerprints.get(iterator.next())); }
    public FingerPrint current() { return current; }
}
