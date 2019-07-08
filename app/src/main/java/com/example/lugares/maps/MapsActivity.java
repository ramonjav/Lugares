package com.example.lugares.maps;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lugares.Activitys.MainActivity;
import com.example.lugares.R;
import com.example.lugares.utilidades.Constantes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    double longitud;
    double latitud;
    String nombre;
    String descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extra = getIntent().getExtras();
        longitud = extra.getDouble(Constantes.LONGITUD);
        latitud = extra.getDouble(Constantes.LATITUD);
        nombre = extra.getString(Constantes.NOMBRE);
        descripcion = extra.getString(Constantes.DESCRIPCION);



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

        mMap.setOnInfoWindowClickListener(this);

        LatLng place = new LatLng(latitud, longitud);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(place)
                .title(nombre)
                .snippet(descripcion)
                .draggable(true)
                .flat(true)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Toast.makeText(MapsActivity.this, place.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(MapsActivity.this, CalleActivity.class);
        i.putExtra(Constantes.LONGITUD, longitud);
        i.putExtra(Constantes.LATITUD, latitud);
        startActivity(i);
    }

}
