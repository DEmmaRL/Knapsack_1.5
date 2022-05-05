package com.example.knapsack;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knapsack.Fragments.Filelist;
import com.example.knapsack.Fragments.FragmentAlmacenamiento;

import java.io.File;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    FragmentManager contexto;
    Context context;
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
