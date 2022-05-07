package com.example.knapsack.knapsack;

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

import androidx.recyclerview.widget.RecyclerView;

import com.example.knapsack.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    File[] filesAndFolders;
    String targetLocation;
    public MyAdapter(Context context, File[] filesAndFolders, String targetLocation){
        this.context = context;
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
                            in = new FileInputStream(sourceLocation);
                            OutputStream out = null;
                            out = new FileOutputStream(targetLocation);

                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;

                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            in.close();
                            out.close();


                            Toast.makeText(context.getApplicationContext(), "Copy file successful.", Toast.LENGTH_SHORT).show();
                        }catch (Exception e)
                        {
                            Toast.makeText(context.getApplicationContext(),"Copy file failed",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context.getApplicationContext(),"Copy file failed. Source file missing.",Toast.LENGTH_SHORT).show();
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
