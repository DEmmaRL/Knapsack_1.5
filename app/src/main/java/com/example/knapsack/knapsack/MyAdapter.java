package com.example.knapsack.knapsack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knapsack.Fragments.Filelist;
import com.example.knapsack.NuevoActivity;
import com.example.knapsack.R;
import com.example.knapsack.goku.nav_menu;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    FragmentManager contexto;
    File[] filesAndFolders;
    String targetLocation;
    private StorageReference mStorageRef;
    public MyAdapter(Context context, File[] filesAndFolders, FragmentManager contexto, String targetLocation){
        this.context = context;
        this.contexto = contexto;
        this.filesAndFolders = filesAndFolders;
        this.targetLocation=targetLocation;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_add_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFile.isDirectory()){
                    Intent intent = new Intent(context, FileListActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path",path);
                    intent.putExtra("targetLocation",targetLocation);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    File sourceLocation = new File (selectedFile.getAbsolutePath());
                    Toast.makeText(context, selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    // make sure your target location folder exists!
                    File targetLocatio = new File (targetLocation+"/" + selectedFile.getName());
                    //Toast.makeText(context.getApplicationContext(),targetLocatio.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    if(!targetLocatio.exists())
                    {
                        try {
                            targetLocatio.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    if(targetLocatio.exists())
                    {
                    //    Toast.makeText(context.getApplicationContext(),"EXISTE",Toast.LENGTH_SHORT).show();
                    }
                    if(sourceLocation.exists()){

                        InputStream in = null;
                        try {
                            sourceLocation.renameTo(targetLocatio);
                         //   Toast.makeText(context.getApplicationContext(), "Copy file successful.", Toast.LENGTH_SHORT).show();
                        }catch (Exception e)
                        {
                          Toast.makeText(context.getApplicationContext(),"Copy file failed",Toast.LENGTH_SHORT).show();
                        }
                        try {
                            Intent intent = new Intent(v.getContext(), NuevoActivity.class);
                            intent.putExtra("path", targetLocatio.getAbsolutePath());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(context, "ERROR " +e, Toast.LENGTH_SHORT).show();
                        }
                        }
                    //Intent intent = new Intent(context, nav_menu.class);
                    //String path = Environment.getExternalStorageDirectory().getPath();
                    //context.startActivity(intent);
                    //AppCompatActivity activity = (AppCompatActivity) v.getContext();
                  //  Activity myFragment = new nav_menu();
                    //activity.getSupportFragmentManager().beginTransaction().replace(R.id.add_file_list, myFragment).addToBackStack(null).commit();

                    /*
                    Intent intent = new Intent(context, Filelist.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    context.startActivity(intent);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Fragment myFragment = new Filelist();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.add_file_list, myFragment).addToBackStack(null).commit();
*/
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mStorageRef = FirebaseStorage.getInstance().getReference();
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenu().add("DELETE");
                popupMenu.getMenu().add("MOVE");
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
                        if(item.getTitle().equals("MOVE")){
                            Toast.makeText(context.getApplicationContext(),"MOVED ",Toast.LENGTH_SHORT).show();

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
