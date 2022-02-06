package com.github.adilcetin.worldwindexampleusage.ww.geom.coords;/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */


import com.github.adilcetin.worldwindexampleusage.ww.avlist.AVKey;
import com.github.adilcetin.worldwindexampleusage.ww.geom.Angle;
import com.github.adilcetin.worldwindexampleusage.ww.geom.LatLon;
import com.github.adilcetin.worldwindexampleusage.ww.util.WWUtil;
/**
 * This immutable class holds a set of UTM coordinates along with it's corresponding latitude and longitude.
 *
 * @author Patrick Murris
 * @version $Id$
 */

public class UTMCoord
{
    private final Angle latitude;
    private final Angle longitude;
    private final String hemisphere;
    private final int zone;
    private final double easting;
    private final double northing;
    private Angle centralMeridian;


    public static UTMCoord fromLatLon(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }

        final UTMCoordConverter converter = new UTMCoordConverter();
        long err = converter.convertGeodeticToUTM(latitude.radians, longitude.radians);

        if (err != UTMCoordConverter.UTM_NO_ERROR)
        {
            throw new IllegalArgumentException("UTM Conversion Error");
        }

        return new UTMCoord(latitude, longitude, converter.getZone(), converter.getHemisphere(),
            converter.getEasting(), converter.getNorthing(), Angle.fromRadians(converter.getCentralMeridian()));
    }

    public static UTMCoord fromLatLon(Double latitude, Double longitude)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }

        return UTMCoord.fromLatLon(Angle.fromDegrees(latitude), Angle.fromDegreesLongitude(longitude));
    }

    public static UTMCoord fromLatLon(Angle latitude, Angle longitude, String datum)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }

        UTMCoordConverter converter;
        if (!WWUtil.isEmpty(datum) && datum.equals("NAD27"))
        {
            converter = new UTMCoordConverter(UTMCoordConverter.CLARKE_A, UTMCoordConverter.CLARKE_F);
            LatLon llNAD27 = UTMCoordConverter.convertWGS84ToNAD27(latitude, longitude);
            latitude = llNAD27.getLatitude();
            longitude = llNAD27.getLongitude();
        }
        else
        {
            converter = new UTMCoordConverter(UTMCoordConverter.WGS84_A, UTMCoordConverter.WGS84_F);
        }

        long err = converter.convertGeodeticToUTM(latitude.radians, longitude.radians);

        if (err != UTMCoordConverter.UTM_NO_ERROR)
        {
            throw new IllegalArgumentException("UTM Conversion Error");
        }

        return new UTMCoord(latitude, longitude, converter.getZone(), converter.getHemisphere(),
            converter.getEasting(), converter.getNorthing(), Angle.fromRadians(converter.getCentralMeridian()));
    }


    public static UTMCoord fromUTM(int zone, String hemisphere, double easting, double northing)
    {
        final UTMCoordConverter converter = new UTMCoordConverter();
        long err = converter.convertUTMToGeodetic(zone, hemisphere, easting, northing);

        if (err != UTMCoordConverter.UTM_NO_ERROR)
        {
            throw new IllegalArgumentException("UTM Conversion Error");
        }

        return new UTMCoord(Angle.fromRadians(converter.getLatitude()),
            Angle.fromRadians(converter.getLongitude()),
            zone, hemisphere, easting, northing, Angle.fromRadians(converter.getCentralMeridian()));
    }


    public static LatLon locationFromUTMCoord(int zone, String hemisphere, double easting, double northing)
    {
        UTMCoord coord = UTMCoord.fromUTM(zone, hemisphere, easting, northing);
        return new LatLon(coord.getLatitude(), coord.getLongitude());
    }

    public UTMCoord(Angle latitude, Angle longitude, int zone, String hemisphere, double easting, double northing)
    {
        this(latitude, longitude, zone, hemisphere, easting, northing, Angle.fromDegreesLongitude(0.0));
    }


    public UTMCoord(Angle latitude, Angle longitude, int zone, String hemisphere, double easting, double northing,
        Angle centralMeridian)
    {
        if (latitude == null || longitude == null)
        {
            throw new IllegalArgumentException("Latitude Or Longitude Is Null");
        }

        this.latitude = latitude;
        this.longitude = longitude;
        this.hemisphere = hemisphere;
        this.zone = zone;
        this.easting = easting;
        this.northing = northing;
        this.centralMeridian = centralMeridian;
    }

    public Angle getCentralMeridian()
    {
        return this.centralMeridian;
    }

    public Angle getLatitude()
    {
        return this.latitude;
    }

    public Angle getLongitude()
    {
        return this.longitude;
    }

    public int getZone()
    {
        return this.zone;
    }

    public String getHemisphere()
    {
        return this.hemisphere;
    }

    public double getEasting()
    {
        return this.easting;
    }

    public double getNorthing()
    {
        return this.northing;
    }

    public String getZoneStr()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(zone);
        sb.append(" ").append(AVKey.NORTH.equals(hemisphere) ? "N" : "S");
        return sb.toString();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(zone);
        sb.append(" ").append(AVKey.NORTH.equals(hemisphere) ? "N" : "S");
        sb.append(" ").append(easting);
        sb.append(" ").append(northing);
        return sb.toString();
    }
}
