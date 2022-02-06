package com.github.adilcetin.worldwindexampleusage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.adilcetin.worldwindexampleusage.ww.customized.CustomizedEllipse;
import com.github.adilcetin.worldwindexampleusage.ww.customized.CustomizedPolygon;
import com.github.adilcetin.worldwindexampleusage.ww.customized.CustomizedPolyline;
import com.github.adilcetin.worldwindexampleusage.ww.customized.CustomizedWorldWindow;
import com.github.adilcetin.worldwindexampleusage.ww.customized.WWGraphicLayer;
import com.github.adilcetin.worldwindexampleusage.ww.milstd2525.MilStd2525;
import com.github.adilcetin.worldwindexampleusage.ww.milstd2525.MilStd2525Sybols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Camera;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globe.BasicElevationCoverage;
import gov.nasa.worldwind.layer.BackgroundLayer;
import gov.nasa.worldwind.ogc.WmsLayer;
import gov.nasa.worldwind.ogc.WmsLayerConfig;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.shape.Highlightable;
import gov.nasa.worldwind.shape.Label;
import gov.nasa.worldwind.shape.Placemark;
import gov.nasa.worldwind.shape.TextAttributes;

public class WorldwindMapManager {

    public enum MapObjectType{
        POI(1, "Point of Interest"),
        POLYLINE(2, "Polyline"),
        POLYGON(3, "Polygon"),
        ELLIPSE(4, "Ellipse"),
        ELLIPSE_TITLE(5, "Ellipse");

        private final int code;
        private final String title;

        public String getTitle() {
            return title;
        }

        public int getCode() {
            return code;
        }

        MapObjectType(int i, String title) {
            this.code = i;
            this.title = title;
        }

        public static MapObjectType getById(int code) {
            return Arrays.stream(MapObjectType.values())
                    .filter(obj -> obj.code == code)
                    .collect(Collectors.toList()).get(0);
        }
    }

    private boolean isVMSExist = false;
    private ViewGroup mapView;
    private WorldWindow worldWindow;
    private WorldWindowController worldWindowController;
    private WmsLayer wmsLayer;
    private MainActivity viewContext;
    private View mapCallOut;
    private ViewGroup mapSketchController;
    private ViewGroup mapDeleteController;
    private final WWGraphicLayer graphicLayer = new WWGraphicLayer();

    public static final String ID = "GRAPHIC_ID";
    public static final String TYPE = "GRAPHIC_TYPE";
    private boolean isNavigateToOwnUnit  = true;
    private static final double MAP_MAX_ZOOM_IN_VALUE = 50;  // 50 meters
    private static final double MAP_MAX_ZOOM_OUT_VALUE = 1.1317611996036978E7;
    private static final double FOLLOW_OWN_POSITION_CAMERA_ALTITUDE_VALUE = 1000;

    public void initMap(Context viewContext, ViewGroup mapView, ViewGroup mapSketchController, ViewGroup mapDeleteController) {
        this.viewContext = (MainActivity) viewContext;
        this.mapView = mapView;
        this.mapSketchController = mapSketchController;
        this.mapDeleteController = mapDeleteController;

        mapSketchController.findViewById(R.id.undoButton).setVisibility(View.GONE);
        mapSketchController.findViewById(R.id.redoButton).setVisibility(View.GONE);

        addDeleteListener(mapDeleteController.findViewById(R.id.deleteButton));
        addStopListener(mapSketchController.findViewById(R.id.stopButton));
        addCancelListener(mapSketchController.findViewById(R.id.cancelButton));

        CompletableFuture.runAsync(() -> MilStd2525.initializeRenderer(viewContext));

        worldWindow = new CustomizedWorldWindow(viewContext);
        worldWindowController = new WorldWindowController(viewContext, this);
        worldWindow.setWorldWindowController(worldWindowController);

        loadMapBaseLayer();

        worldWindow.getGlobe().getElevationModel().addCoverage(new BasicElevationCoverage());

        worldWindow.getLayers().addLayer(1, graphicLayer);
        mapView.addView(worldWindow);

        // rawCircle(1, new Position(45, 40, 0), 10000);
    }

    public MainActivity getViewContext() {
        return viewContext;
    }

    private WmsLayer getCurrentWmsLayer(){
       this.wmsLayer = new WmsLayer(new Sector().setFullSphere(),
                1e3,
                new WmsLayerConfig("*** WRITE YOUR VMS ADDRESS ***", "*** WRITE YOUR VMS LAYER NAME ***"));
        return wmsLayer;
    }

    private void loadMapBaseLayer(){
        if(worldWindow.getLayers().count() > 0){
            worldWindow.getLayers().removeLayer(0);
        }

        if(!isVMSExist){
            worldWindow.getLayers().addLayer(0, new BackgroundLayer());
        }
        else{
            worldWindow.getLayers().addLayer(0, getCurrentWmsLayer());
        }
    }

    public void addPOI(Position position) {
        if (position == null) {
            return;
        }

        Placemark placemark = new Placemark(position,
                MilStd2525.getPlacemarkAttributes(MilStd2525Sybols.getRandomly(), new SparseArray<>(), null).setImageScale(1));
        placemark.setHighlightAttributes(placemark.getAttributes().setImageScale(1.2));

        placemark.putUserProperty(TYPE, MapObjectType.POI.code);
        placemark.putUserProperty(ID, new Random().nextInt());

        synchronized (graphicLayer.graphics()){
            graphicLayer.addRenderable(placemark);
        }
        worldWindow.requestRedraw();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void showCallOut(Position mapPoint){

        if(mapCallOut != null) {
            return;
        }

        this.mapCallOut = viewContext.getLayoutInflater().inflate(R.layout.map_callout, null);
        TextView latitude = mapCallOut.findViewById(R.id.latitude);
        TextView longitude = mapCallOut.findViewById(R.id.longitude);
        TextView altitude = mapCallOut.findViewById(R.id.altitude);

        latitude.setText(String.format("%.4f", mapPoint.latitude)+" ");
        longitude.setText(" "+String.format("%.4f", mapPoint.longitude));

        mapView.addView(mapCallOut);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        mapCallOut.setLayoutParams(params);

        mapCallOut.findViewById(R.id.freehandLineButton).setVisibility(View.GONE);
        mapCallOut.findViewById(R.id.freehandPolygonButton).setVisibility(View.GONE);

        addPOIListener(mapCallOut.findViewById(R.id.pointButton), mapPoint);
        addPolylineListener(mapCallOut.findViewById(R.id.polylineButton));
        addPolygonListener(mapCallOut.findViewById(R.id.polygonButton));
    }

    private void addPolylineListener(ImageButton imageButton){
        imageButton.setOnClickListener(v -> {
            hideCallOut();
            mapSketchController.setVisibility(View.VISIBLE);
            worldWindowController.setMapInteractionMode(WorldWindowController.InteractionMode.SKETCH_POLYLINE);
        });
    }

    private void addPolygonListener(ImageButton imageButton){
        imageButton.setOnClickListener(v -> {
            hideCallOut();
            mapSketchController.setVisibility(View.VISIBLE);
            worldWindowController.setMapInteractionMode(WorldWindowController.InteractionMode.SKETCH_POLYGON);
        });
    }

    private void addCancelListener(ImageButton imageButton){
        imageButton.setOnClickListener(v -> {
            mapSketchController.setVisibility(View.INVISIBLE);
            worldWindowController.setMapInteractionMode(WorldWindowController.InteractionMode.NONE);
        });
    }

    private void addDeleteListener(ImageButton imageButton){
        imageButton.setOnClickListener(v -> {
            mapDeleteController.setVisibility(View.INVISIBLE);
            worldWindowController.setMapInteractionMode(WorldWindowController.InteractionMode.NONE);
            deleteSelectedGraphicFromMap();
        });
    }

    public void showDeleteContainer() {
        hideCallOut();
        mapDeleteController.setVisibility(View.VISIBLE);
    }

    public void hideDeleteContainer() {
        mapDeleteController.setVisibility(View.INVISIBLE);
    }

    private void addStopListener(ImageButton imageButton){
        imageButton.setOnClickListener(v -> {
            mapSketchController.setVisibility(View.INVISIBLE);

            switch (worldWindowController.getInteractionMode()) {
                case SKETCH_POLYGON:
                    sketchPolygon();
                    break;
                case SKETCH_POLYLINE:
                    sketchPolyline();
                    break;
            }

            worldWindowController.setMapInteractionMode(WorldWindowController.InteractionMode.NONE);
            worldWindow.requestRedraw();
        });
    }

    private void sketchPolyline() {
        int id = new Random().nextInt() & Integer.MAX_VALUE;

        CustomizedPolyline path = CustomizedPolyline.create();
        List<Position> positions = new ArrayList<>(worldWindowController.getSketchPointQueue());

        path.setPositions(positions);
        path.putUserProperty(TYPE, MapObjectType.POLYLINE.getCode());
        path.putUserProperty(ID, String.valueOf(id));

        // add the graphic to the graphics overlay
        synchronized (graphicLayer.graphics()){
            graphicLayer.addRenderable(path);
        }

    }

    private void sketchPolygon() {
        int id = new Random().nextInt() & Integer.MAX_VALUE;

        CustomizedPolygon polygon = CustomizedPolygon.create();
        List<Position> positions = new ArrayList<>(worldWindowController.getSketchPointQueue());

        polygon.addBoundary(positions);
        polygon.putUserProperty(TYPE, MapObjectType.POLYGON.getCode());
        polygon.putUserProperty(ID, String.valueOf(id));

        // add the graphic to the graphics overlay
        synchronized (graphicLayer.graphics()){
            graphicLayer.addRenderable(polygon);
        }
    }

    private void addPOIListener(ImageButton imageButton, Position mapPoint){
        imageButton.setOnClickListener(v -> {
            hideCallOut();

            addPOI(mapPoint);
        });
    }

    void hideCallOut(){
        if(mapCallOut != null){
            mapView.removeView(mapCallOut);
            mapCallOut = null;
        }
    }

    public void drawCircle(int ellipseID, Position center, int radius) {

        List<Renderable> foundSummary = graphicLayer.findGraphic(MapObjectType.ELLIPSE, String.valueOf(ellipseID));
        if (foundSummary.size() != 0){
            synchronized (graphicLayer.graphics()){
                graphicLayer.removeRenderable(foundSummary.get(0));
            }
        }

        CustomizedEllipse ellipse = CustomizedEllipse.create(center, radius);
        ellipse.putUserProperty(TYPE, MapObjectType.ELLIPSE);
        ellipse.putUserProperty(ID, ellipseID);

        // TITLE
        List<Renderable> foundSummaryTitle = graphicLayer.findGraphic(MapObjectType.ELLIPSE_TITLE, String.valueOf(ellipseID));
        if (foundSummaryTitle.size() != 0){
            synchronized (graphicLayer.graphics()){
                graphicLayer.removeRenderable(foundSummaryTitle.get(0));
            }
        }

        TextAttributes textAttrs = new TextAttributes();
        textAttrs.setTextSize(60);
        Label label = new Label(center, "Ellipse " + ellipseID , textAttrs);
        label.putUserProperty(TYPE, MapObjectType.ELLIPSE_TITLE);
        label.putUserProperty(ID, ellipseID);

        synchronized (graphicLayer.graphics()){
            graphicLayer.addRenderable(ellipse);
            graphicLayer.addRenderable(label);
        }
        worldWindow.requestRedraw();
    }


    public void setAllSymbolsAsUnselected() {
        graphicLayer.graphics().forEach(graphic -> {
            if (graphic instanceof Highlightable) {
                ((Highlightable) graphic).setHighlighted(false);
            }
        });
    }

    public void deleteSelectedGraphicFromMap() {
        graphicLayer.removeGraphic(worldWindowController.getSelectedItemType(), worldWindowController.getSelectedItemId());
        worldWindow.requestRedraw();
    }

    public void goToPosition(Position position) {
        if (position != null) {
            Camera camera = new Camera();

            camera.set(position.latitude, position.longitude, FOLLOW_OWN_POSITION_CAMERA_ALTITUDE_VALUE,  // Camera Altitude 220 mt.
                    WorldWind.ABSOLUTE, 0,0,0);

            worldWindow.getNavigator().setAsCamera(worldWindow.getGlobe(), camera);
            worldWindow.requestRedraw();
        }
    }

    public void zoomOut() {
        double currentAltitude = worldWindow.getNavigator().getAltitude();
        double zoomValue = currentAltitude / 5;
        if(currentAltitude < MAP_MAX_ZOOM_OUT_VALUE){
            worldWindow.getNavigator().setAltitude(Math.min((currentAltitude + zoomValue), MAP_MAX_ZOOM_OUT_VALUE));
            worldWindow.requestRedraw();
        }
    }

    public void zoomIn() {
        double currentAltitude = worldWindow.getNavigator().getAltitude();
        double zoomValue = currentAltitude / 5;
        if(currentAltitude > MAP_MAX_ZOOM_IN_VALUE){
            worldWindow.getNavigator().setAltitude(Math.max((currentAltitude - zoomValue), MAP_MAX_ZOOM_IN_VALUE));
            worldWindow.requestRedraw();
        }
    }
}
