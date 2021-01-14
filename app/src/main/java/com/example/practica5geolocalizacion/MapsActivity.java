package com.example.practica5geolocalizacion;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    ActivityResultLauncher<String> requestPermissionLauncher;
    Button btn_ruta;
    Button btn_marcadores;
    LocationManager loc_manager;
    LatLng latLng;
    private static final float zoom = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>(){
            @Override
            public void onActivityResult(Boolean isGranted){
                if (isGranted) {
// Permission is granted. Continue the action or workflow in your
// app.

                }
            }
        });

        loc_manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btn_ruta = findViewById(R.id.btn_empezar_ruta);
        btn_marcadores = findViewById(R.id.btn_marcadores);

        btn_ruta.setOnClickListener(this::onClick);
        btn_marcadores.setOnClickListener(this::onClick);


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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==btn_ruta.getId())
        {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
// You can use the API that requires the permission.

                loc_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);

                mMap.setMyLocationEnabled(true);

            }else {
// You can directly ask for the permission.
// The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        else
            {
                onClickMarcadores();
            }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoom)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void onClickMarcadores() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loc_manager.removeUpdates(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loc_manager.removeUpdates(this);
    }
}