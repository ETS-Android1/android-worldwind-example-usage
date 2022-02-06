package com.github.adilcetin.worldwindexampleusage.ww.customized;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.shape.Ellipse;
import gov.nasa.worldwind.shape.ShapeAttributes;

public class CustomizedEllipse extends Ellipse {

    public CustomizedEllipse(Position center, double majorRadius, double minorRadius) {
        super(center, majorRadius, minorRadius);
    }

    public static CustomizedEllipse create(Position center, double radius) {

        CustomizedEllipse ellipse = new CustomizedEllipse(center, radius, radius);

        ShapeAttributes attrs = new ShapeAttributes();
        attrs.setOutlineColor(new gov.nasa.worldwind.render.Color(1, (float) 0.64,0, 1));
        attrs.setInteriorColor(new gov.nasa.worldwind.render.Color(1, 1, 1, (float) 0.5));        // cTransparent Gray
        ellipse.setAttributes(attrs);
        ellipse.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        ellipse.setFollowTerrain(true);

        return ellipse;
    }
}
