package com.example.mike4christ.aaua_navigate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends AppCompatActivity implements RoutingListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String API_URL = "http://aauanav.000webhostapp.com/testing-file.php";
    private static final String LOG_TAG = "MyActivity";
    private static final int[] COLORS = new int[]{R.color.colorPurple, R.color.colorYellow, R.color.colorAccent};
    /*private static final LatLngBounds AAUA = new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));*/
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Satelite", "Terrain", "Hybrid", "Normal"};
    public Model obj;
    public LatLng destination_location;
    protected GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker, mMaker;
    @BindView(R.id.destination)
    Spinner destination;
    @BindView(R.id.send)
    FloatingActionButton send;
    @BindView(R.id.map_type)
    ImageButton map_type;

    private TextView jtest;
    LocationRequest mLocationRequest;
    ArrayList<String> worldlist;
    ArrayList<Model> world;
    JSONArray jsonarray;
    JSONArray data;
    JSONObject object;
    LatLng latLng;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        ButterKnife.bind(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        jtest=findViewById(R.id.test_json);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        }
        polylines = new ArrayList<>();

        mapFragment.getMapAsync(this);


        map_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showMapTypeSelectorDialog();
            }
        });


    }

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType();

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 0:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

        progressDialog.dismiss();

        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(7 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

     /*   // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(destination_location);
        mMap.addMarker(options);*/


    }

    @Override
    public void onRoutingCancelled() {
        progressDialog.dismiss();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }

    @Override
    public void onBackPressed() {


        ExitAlert();

    }

    public void ExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Exit Application ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        MapsActivity.this.finish();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alert = builder.create();
        alert.setTitle("Exit Alert !!");
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {

            if (Util.Operations.isOnline(this)) {
            try{
                progressDialog = ProgressDialog.show(this, "Please wait.",
                        "Fetching Map", true);
                progressDialog.setMax(100);
                progressDialog.setCanceledOnTouchOutside(true);

                if (progressDialog.getProgress()==100){
                   Alert();
                }
                class FetchApi extends AsyncTask<Void, Void, Void> {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    protected Void doInBackground(Void... params) {
                        world = new ArrayList<Model>();
                        worldlist = new ArrayList<String>();

                        //fetch json object
                        JSONObject data=JSONConnect.getJSONfromURL(API_URL);
                        try {

                            JSONArray jArray = data.getJSONArray("data");
                            for (int i = 0; i < jArray.length(); i++) {
                                try {
                                    object = jArray.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Model worldpop = new Model();

                                worldpop.setPlace(object.optString("places"));
                                worldpop.setmy_lng(object.optDouble("longitude"));
                                worldpop.setmy_lat(object.optDouble("latitude"));

                                world.add(worldpop);

                                // Populate spinner with country names
                                worldlist.add(object.optString("places"));

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            Alert();
                        }catch (NullPointerException n){
                            Looper.prepare();
                            Alert();
                            Toast.makeText(MapsActivity.this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
                        }
                        return  null;
                    }

                    @Override
                    protected void onPostExecute(Void args) {

                        progressDialog.dismiss();


                        destination
                                .setAdapter(new ArrayAdapter<String>(MapsActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        worldlist));


                        destination
                                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0,
                                                               View arg1, int position, long arg3) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(MapsActivity.this, world.get(position).getPlace(), Toast.LENGTH_SHORT).show();
                                        if (mMaker != null) {
                                            mMaker.remove();
                                        }
                                        destination_location = new LatLng(world.get(position).getmy_lat(), world.get(position).getmy_lng());


                                        MarkerOptions option = new MarkerOptions()
                                                .title(world.get(position).getPlace())
                                                .position(destination_location)
                                                .snippet(world.get(position).getPlace());

                                        mMaker = mMap.addMarker(option);


                                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                                .target(destination_location) // Sets the center of the map to Mountain View
                                                .zoom(15)                   // Sets the zoom
                                                .bearing(30)                // Sets the orientation of the camera to east
                                                .tilt(45)   // Sets the tilt of the camera to 30 degrees
                                                .build();                   // Creates a CameraPosition from the builder
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                        // CameraUpdate update = CameraUpdateFactory.newLatLngZoom(destination_location, 17);

                                        //  mMap.animateCamera(update, 2000, null);


                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> arg0) {
                                        //remove marker
                                        mMaker.remove();
                                    }
                                });


                    }

                }
                try {
                    new FetchApi().execute();
                }catch (NullPointerException e){
                    Alert();
                }

                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);

                }

                mMap.setTrafficEnabled(true);
                mMap.setIndoorEnabled(true);
                mMap.setBuildingsEnabled(true);
            }catch (RuntimeException e){
                e.printStackTrace();
                Alert();
            }

            } else {
                Alert();
            }
        }catch (Exception e){
            e.printStackTrace();
            Alert();
        }
    }

    public void Alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Internet Connection Error")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        MapsActivity.this.finish();
                        moveTaskToBack(true);
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle("Alert !!");
        alert.show();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //stop location updates


    }


    @OnClick(R.id.send)
    public void sendRequest() {
        try{
            if (Util.Operations.isOnline(this)) {
                route();
            }
        }catch (Exception e){
            AlertDialog.Builder build = new AlertDialog.Builder(MapsActivity.this);
            build.setMessage("Check your Internet")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MapsActivity.this.finish();
                            finish();
                        }
                    });
            AlertDialog alertDialog = build.create();
            alertDialog.setTitle("Internet Connection Exception");
            alertDialog.show();
        }

    }

    public void route() {
        try {
            progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Fetching route information.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(latLng, destination_location)
                    .build();
            routing.execute();
        }catch (Exception n){
            Looper.prepare();
            Alert();
            Toast.makeText(MapsActivity.this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        String error=connectionResult.getErrorMessage();
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent intent = new Intent(MapsActivity.this, About.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static  String reading(String filepath){
        String content=" ";
        try{
            content=new String(Files.readAllBytes(Paths.get(filepath)));

        }catch(IOException e){
            e.printStackTrace();
        }
        return content;
    }
    }





