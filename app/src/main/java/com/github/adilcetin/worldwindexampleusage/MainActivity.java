package com.github.adilcetin.worldwindexampleusage;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private WorldwindMapManager worldwindMapManager = new WorldwindMapManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    protected void initUI() {
        ViewGroup map = findViewById(R.id.mapView);
        ViewGroup sketch = findViewById(R.id.map_sketch_container);
        ViewGroup delete = findViewById(R.id.map_delete_container);

        worldwindMapManager.initMap(this, map, sketch, delete);

        findViewById(R.id.mapoperations_imageview_zoomin).setOnClickListener(view -> worldwindMapManager.zoomIn());
        findViewById(R.id.mapoperations_imageview_zoomout).setOnClickListener(view -> worldwindMapManager.zoomOut());
    }
}