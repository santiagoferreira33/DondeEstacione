package com.example.santi_pc.dondeestacione;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_obtenerUbicacion, btn_guardarEstacionamiento;
    public double lat = 0.0;
    public double lng = 0.0;
    public String direccion = "";
    //public String nota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_obtenerUbicacion = (Button) findViewById(R.id.btn_obtenerUbicacion);
        btn_guardarEstacionamiento = (Button) findViewById(R.id.btn_guardarEstacionamiento);



        btn_obtenerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });


        Bundle extras = this.getIntent().getExtras();
        if(extras != null){
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
            direccion = extras.getString("direccion");
            //Toast.makeText(this,lat+" ,"+lng+" "+direccion,Toast.LENGTH_LONG).show();
        }

        btn_guardarEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nota = (EditText)findViewById(R.id.et_nota);
                if (lat==0.0){
                    Toast.makeText(getApplicationContext(),"obtenga una ubicacion",Toast.LENGTH_LONG).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), Estacionado.class);
                    Bundle extras = new Bundle();
                    extras.putDouble("lat", lat);
                    extras.putDouble("lng", lng);
                    extras.putString("direccion", direccion);
                    extras.putString("nota", nota.getText().toString());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

    }
}
