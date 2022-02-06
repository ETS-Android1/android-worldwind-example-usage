package com.github.adilcetin.worldwindexampleusage;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.adilcetin.worldwindexampleusage.ww.customized.CustomizedPolygon;
import com.github.adilcetin.worldwindexampleusage.ww.customized.CustomizedPolyline;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import gov.nasa.worldwind.BasicWorldWindowController;
import gov.nasa.worldwind.PickedObject;
import gov.nasa.worldwind.PickedObjectList;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layer.RenderableLayer;
import gov.nasa.worldwind.render.ImageSource;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.shape.Highlightable;
import gov.nasa.worldwind.shape.Placemark;
import gov.nasa.worldwind.shape.PlacemarkAttributes;

// Listener Of MAP
public class WorldWindowController extends BasicWorldWindowController {

    private Renderable pickedObject;
    private Context context;
    private WorldwindMapManager mapManager;
    private InteractionMode interactionMode = InteractionMode.NONE;
    private Queue<Position> sketchPointQueue = new ArrayDeque<>();
    private String selectedItemId;
    private WorldwindMapManager.MapObjectType selectedItemType;

    private RenderableLayer drawingLayer = new RenderableLayer();
    private CustomizedPolyline path;
    private CustomizedPolygon polygon;

    public enum InteractionMode {
        SKETCH_POLYLINE,
        SKETCH_POLYGON,
        NONE
    }

    public InteractionMode getInteractionMode() {
        return interactionMode;
    }

    public Queue<Position> getSketchPointQueue() {
        return sketchPointQueue;
    }

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public WorldwindMapManager.MapObjectType getSelectedItemType() {
        return selectedItemType;
    }

    public WorldWindowController(Context context, WorldwindMapManager mapManager) {
        this.context = context;
        this.mapManager = mapManager;
    }

    private GestureDetector pickGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent event) {
            pick(event);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mapManager.zoomIn();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toggleSelection();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(MotionEvent event) {
            super.onLongPress(event);
            if(interactionMode != InteractionMode.NONE){
                return;
            }

            mapManager.setAllSymbolsAsUnselected();

            PickedObjectList pickList = getWorldWindow().pick(event.getX(), event.getY());
            PickedObject topPickedObject = pickList.topPickedObject();
            if (topPickedObject != null) {
                mapManager.showCallOut(topPickedObject.getTerrainPosition());
            }
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = this.pickGestureDetector.onTouchEvent(event);
        if (!consumed) {
            return super.onTouchEvent(event);
        }
        return consumed;
    }

    private void pick(MotionEvent event) {
        this.pickedObject = null;

        PickedObjectList pickList = getWorldWindow().pick(event.getX(), event.getY());
        PickedObject topPickedObject = pickList.topPickedObject();

        if (topPickedObject != null) {

            if(interactionMode == InteractionMode.SKETCH_POLYGON || interactionMode == InteractionMode.SKETCH_POLYLINE){
                sketchPointQueue.add(topPickedObject.getTerrainPosition());

                Placemark point = new Placemark(topPickedObject.getTerrainPosition(),
                        PlacemarkAttributes.createWithImage(ImageSource.fromResource(R.drawable.ic_action_point)));

                List<Position> positions = new ArrayList<>(sketchPointQueue);

                if(interactionMode == InteractionMode.SKETCH_POLYLINE && positions.size() > 1) {
                    path.setPositions(positions);
                }
                else if(interactionMode == InteractionMode.SKETCH_POLYGON && positions.size() > 2){
                    if(polygon.getBoundaryCount() == 1) {
                        polygon.removeBoundary(0);
                    }
                    polygon.addBoundary(0, positions);
                }

                // Bluemarble ve Graphic layer üzerine eklenmeli geçici bir katman çünkü
                if(getWorldWindow().getLayers().count() == 2) {
                    getWorldWindow().getLayers().addLayer(drawingLayer);
                }
                drawingLayer.addRenderable(point);
                getWorldWindow().requestRedraw();
                return;
            }

            try {
                this.pickedObject = (Renderable) topPickedObject.getUserObject();
            }
            catch (Exception e){
                this.pickedObject = null;
            }
        }
    }

    private void toggleSelection() {

        if(interactionMode != InteractionMode.NONE){
            return;
        }

        setHighlighted();

        if (pickedObject != null&& pickedObject.getUserProperty(WorldwindMapManager.TYPE) != null &&
                (pickedObject.getUserProperty(WorldwindMapManager.TYPE).equals(WorldwindMapManager.MapObjectType.POI.getCode()) ||
                        pickedObject.getUserProperty(WorldwindMapManager.TYPE).equals(WorldwindMapManager.MapObjectType.POLYGON.getCode()) ||
                        pickedObject.getUserProperty(WorldwindMapManager.TYPE).equals(WorldwindMapManager.MapObjectType.POLYLINE.getCode()))) {

            selectedItemId = String.valueOf(pickedObject.getUserProperty(WorldwindMapManager.ID));
            selectedItemType = WorldwindMapManager.MapObjectType.getById((Integer) pickedObject.getUserProperty(WorldwindMapManager.TYPE));

            mapManager.showDeleteContainer();

            Toast.makeText(mapManager.getViewContext(), "Selected Item: " + selectedItemType.getTitle(),
                    Toast.LENGTH_LONG).show();
        } else {
            Log.d(this.getClass().getCanonicalName(), "On next empty map selection!");
            mapManager.hideCallOut();
            mapManager.hideDeleteContainer();
        }
    }

    private void setHighlighted(){
        mapManager.setAllSymbolsAsUnselected();

        if(this.pickedObject instanceof Highlightable){
            ((Highlightable) pickedObject).setHighlighted(true);
        }
        this.getWorldWindow().requestRedraw();
    }

    public void setMapInteractionMode(InteractionMode mapInteractionMode) {

        switch (mapInteractionMode){
            case NONE:
                drawingLayer.clearRenderables();
                sketchPointQueue.clear();
                getWorldWindow().requestRedraw();
                break;
            case SKETCH_POLYLINE:
                path = CustomizedPolyline.create();
                drawingLayer.addRenderable(path);
                break;
            case SKETCH_POLYGON:
                polygon = CustomizedPolygon.create();
                drawingLayer.addRenderable(polygon);
        }
        this.interactionMode = mapInteractionMode;
    }
}