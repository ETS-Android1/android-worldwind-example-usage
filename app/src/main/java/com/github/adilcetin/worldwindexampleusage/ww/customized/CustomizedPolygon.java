package com.github.adilcetin.worldwindexampleusage.ww.customized;

import android.util.Log;

import com.github.adilcetin.worldwindexampleusage.WorldwindMapManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Color;
import gov.nasa.worldwind.shape.Polygon;
import gov.nasa.worldwind.shape.ShapeAttributes;

public class CustomizedPolygon extends Polygon {

    public static String JSON_TAG = "rings";

    public static CustomizedPolygon create() {
        CustomizedPolygon polygon = new CustomizedPolygon();
        ShapeAttributes attrs = new ShapeAttributes();
        attrs.setOutlineWidth(3);
        attrs.setOutlineColor(new Color(1, (float) 0.64,0, 1));
        attrs.setInteriorColor(new Color(1,1,1, (float) 0.3));

        ShapeAttributes highlightedAttrs = new ShapeAttributes();
        highlightedAttrs.setOutlineWidth(3);
        highlightedAttrs.setOutlineColor(new Color(1, (float) 0.64,0, 1));
        highlightedAttrs.setInteriorColor(new Color(0,1,1, (float) 0.3));

        polygon.setFollowTerrain(true);
        polygon.setExtrude(true);
        polygon.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        polygon.setAttributes(attrs);
        polygon.setHighlightAttributes(highlightedAttrs);

       return polygon;
    }

    public String toJson() {
        return WWGraphicToJson.createJsonRepresentation(getBoundary(0), WorldwindMapManager.MapObjectType.POLYGON);
    }

    public static CustomizedPolygon fromJson(String jsonRep) {
        CustomizedPolygon polygon = create();

        try {
            JsonElement root = JsonParser.parseString(jsonRep);
            List<Position> positions = new ArrayList<>();

            for(JsonElement jsonElement : root.getAsJsonObject().get(JSON_TAG).getAsJsonArray().get(0).getAsJsonArray()) {
                try {
                    positions.add(new Position(jsonElement.getAsJsonArray().get(1).getAsDouble(),
                            jsonElement.getAsJsonArray().get(0).getAsDouble(), 0));
                }
                catch (Exception e) {
                    Log.e("Polygon", "", e);
                }
            }

            polygon.addBoundary(positions);

        }
        catch (Exception e) {
            Log.e("Polygon", "", e);
        }
        return polygon;
    }

}
