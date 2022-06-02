package com.example.knapsack;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        //FirebaseAuth mAuth;

        //mAuth = FirebaseAuth.getInstance();

        //FirebaseUser user = mAuth.getCurrentUser();

        //String UID = user.getUid();
        Toast.makeText(this, "USUARIO " , Toast.LENGTH_SHORT).show();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.nofiles_textview);

        String path = getIntent().getStringExtra("path");
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        if(filesAndFolders==null || filesAndFolders.length ==0){
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }

        noFilesText.setVisibility(View.INVISIBLE);
        FragmentManager activity;
        activity= this.getSupportFragmentManager();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // recyclerView.setAdapter(new MyAdapter(getApplicationContext(),filesAndFolders, activity));




    }
}