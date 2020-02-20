package com.dcall.core.app.terminal.gui.controller.screen;

public final class ScreenMetrics {
    public static int width;
    public static int height;
    public static int minX;
    public static int minY;
    public static int maxX;
    public static int maxY;
    public static int currX = 0;
    public static int currY = 0;

    public static int screenPosX(final int x) { return minX + x; }
    public static int screenPosY(final int y) { return minY + y; }
    public static int posX() { return currX - minX; }
    public static int posY() { return currY - minY; }
}
