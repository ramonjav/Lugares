package com.example.lugares.maps;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

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

public class SelecMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selec_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        // Add a marker in Sydney and move the camera
        /*LatLng place = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    public void onMapClick(LatLng latLng) {

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Nueva localización")
                .snippet("Añadir esta localización")
                .draggable(true)
                .flat(true)
        );
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    if(marker != null){
        LatLng newPlace = marker.getPosition();

        Intent i = new Intent(SelecMapActivity.this, NewActivity.class);
        i.putExtra(Constantes.LONGITUD, newPlace.longitude);
        i.putExtra(Constantes.LATITUD, newPlace.latitude);
        setResult(RESULT_OK, i);
        Toast.makeText(SelecMapActivity.this, "Ubicación seleccionada", Toast.LENGTH_SHORT).show();
        finish();
    }
    }
}
