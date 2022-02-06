package com.github.adilcetin.worldwindexampleusage.ww.customized;

import com.github.adilcetin.worldwindexampleusage.WorldwindMapManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gov.nasa.worldwind.layer.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;

public class WWGraphicLayer extends RenderableLayer {

    public static String ID = WorldwindMapManager.ID;
    public static String TYPE = WorldwindMapManager.TYPE;

    public List<Renderable> graphics() {
        return this.renderables;
    }

    public List<Renderable> findGraphic(WorldwindMapManager.MapObjectType type, String id) {
        try {
            synchronized (graphics()){
                return graphics()
                        .stream()
                        .filter(graphic -> graphic.hasUserProperty(ID) &&
                                graphic.getUserProperty(ID).toString().equals(id) &&
                                graphic.hasUserProperty(TYPE) &&
                                graphic.getUserProperty(TYPE).equals(type.getCode()))
                        .collect(Collectors.toList());
            }
        }
        catch (Exception ignored){ }
        return new ArrayList<>();
    }

    public void removeGraphic(WorldwindMapManager.MapObjectType type, String id){
        List<Renderable> deletedGraphics = findGraphic(type, id);

        if(deletedGraphics != null){
            for (Renderable g : deletedGraphics) {
                synchronized (graphics()){
                    graphics().remove(g);
                }
            }
        }
    }
}
