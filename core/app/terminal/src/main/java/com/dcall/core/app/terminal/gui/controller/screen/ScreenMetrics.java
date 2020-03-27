package com.dcall.core.app.terminal.gui.controller.screen;

public final class ScreenMetrics {
    public int width = 0;
    public int height = 0;
    public int minX = 0;
    public int minY = 0;
    public int maxX = 0;
    public int maxY = 0;
    public int currX = 0;
    public int currY = 0;

    public ScreenMetrics() {}
    public ScreenMetrics(final ScreenMetrics metrics) { this.setMetrics(metrics); }

    public int screenPosX(final int x) { return minX + x; }
    public int screenPosY(final int y) { return minY + y; }
    public int posX() { return currX - minX; }
    public int posY() { return currY - minY; }

    public void setMetrics(final ScreenMetrics metrics) {
        this.width = metrics.width;
        this.height = metrics.height;
        this.minX = metrics.minX;
        this.minY = metrics.minY;
        this.maxX = metrics.maxX;
        this.maxY = metrics.maxY;
        this.currX = metrics.currX;
        this.currY = metrics.currY;
    }
}
