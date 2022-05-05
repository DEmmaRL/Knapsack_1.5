package com.example.knapsack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;


public class perfilActivity extends AppCompatActivity {
    TextView hola;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        MaterialButton storageBtn = findViewById(R.id.storage_btn);
        hola= findViewById(R.id.texto);
        storageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        hola.setText("DEBUG");
                if(checkPermission()){
                    hola.setText("Permiso permitido");
                    //permission allowed
                   Intent intent = new Intent(perfilActivity.this, FileListActivity.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path",path);
                    startActivity(intent);
                }else{
                    hola.setText("Permiso no permitido");
                    //permission not allowed
                    requestPermission();

                }
            }
        });

    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(perfilActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(perfilActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(perfilActivity.this,"Storage permission is requires,please allow from settings",Toast.LENGTH_SHORT).show();
        }else
            ActivityCompat.requestPermissions(perfilActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
    }
}