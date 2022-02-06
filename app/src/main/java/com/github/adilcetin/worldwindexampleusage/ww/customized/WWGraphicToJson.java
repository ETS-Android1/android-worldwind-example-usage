package com.github.adilcetin.worldwindexampleusage.ww.customized;

import com.github.adilcetin.worldwindexampleusage.WorldwindMapManager;

import java.util.List;

import gov.nasa.worldwind.geom.Position;

public class WWGraphicToJson {

    public static String createJsonRepresentation(List<Position> positions, WorldwindMapManager.MapObjectType mapObjectType){
        String jsonTag = "";
        if(mapObjectType == WorldwindMapManager.MapObjectType.POLYLINE) {
            jsonTag = CustomizedPolyline.JSON_TAG;
        }
        else if(mapObjectType == WorldwindMapManager.MapObjectType.POLYGON) {
            jsonTag = CustomizedPolygon.JSON_TAG;
        }
        else {
            System.out.println("WorldWindGraphicToJson: " + "Unknown graphic type for world wind");
        }

        StringBuilder jsonRepresentation = new StringBuilder("{\"" + jsonTag + "\":[[");
        for(int i=0; i<positions.size(); i++) {
            jsonRepresentation.append("[").append(positions.get(i).longitude).append(",").append(positions.get(i).latitude).append("]");
            if(i < positions.size() - 1){
                jsonRepresentation.append(",");
            }
        }
        jsonRepresentation.append("]],");
        jsonRepresentation.append("\"spatialReference\":{\"wkid\":4326}}");
        return jsonRepresentation.toString();
    }
}
