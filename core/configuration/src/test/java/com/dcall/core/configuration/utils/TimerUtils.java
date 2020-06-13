package com.dcall.core.configuration.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TimerUtils.class);

    private final long ms = 1000000;
    private long startTime = 0;
    private long endTime = 0;

    public TimerUtils startTimer() { setStartTime(System.nanoTime()); return this; }
    public TimerUtils stopTimer() { setEndTime(System.nanoTime()); return this; }

    public TimerUtils logTime(final String msg) {
        final long nano = getExecTime();
        final String timeMsg = " > execution time : " + nano + " ns / " + (nano / ms) + " ms.";

        LOG.debug(msg != null && !msg.isEmpty() ? msg + timeMsg : timeMsg);

        return this;
    }

    // getter
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getExecTime() { return endTime - startTime; }

    // setter
    public TimerUtils setStartTime(final long startTime) { this.startTime = startTime; return this; }
    public TimerUtils setEndTime(final long endTime) { this.endTime = endTime; return this; }
}
