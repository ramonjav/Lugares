package com.example.lugares.maps;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lugares.Activitys.ModificarActivity;
import com.example.lugares.Activitys.NewActivity;
import com.example.lugares.R;
import com.example.lugares.utilidades.Constantes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocalizacionActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    String tipo;
    double latitud = 0, longitud = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        tipo = extras.getString(Constantes.TIPO);
        if(tipo.equals(Constantes.MODIFICAR)){
            longitud = extras.getDouble(Constantes.LONGITUD);
            latitud = extras.getDouble(Constantes.LATITUD);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        map.setOnMapClickListener(this);
        map.setOnInfoWindowClickListener(this);

        if(longitud!=0 && latitud!=0){
            LatLng place = new LatLng(latitud, longitud);
           addMarker(place);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addMarker(latLng);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onMapClick(LatLng latLng) {
        addMarker(latLng);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker != null){
            LatLng newPlace = marker.getPosition();
            Intent i;
            if(tipo.equals(Constantes.MODIFICAR)){
                i = new Intent(LocalizacionActivity.this, ModificarActivity.class);
            }else{
                i = new Intent(LocalizacionActivity.this, NewActivity.class);
            }
            i = new Intent(LocalizacionActivity.this, NewActivity.class);
            i.putExtra(Constantes.LONGITUD, newPlace.longitude);
            i.putExtra(Constantes.LATITUD, newPlace.latitude);
            i.putExtra(Constantes.UBUCACION_SELECCIONADA, true);
            setResult(RESULT_OK, i);
            Toast.makeText(LocalizacionActivity.this, "Ubicación seleccionada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void addMarker(LatLng latLng){

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Nueva localización")
                .snippet("Añadir esta localización")
                .draggable(true)
                .flat(true)
        );
    }
}