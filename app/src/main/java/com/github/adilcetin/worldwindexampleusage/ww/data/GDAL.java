package com.github.adilcetin.worldwindexampleusage.ww.data;/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

import com.github.adilcetin.worldwindexampleusage.ww.geom.Point2D;

import java.util.ArrayList;


/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class GDAL {

    private static final int GT_SIZE = 6;

    // Y = lat , X = long
    private static final int GT_0_PIXEL_X = 0;
    private static final int GT_1_ROTATION_Y = 1;
    private static final int GT_2_ROTATION_X = 2;
    private static final int GT_3_PIXEL_Y= 3;
    private static final int GT_4_REFERENCE_X = 4;
    private static final int GT_5_REFERENCE_Y = 5;

    public static Point2D[] computeCornersFromGeotransform(ArrayList<Double> gt, int width, int height) {
        if (null == gt || gt.size() != GDAL.GT_SIZE)
            return null;

        Point2D[] corners = new Point2D[]
            {
                getGeoPointForRasterPoint(gt, 0, height),
                getGeoPointForRasterPoint(gt, width, height),
                getGeoPointForRasterPoint(gt, width, 0),
                getGeoPointForRasterPoint(gt, 0, 0)
            };

        return corners;
    }

    private static Point2D getGeoPointForRasterPoint(ArrayList<Double> gt, int x, int y) {

        double easting = gt.get(GT_4_REFERENCE_X) + gt.get(GDAL.GT_0_PIXEL_X) * (double) x
                + gt.get(GDAL.GT_2_ROTATION_X) * (double) y;

        double northing = gt.get(GDAL.GT_5_REFERENCE_Y) + gt.get(GT_1_ROTATION_Y) * (double) x
                + gt.get(GDAL.GT_3_PIXEL_Y) * (double) y;

        return new Point2D(easting, northing);
    }

    public static Double getMinLong(Point2D[] points) {
        if (points != null) {

            double min = Double.MAX_VALUE;
            for (Point2D point : points) {
                min = (point.getX() < min) ? point.getX() : min;
            }

            return min;
        }
        return null;
    }

    public static Double getMaxLong(Point2D[] points) {
        if (points != null) {

            double max = -Double.MAX_VALUE;
            for (Point2D point : points) {
                max = (point.getX() > max) ? point.getX() : max;
            }

            return max;
        }
        return null;
    }

    public static Double getMinLat(Point2D[] points) {
        if (points != null) {

            double min = Double.MAX_VALUE;
            for (Point2D point : points) {
                min = (point.getY() < min) ? point.getY() : min;
            }

            return min;
        }
        return null;
    }

    public static Double getMaxLat(Point2D[] points) {
        if (points != null) {

            double max = -Double.MAX_VALUE;
            for (Point2D point : points)
            {
                max = (point.getY() > max) ? point.getY() : max;
            }

            return max;
        }
        return null;
    }

    public static Double getDeltaLat(Point2D[] points) {
        if (points != null) {
            double delta = getMaxLat(points) - getMinLat(points);
            return delta;
        }
        return null;
    }

    public static Double getDeltaLong(Point2D[] points) {
        if (points != null) {
            double delta = getMaxLong(points) - getMinLong(points);
            return delta;
        }
        return null;
    }
}
