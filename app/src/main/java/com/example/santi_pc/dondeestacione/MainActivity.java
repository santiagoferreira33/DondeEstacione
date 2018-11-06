package com.example.santi_pc.dondeestacione;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.URIResolver;

public class MainActivity extends AppCompatActivity {

    Button btn_obtenerUbicacion, btn_guardarEstacionamiento, btn_tomarFoto;
    public double lat = 0.0;
    public double lng = 0.0;
    public String direccion = "";
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    Bitmap bmp;
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_obtenerUbicacion = (Button) findViewById(R.id.btn_obtenerUbicacion);
        btn_guardarEstacionamiento = (Button) findViewById(R.id.btn_guardarEstacionamiento);
        btn_tomarFoto = (Button) findViewById(R.id.btn_tomarFoto);

        btn_tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });


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

                    if(bmp!=null){
                        extras.putString("img", bitMapToString(bmp));
                    }else
                        extras.putString("img", null);

                    if(selectedImageUri!=null){
                        extras.putString("uri",selectedImageUri.toString());  ////  URI
                    }else
                        extras.putString("uri",null);

                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

    }


    //********************  FOTO  *****************************************************//

    public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] arr=baos.toByteArray();
        String result=Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }


    private void SelectImage(){

        final CharSequence[] items={"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera")){

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,REQUEST_CAMERA);
                } else if (items[i].equals("Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Select File"),SELECT_FILE);

                } else if(items[i].equals("Cancel")){

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== Activity.RESULT_OK){

            if(requestCode==REQUEST_CAMERA){

                Bundle bundle = data.getExtras();
                bmp = (Bitmap) bundle.get("data");
                selectedImageUri=null;
                //ivImage.setImageBitmap(bmp);
            } else if(requestCode==SELECT_FILE){
                selectedImageUri = data.getData();
                bmp=null;
                //ivImage.setImageURI(selectedImageUri);
            }
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}
