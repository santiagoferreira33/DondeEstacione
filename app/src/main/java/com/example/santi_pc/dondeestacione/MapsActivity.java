package com.example.santi_pc.dondeestacione;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity/*FragmentActivity*/ implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener  {

    public GoogleMap mMap;
    private Marker marcador;
    public double lat = 0.0;
    public double lng = 0.0;
    public String mensaje1;
    public String direccion = "";
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);

        btnGuardar = (Button)findViewById(R.id.btn_guardarUbicacion);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),direccion,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            if(!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locListener);//esta
            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
                lm.requestLocationUpdates(provider,3000,0, locListener); //esta
            }

            if(myLocation!=null){
                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                agregarMarcador(latLng);
                mMap.setOnMarkerDragListener(this);
                mMap.setOnMarkerClickListener(this);

            }
        }

    }


    private void locationStart(){
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled =mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled){
            Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingIntent);
        }
    }

    private void setLocation (LatLng latLng){
        if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
                if (!list.isEmpty()){
                    Address DirCalle = list.get(0);
                    direccion = (DirCalle.getAddressLine(0));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void agregarMarcador(LatLng latLng) {
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador))
                .draggable(true) //permite arrastrar marcador
        );
        mMap.animateCamera(miUbicacion);
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            agregarMarcador(latLng);
            setLocation(latLng);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            mensaje1 = ("gps Activado");
            Mensaje();
        }

        @Override
        public void onProviderDisabled(String provider) {
            mensaje1 = ("gps Desactivado");
            locationStart();
            Mensaje();
        }
    };

    private void  Mensaje(){
        Toast toast = Toast.makeText(this,mensaje1,Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       if(marker.equals(marcador)){
           LatLng latLng = marker.getPosition();
           setLocation(latLng);
           Toast.makeText(this, direccion,Toast.LENGTH_LONG).show();
       }
        return false;
    }
}
