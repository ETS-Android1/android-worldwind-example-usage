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
import gov.nasa.worldwind.shape.Path;
import gov.nasa.worldwind.shape.ShapeAttributes;

public class CustomizedPolyline extends Path {

    public static String JSON_TAG = "paths";

    public static CustomizedPolyline create() {

        CustomizedPolyline path = new CustomizedPolyline();

        ShapeAttributes attrs = new ShapeAttributes();
        attrs.setOutlineWidth(4);
        attrs.setOutlineColor(new Color(1, (float) 0.64,0, 1));

        ShapeAttributes highlightedAttrs = new ShapeAttributes();
        highlightedAttrs.setOutlineWidth(4);
        highlightedAttrs.setOutlineColor(new Color(0,1,1, 1));

        path.setAttributes(attrs);
        path.setHighlightAttributes(highlightedAttrs);
        path.setFollowTerrain(true);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // clamp the path vertices to the ground

        return path;
    }

    public String toJson() {
        return WWGraphicToJson.createJsonRepresentation(getPositions(), WorldwindMapManager.MapObjectType.POLYLINE);
    }

    public static CustomizedPolyline fromJson(String jsonRep) {
        CustomizedPolyline path = create();

        try {
            JsonElement root = JsonParser.parseString(jsonRep);
            List<Position> positions = new ArrayList<>();

            for(JsonElement jsonElement : root.getAsJsonObject().get(JSON_TAG).getAsJsonArray().get(0).getAsJsonArray()) {
                try {
                    positions.add(new Position(jsonElement.getAsJsonArray().get(1).getAsDouble(),
                            jsonElement.getAsJsonArray().get(0).getAsDouble(), 0));
                }
                catch (Exception e) {
                    Log.e("Polyline","", e);
                }
            }

            path.setPositions(positions);

        }
        catch (Exception e) {
            Log.e("Polyline","", e);
        }
        return path;
    }
}
