package com.github.adilcetin.worldwindexampleusage.ww.geom;

import android.graphics.PointF;

public class Point2D extends PointF {

    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point2D(double x, double y) {
        this.set((float) x,(float) y);
        this.x = x;
        this.y = y;
    }

    public final void set(double x, double y) {
        this.set((float) x,(float) y);
        this.x = x;
        this.y = y;
    }
}