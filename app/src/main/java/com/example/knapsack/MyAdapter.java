package com.example.knapsack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knapsack.Fragments.Filelist;
import com.example.knapsack.db.DbContactos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    FragmentManager contexto;
    Context context;
    private StorageReference mStorageRef;
    private FirebaseAuth authPerfil;
    File[] filesAndFolders;
    private int nivel;


    public MyAdapter(Context context, File[] filesAndFolders, FragmentManager contexto, int nivel){
        this.context = context;
        this.contexto=contexto;
        this.filesAndFolders = filesAndFolders;
        this.nivel = nivel;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        String UID = user.getUid();
        File selectedFile = filesAndFolders[position];
        View f= holder.itemView;
        TextView F=(TextView) f.findViewById(R.id.idinfo);
        ImageView icon = f.findViewById(R.id.icon_view);
        //icon.setImageResource(R.drawable.lain_test);
        //F.setText("Descripción");
        holder.textView.setText(selectedFile.getName());
      //  holder.imageView.setImageResource(R.drawable.lain_test);
        DbContactos dbContactos=new DbContactos(context);
        try {
            String aux = dbContactos.getDescription(selectedFile.getAbsolutePath());
            if (aux != null){
                F.setText(aux);
             }
            else
            {
                F.setText("____");
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, "ERROR " + e, Toast.LENGTH_SHORT).show();
        }
        //long id= dbContactos.insertarDescripcion(txtPath.getText().toString(), txtDescription.getText().toString());
        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            String cover= selectedFile.getAbsolutePath();
            cover = cover.replace(".", "_");
            cover = cover.replace("/", "_");
            File imagen= new File("storage/emulated/0/Knapsack_covers"+ "/" + cover+".jpg");
            if(imagen.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imagen.getAbsolutePath());


                holder.imageView.setImageBitmap(myBitmap);

            }
            else
            {
                holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selectedFile.isDirectory()){
                    String path = selectedFile.getAbsolutePath();
                    FragmentManager fragmentManager = contexto;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setReorderingAllowed(true);

                    // Replace whatever is in the fragment_container view with this fragment
                    File file= new File(path);
                    String res= file.getParentFile().getName();
                    // Toast.makeText(context, file.getParent().toString(), Toast.LENGTH_SHORT).show();
                    transaction.replace(R.id.almacenamiento_vista, Filelist.newInstance(path, file.getParent().toString(), nivel+1));
                    // Commit the transaction
                    transaction.commit();
                    /*Intent intent = new Intent(context, FileListActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path",path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);*/
                }else{
                    //open thte file
                    try {
                        File file = new File(selectedFile.getAbsolutePath());
                        Toast.makeText(context, selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        Uri uri =  FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),BuildConfig.APPLICATION_ID + ".provider",file);
                        String mime = context.getContentResolver().getType(uri);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, mime);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(context.getApplicationContext(),"Cannot open the file",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenu().add("DELETE");
                if(!selectedFile.isDirectory()) {
                    popupMenu.getMenu().add("GUARDAR EN LA NUBE");
                }
                popupMenu.getMenu().add("RENAME");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("DELETE")){
                            boolean deleted = selectedFile.delete();
                            if(deleted){
                                Toast.makeText(context.getApplicationContext(),"DELETED ",Toast.LENGTH_SHORT).show();

                                v.setVisibility(View.GONE);
                            }
                        }
                        if(item.getTitle().equals("GUARDAR EN LA NUBE")) {
                            if(isOnlineNet()) {
                                authPerfil = FirebaseAuth.getInstance();
                                mStorageRef = FirebaseStorage.getInstance().getReference();
                                long add = System.currentTimeMillis();
                                try {
                                    //ARCHIVO
                                    File file = new File(selectedFile.getAbsolutePath());
                                    String temp = selectedFile.getName();
                                    Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file);
                                    String mime = context.getContentResolver().getType(uri);
                                    temp = temp.replace(".", "_");
                                    //String extension = FilenameUtils.getExtension("/path/to/file/mytext.txt");
                                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                    final StorageReference referencia = mStorageRef.child(authPerfil.getUid() + "/" + "files/" + temp + "_" + add + extension);
                                    InputStream archivo = null;
                                    try {
                                        archivo = new FileInputStream(new File(selectedFile.getAbsolutePath()));
                                    } catch (FileNotFoundException e) {
                                        Toast.makeText(context, "ERROR: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                    //InputStream archivo = getResources().openRawResource(R.raw.storage);
                                    referencia.putStream(archivo)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    // Get a URL to the uploaded content
                                                    Toast.makeText(context, "SUBIDO", Toast.LENGTH_SHORT).show();
                                                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle unsuccessful uploads
                                                    // ...
                                                    Toast.makeText(context, "ERROR ffff", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    Toast.makeText(context, "Archivo guardado", Toast.LENGTH_SHORT).show();
                                    // Toast.makeText(context.getApplicationContext(),"MOVED ",Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                                }
                                //PORTADA
                                try {
                                    String cover = selectedFile.getAbsolutePath();
                                    cover = cover.replace(".", "_");
                                    cover = cover.replace("/", "_");
                                    File imagen = new File("storage/emulated/0/Knapsack_covers" + "/" + cover + ".jpg");

                                    if (imagen.exists()) {
                                        File file = new File(selectedFile.getAbsolutePath());
                                        String temp = selectedFile.getName();
                                        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file);
                                        String mime = context.getContentResolver().getType(uri);
                                        temp = temp.replace(".", "_");
                                        //String extension = FilenameUtils.getExtension("/path/to/file/mytext.txt");
                                        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                        //  Bitmap myBitmap = BitmapFactory.decodeFile(imagen.getAbsolutePath());
                                        final StorageReference referencia = mStorageRef.child(authPerfil.getUid() + "/" + "covers/" + temp + "_" + add + extension);
                                        InputStream archivo = null;

                                        try {
                                            archivo = new FileInputStream(new File("storage/emulated/0/Knapsack_covers" + "/" + cover + ".jpg"));
                                        } catch (FileNotFoundException e) {
                                            Toast.makeText(context, "ERROR: " + e, Toast.LENGTH_SHORT).show();
                                        }
                                        //InputStream archivo = getResources().openRawResource(R.raw.storage);
                                        referencia.putStream(archivo)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // Get a URL to the uploaded content
                                                        Toast.makeText(context, "SUBIDO", Toast.LENGTH_SHORT).show();
                                                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                        // ...
                                                        Toast.makeText(context, "ERROR ffff", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        //    holder.imageView.setImageBitmap(myBitmap);

                                    } else {
                                        @SuppressLint("ResourceType") InputStream archivo = context.getResources().openRawResource(R.drawable.file_icon);
                                        File file = new File(selectedFile.getAbsolutePath());
                                        String temp = selectedFile.getName();
                                        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file);
                                        String mime = context.getContentResolver().getType(uri);
                                        temp = temp.replace(".", "_");
                                        //String extension = FilenameUtils.getExtension("/path/to/file/mytext.txt");
                                        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                        //  Toast.makeText(context, "mime es "+ extension, Toast.LENGTH_SHORT).show();
                                        final StorageReference referencia = mStorageRef.child(authPerfil.getUid() + "/" + "covers/" + temp + "_" + add + extension);
                                        referencia.putStream(archivo)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // Get a URL to the uploaded content
                                                        //     Toast.makeText(context, "SUBIDO", Toast.LENGTH_SHORT).show();
                                                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                        // ...
                                                        Toast.makeText(context, "ERROR ffff", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                    //Descripción
                                    try {

                                        File root = new File(Environment.getExternalStorageDirectory(), "Knapsack_notes");
                                        if (!root.exists()) {
                                            root.mkdirs();
                                        }
                                        DbContactos dbContactos = new DbContactos(context);
                                        String sBody = "";
                                        try {
                                            String aux = dbContactos.getDescription(selectedFile.getAbsolutePath());
                                            if (aux != null) {
                                                sBody = aux;
                                            }
                                        } catch (Exception e) {
                                        }
                                        String temp = selectedFile.getName();
                                        temp = temp.replace(".", "_");
                                        //Toast.makeText(context, temp, Toast.LENGTH_SHORT).show();
                                        File gpxfile = new File(root, "aaaa" + "." + "txt");
                                        FileWriter writer = new FileWriter(gpxfile);
                                        writer.append(sBody);
                                        writer.flush();
                                        writer.close();
                                        //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                        String extension = gpxfile.getAbsolutePath().substring(gpxfile.getAbsolutePath().lastIndexOf("."));
                                        final StorageReference referencia = mStorageRef.child(authPerfil.getUid() + "/" + "notes/" + temp + "_" + add + extension);
                                        InputStream archivo = null;
                                        try {
                                            archivo = new FileInputStream(gpxfile);
                                        } catch (FileNotFoundException e) {
                                            Toast.makeText(context, "ERROR: " + e, Toast.LENGTH_SHORT).show();
                                        }
                                        //InputStream archivo = getResources().openRawResource(R.raw.storage);
                                        referencia.putStream(archivo)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        // Get a URL to the uploaded content
                                                        //    Toast.makeText(context, "SUBIDO", Toast.LENGTH_SHORT).show();
                                                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle unsuccessful uploads
                                                        // ...
                                                        Toast.makeText(context, "ERROR ffff", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, "ERROR AL SUBIR PORTADA", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(context, "NO  HAY CONEXIÓN A INTERNET", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(item.getTitle().equals("RENAME")){
                            Toast.makeText(context.getApplicationContext(),"RENAME ",Toast.LENGTH_SHORT).show();

                        }
                        return true;
                    }
                });

                popupMenu.show();
                return true;
            }
        });


    }


    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
        }
    }
}
