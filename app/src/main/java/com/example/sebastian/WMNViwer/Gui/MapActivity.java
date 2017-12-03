package com.example.sebastian.WMNViwer.Gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sebastian.WMNViwer.Data.AllNodes;
import com.example.sebastian.WMNViwer.Data.ApiConnection;
import com.example.sebastian.WMNViwer.Data.AsyncResponse;
import com.example.sebastian.WMNViwer.Data.Node;
import com.example.sebastian.mapbox.R;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements AsyncResponse{
    private MapView mapView;
    public int zoom;
    public float markerSize = 26;
    public boolean markerScaled=false;

    private AllNodes allnodes;

    private ProgressBar spinner;

    private int requestCount;
    private int maxParallelRequest=5;
    ApiConnection apiConnection;

    Toast toast;

    Handler h = new Handler();
    int delay;
    Runnable runnable;

    Button map,table;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        spinner=(ProgressBar)findViewById(R.id.progressBar);

        setSupportActionBar(myToolbar);

        map=(Button) findViewById(R.id.map);
        table = (Button) findViewById(R.id.table);

        // Create a mapVie
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        allnodes = AllNodes.getInstance();

        requestCount=0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCameraPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        zoom= Integer.parseInt(SP.getString("zoomLevel" , "13"));

        delay = Integer.parseInt(SP.getString("refreshInterval", "15")) * 1000;
        //check if delay smaller than 1 to prevent delay from being 0
        if (delay<1) {
            delay = 1000;
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                update();

                h.postDelayed(new Runnable() {

                    public void run() {
                        //do something
                        System.out.println("onResume update");
                        update();

                        runnable = this;

                        h.postDelayed(runnable, delay);
                    }
                }, delay);

            }
        });
    }

    /**
     * Method that gets called by the "Map"- Button, updating the content of the map
     * @param  v Current View
     */
    public void updateClick(View v){
        map.setEnabled(false);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                update();

                setCameraPosition();
            }
        });
    }

    /**
     * Method that starts an Async API call, which loads a jsonFile (local or from API)
     * The Maximum of 5 parallel Requests is possible, the maximum is changeble with the variable "maxParallelRequest"
     */
    public void update(){

        if (requestCount<maxParallelRequest){
            spinner.setVisibility(View.VISIBLE);
            requestCount++;
            System.out.println("request send number: " + requestCount);

            apiConnection= new ApiConnection(this);
            apiConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getApplicationContext());

        } else {
           displayToast("too many requests already running");
        }
  }


    /**
     * displayToast() creates a toast, showing the Message on the screen. If a Toast is already showing on the screen it gets cancelled.
     *
     *
     * @param message The Message that is displayed in the Toast
     */
    public void displayToast(String message) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * sets the focus of the map to the coordinates of the first node in  the array.
     * If no nodes currenty exist it sets the focus to the Coordinates of the TH Cologne
     */
    public void setCameraPosition(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                //allnodes = AllNodes.getInstance();
                ArrayList<Node> nodes = allnodes.getNodes();
                if (nodes.size() != 0) {
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(nodes.get(0).getLat(), nodes.get(0).getLng()))
                            .zoom(zoom)
                            .build());
                } else {
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(50.933916, 6.988506))
                            .zoom(zoom)
                            .build());
                }
            }
        });
    }

    /**
     * draws the marker, created in createMarker()  on the mapboxmap
     */
    public void drawMaker(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {


                ArrayList<MarkerViewOptions> marker;
                marker = createMarker();
                //System.out.println("Anzahl nodes: " + marker.size());
                for (int i = 0; i < marker.size(); i++) {

                    IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);

                    int connectedClients = allnodes.getNodes().get(i).getClients();
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), drawText(Integer.toString(connectedClients)));
                    roundedBitmapDrawable.setCircular(true);
                    roundedBitmapDrawable.setAntiAlias(true);
                    Icon icon = iconFactory.fromDrawable(roundedBitmapDrawable);
                    mapboxMap.addMarker(marker.get(i).icon(icon));
                }

                //   System.out.println("drawMarker finished");

            }
        });

    }

    /**
     * creates a Bitmap with the number of connected clients that is used as an icon for the Marker
     * @param text the text, that is shown in the marker
     * @return  Bitmap that is used as an icon for the marker
     */

    public  Bitmap drawText(String text) {

        final float scale = getResources().getDisplayMetrics().density;

        if (!markerScaled) {
            markerSize=markerSize*scale;
            markerScaled=true;
        }

        float textSize=(0.77f)*markerSize;
        float bitmapWidth=markerSize;

        float bitmapHeight=markerSize;
        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        //textPaint.setStrokeWidth(5f);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                (int)bitmapWidth, Layout.Alignment.ALIGN_CENTER,1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap((int)bitmapWidth, (int)bitmapHeight, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        c.drawPaint(paint);
        paint.setColor(Color.WHITE);
        c.drawCircle((markerSize / 2), (markerSize / 2), (markerSize / 2.0f) - (markerSize * 0.1f), paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.CENTER);


        c.drawText(text, 26 , 35, paint);
        c.save();

        return b;
    }

    /**
     * draws the Polylines created in createPolylines() on the mapboxmap
     */

    public void drawPolylines(){

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                ArrayList<PolylineOptions> lines;

                lines = createPolylines();
                //System.out.println("Anzahl linien: " + lines.size());
                for (int i = 0; i < lines.size(); i++) {
                    mapboxMap.addPolyline(lines.get(i));
                }
            }
        });
    }


    /**
     * Creates a list of polyline objects with data from the connection objects.
     * The first four Variables define the look of the lines as follows:
     * width: defines the width of the line
     * colorGood: defines the color of a connection with the mcs index of 7-9
     * colorMedium: defines the color of a connection with the mcs index of 4-6
     * colorBad: defines the color of a connection with the mcs index of 1-3
     *
     * @return A list with PolylineOptions to draw them on the MapboxMap
     */
    public ArrayList<PolylineOptions> createPolylines() {
        int width = 2;
        int colorGood = Color.GREEN;
        int colorMedium = Color.YELLOW;
        int colorBad = Color.RED;
        boolean LineAlreadyExists;

        ArrayList<Node> nodes = allnodes.getNodes();

        ArrayList<PolylineOptions> lines = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {

            for (int j = 0; j < nodes.get(i).getConnectedTo().size(); j++) {
                LineAlreadyExists=false;

                double lat1=nodes.get(i).getConnectedTo().get(j).getLat1();
                double lng1=nodes.get(i).getConnectedTo().get(j).getLng1();
                double lat2=nodes.get(i).getConnectedTo().get(j).getLat2();
                double lng2=nodes.get(i).getConnectedTo().get(j).getLng2();

                for (int k=0;k<lines.size();k++){

                    if (
                    lines.get(k).getPoints().get(0).getLatitude()==lat1&&
                    lines.get(k).getPoints().get(0).getLongitude()==lng1&&
                    lines.get(k).getPoints().get(1).getLatitude()==lat2&&
                    lines.get(k).getPoints().get(1).getLongitude()==lng2||

                    lines.get(k).getPoints().get(0).getLatitude()==lat2&&
                    lines.get(k).getPoints().get(0).getLongitude()==lng2&&
                    lines.get(k).getPoints().get(1).getLatitude()==lat1&&
                    lines.get(k).getPoints().get(1).getLongitude()==lng1
                            ) {
                        LineAlreadyExists=true;
                    }
  }
                PolylineOptions line = new PolylineOptions()
                        .add(new LatLng(lat1,
                                        lng1),
                                new LatLng(lat2,
                                        lng2))
                        .width(width);

                int mcs = nodes.get(i).getConnectedTo().get(j).getMcs();

                if (mcs <= 3) {
                    line.color(colorBad);
                } else if (mcs > 3 && mcs <= 6) {
                    line.color(colorMedium);
                } else if (mcs > 6) {
                    line.color(colorGood);
                } else if (mcs==-1) {
                    line.color(Color.BLACK);
                }


                if (!LineAlreadyExists) lines.add(line);
            }
        }
        System.out.println("createPolylines finished");

        return lines;
    }

    /**
     * Crates a List of MarkerViweOptions with the data from the node objects.
     *
     * @return List with MarkerviewOptions to draw them on the MapBoxMap
     */
    public ArrayList<MarkerViewOptions> createMarker() {

        ArrayList<Node> nodes = allnodes.getNodes();
        System.out.println("nodesize markerview method : " + nodes.size());

        ArrayList<MarkerViewOptions> marker = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            System.out.println("marker "+nodes.get(i).gethostname());

            marker.add(new MarkerViewOptions()
                    .position(new LatLng(nodes.get(i).getLat(), nodes.get(i).getLng()))
                    .title(nodes.get(i).gethostname())
                    .snippet("MAC: " + nodes.get(i).getMac() + "\nIPv4 Adresse: " + nodes.get(i).getIpv4())
                    .anchor((float) 0.5, (float) 0.5));
        }
        System.out.println("createMarker finished");
        return marker;
    }




    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        h.removeCallbacksAndMessages(null);
        if(toast != null) toast.cancel();
    }

    @Override
    public void onStop(){
        super.onStop();
        h.removeCallbacksAndMessages(null);
        if(toast != null) toast.cancel();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * method changes to the Table activity and is called by the Table-button on the bottom of the screen.
     *
     * @param v the current view
     */
    public void changeToTableActivity(View v) {

        Intent myIntent = new Intent(v.getContext(), TableActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //If set, and the activity being launched is already running in the current task, then instead of launching a new instance of that activity, all of the other activities on top of it will be closed and this Intent will be delivered to the (now on top) old activity as a new Intent.
        v.getContext().startActivity(myIntent);
        overridePendingTransition(0, 0);

    }








    /**
     * Method gets called, when the Async task in ApiConnection is finished.
     * processFinish() calls the methods that draw marker and lines on the map
     * @param output
     */
    @Override
    public void processFinish(Boolean output) {


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {


              //  System.out.println("load ist finished  " + allnodes.isFinished());

                mapboxMap.clear();

                drawPolylines();

                drawMaker();

                spinner.setVisibility(View.GONE);

                if (allnodes.getNodes().isEmpty()){
                    displayToast("json file empty");
                }

            }

        });
        requestCount--;

        map.setEnabled(true);

    }
}
