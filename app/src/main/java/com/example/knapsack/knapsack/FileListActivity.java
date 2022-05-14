package com.example.knapsack.knapsack;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knapsack.R;

import java.io.File;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.nofiles_textview);
        String path = getIntent().getStringExtra("path");
        String targetLocation = getIntent().getStringExtra("targetLocation");
        Toast.makeText(FileListActivity.this, targetLocation, Toast.LENGTH_SHORT).show();
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        if(filesAndFolders==null || filesAndFolders.length ==0){
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }

        noFilesText.setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FragmentManager activity;
        activity= FileListActivity.this.getSupportFragmentManager();
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),filesAndFolders,activity,  targetLocation));




    }
}