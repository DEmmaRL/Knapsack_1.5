package com.example.knapsack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.knapsack.db.DbContactos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class NuevoActivity extends AppCompatActivity {

    EditText txtDescription;
    Button btnGuardar;
    ImageButton Portada;
   // EditText description;
    String path;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Intent myIntent = getIntent(); // gets the previously created intent
       path = myIntent.getStringExtra("path"); // will return "FirstKeyValue"
       //String secondKeyName= myIntent.getStringExtra("secondKeyName"); // will ret
        setContentView(R.layout.activity_nuevo);
        txtDescription= findViewById(R.id.txtDescription);
        Portada=findViewById(R.id.imageport);
        btnGuardar=findViewById(R.id.btnGuardar);
     //   description=findViewById(R.id.description);
       //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbContactos dbContactos=new DbContactos(NuevoActivity.this);
               long id= dbContactos.insertarDescripcion(path, txtDescription.getText().toString());
                if(id>0)
                {
                  /*  try {
                        String aux = dbContactos.getDescription("g");

                      //  description.setText(aux);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(NuevoActivity.this, "ERROR " + e, Toast.LENGTH_SHORT).show();
                    }*/
                    Toast.makeText(NuevoActivity.this, "Registro hecho", Toast.LENGTH_SHORT).show();
                    limpiar();
                }
                else
                {
                    Toast.makeText(NuevoActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });
   }
   private void limpiar()
   {
       txtDescription.setText("");
   }

    public void onclick(View view) {
       cargarImagen();
    }

    private void cargarImagen() {
       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       intent.setType("image/");
       startActivityForResult(intent.createChooser(intent, "SELECCIONE APP"), 10);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Uri path =  data.getData();
            Portada.setImageURI(path);
            this.getExternalFilesDir("Knapsack");
            File f = new File(Environment.getExternalStorageDirectory(), "Knapsack_covers");
            if (!f.exists()) {
                f.mkdirs();
            }
            String archivo=this.path.toString();
            archivo = archivo.replace(".", "_");
            archivo = archivo.replace("/", "_");
           // Toast.makeText(this, "PATH  " + archivo, Toast.LENGTH_SHORT).show();
            //File sourceLocation = new File (path.getPath());
            //Toast.makeText(this, selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            // make sure your target location folder exists!

/*
            aqui hay que crear un string que sea el path del archivo, recuerda que aqui path es el archivo uri que
            es la imagen y this.path es la direccion del archivo, hay que crear el string que sea igual a la direccion
            del archivo pero cambiado el nombre de la carpeta knapsack por covers y las diagonales por guiones
*/
            String targetLocation="storage/emulated/0/Knapsack_covers";
           // File targetLocatio = new File (targetLocation+"/" + sourceLocation.getName());
           // Toast.makeText(this, targetLocation+"/" + sourceLocation.getName(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(context.getApplicationContext(),targetLocatio.getAbsolutePath(),Toast.LENGTH_SHORT).show();
            File sourceLocation = new File (path.getPath());
           // Toast.makeText(this,"MMMM "+ path.getPath(), Toast.LENGTH_SHORT).show();
            // make sure your target location folder exists!
            File targetLocatio = new File (targetLocation+"/" + archivo + ".jpg");



            String path_source=path.getPath(), path_destination=targetLocatio.getAbsolutePath();
            path_source = path_source.replace("/raw//", "");
            Toast.makeText(this, "WWWW "+ path_source, Toast.LENGTH_SHORT).show();
            File file_Source = new File(path_source);
            File file_Destination = new File(path_destination);

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(file_Source).getChannel();
                destination = new FileOutputStream(file_Destination).getChannel();

                long count = 0;
                long size = source.size();
                while((count += destination.transferFrom(source, count, size-count))<size);
                file_Source.delete();
                if(source != null) {
                    source.close();
                }
                if(destination != null) {
                    destination.close();
                }
            }
            catch (Exception e)
            {
                Toast.makeText(this, "ERROR " + e, Toast.LENGTH_SHORT).show();
            }
            file_Destination.renameTo(targetLocatio);

        }
    }


}



















