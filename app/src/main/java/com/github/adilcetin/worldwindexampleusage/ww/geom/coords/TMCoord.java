package com.github.adilcetin.worldwindexampleusage.ww.geom.coords;/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

import com.github.adilcetin.worldwindexampleusage.ww.geom.Angle;

/**
 * This class holds a set of Transverse Mercator coordinates along with the
 * corresponding latitude and longitude.
 *
 * @author Patrick Murris
 * @version $Id$
 * @see TMCoordConverter
 */
public class TMCoord
{
    private final Angle latitude;
    private final Angle longitude;
    private final Angle originLatitude;
    private final Angle centralMeridian;
    private final double falseEasting;
    private final double falseNorthing;
    private final double scale;
    private final double easting;
    private final double northing;

    public static TMCoord fromLatLon(Angle latitude, Angle longitude, Double a, Double f,
                   Angle originLatitude, Angle centralMeridian,
                   double falseEasting, double falseNorthing,
                   double scale)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        if (originLatitude == null || centralMeridian == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }

        final TMCoordConverter converter = new TMCoordConverter();
        if (a == null || f == null)
        {
            a = converter.getA();
            f = converter.getF();
        }
        long err = converter.setTransverseMercatorParameters(a, f, originLatitude.radians, centralMeridian.radians,
                falseEasting, falseNorthing, scale);
        if (err == TMCoordConverter.TRANMERC_NO_ERROR)
            err = converter.convertGeodeticToTransverseMercator(latitude.radians, longitude.radians);

        if (err != TMCoordConverter.TRANMERC_NO_ERROR && err != TMCoordConverter.TRANMERC_LON_WARNING)
        {
            throw new IllegalArgumentException("TM Conversion Error");
        }

        return new TMCoord(latitude, longitude, converter.getEasting(), converter.getNorthing(),
                originLatitude, centralMeridian, falseEasting, falseNorthing, scale);
    }

    public static TMCoord fromTM(double easting, double northing,
                   Angle originLatitude, Angle centralMeridian,
                   double falseEasting, double falseNorthing,
                   double scale)
    {
        if (originLatitude == null || centralMeridian == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }

        final TMCoordConverter converter = new TMCoordConverter();

        double a = converter.getA();
        double f = converter.getF();
        long err = converter.setTransverseMercatorParameters(a, f, originLatitude.radians, centralMeridian.radians,
                falseEasting, falseNorthing, scale);
        if (err == TMCoordConverter.TRANMERC_NO_ERROR)
            err = converter.convertTransverseMercatorToGeodetic(easting, northing);

        if (err != TMCoordConverter.TRANMERC_NO_ERROR && err != TMCoordConverter.TRANMERC_LON_WARNING)
        {
            throw new IllegalArgumentException("TM Conversion Error");
        }

        return new TMCoord(Angle.fromRadians(converter.getLatitude()), Angle.fromRadians(converter.getLongitude()),
                easting, northing, originLatitude, centralMeridian, falseEasting, falseNorthing, scale);
    }

    public TMCoord(Angle latitude, Angle longitude, double easting, double northing,
                   Angle originLatitude, Angle centralMeridian,
                   double falseEasting, double falseNorthing,
                   double scale)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }
        if (originLatitude == null || centralMeridian == null)
        {
            throw new IllegalArgumentException("Angle Is Null");
        }

        this.latitude = latitude;
        this.longitude = longitude;
        this.easting = easting;
        this.northing = northing;
        this.originLatitude = originLatitude;
        this.centralMeridian = centralMeridian;
        this.falseEasting = falseEasting;
        this.falseNorthing = falseNorthing;
        this.scale = scale;
    }

    public Angle getLatitude()
    {
        return this.latitude;
    }

    public Angle getLongitude()
    {
        return this.longitude;
    }

    public Angle getOriginLatitude()
    {
        return this.originLatitude;
    }

    public Angle getCentralMeridian()
    {
        return this.centralMeridian;
    }

    public double getFalseEasting()
    {
        return this.falseEasting;
    }

    public double getFalseNorthing()
    {
        return this.falseNorthing;
    }

    public double getScale()
    {
        return this.scale;
    }

    public double getEasting()
    {
        return this.easting;
    }

    public double getNorthing()
    {
        return this.northing;
    }

}
