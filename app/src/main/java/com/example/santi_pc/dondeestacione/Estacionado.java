package com.example.santi_pc.dondeestacione;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Estacionado extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap mMap;
    MapView mapView;
    TextView tv_nota, tv_direccion;
    private Marker marcador;
    public double lat = 0.0;
    public double lng = 0.0;
    public String direccion = "";
    public String nota;
    Uri selectedImageUri;
    ImageView iv_estacionado;
    Bitmap bmp;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estacionado);

        mapView = (MapView) findViewById(R.id.mv_Estacionado);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        tv_nota= (TextView) findViewById(R.id.tv_nota);
        tv_direccion= (TextView) findViewById(R.id.tv_direccion);
        iv_estacionado = (ImageView) findViewById(R.id.iv_estacionado);  //////////

        Bundle extras = this.getIntent().getExtras();
        if(extras != null){
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
            direccion = extras.getString("direccion");
            tv_nota.setText(extras.getString("nota"));
            String uriString = extras.getString("uri");
            if(uriString!= null)
                selectedImageUri = Uri.parse(uriString);   //////////
            bmp = StringToBitMap(extras.getString("img"));

        }
        tv_direccion.setText(direccion);
        if(selectedImageUri!=null)
            iv_estacionado.setImageURI(selectedImageUri);
        if (bmp != null)
            iv_estacionado.setImageBitmap(bmp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Flecha para volver atras
    }

    @Override
    public boolean onSupportNavigateUp() {  //Flecha para volver atras
        onBackPressed();
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Estacionado.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            if(!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(lat, lng);
            agregarMarcador(latLng);

        }

    }

    private void agregarMarcador(LatLng latLng) {
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(direccion)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador))
        );
        mMap.animateCamera(miUbicacion);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    ////Camara
    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
