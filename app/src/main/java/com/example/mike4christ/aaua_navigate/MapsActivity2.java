package com.example.mike4christ.aaua_navigate;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class MapsActivity2 extends FragmentActivity  implements OnStreetViewPanoramaReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.mappanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

       streetViewPanorama.setPosition(new LatLng(37.869260, -122.254811));



        streetViewPanorama.setStreetNamesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);


        long duration = 1000;
        StreetViewPanoramaCamera camera =
                new StreetViewPanoramaCamera.Builder()
                        .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                        .tilt(streetViewPanorama.getPanoramaCamera().tilt)
                        .bearing(streetViewPanorama.getPanoramaCamera().bearing - 60)
                        .build();
        streetViewPanorama.animateTo(camera, duration);

    }
}
