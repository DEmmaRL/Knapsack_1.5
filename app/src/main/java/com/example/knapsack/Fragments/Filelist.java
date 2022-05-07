package com.example.knapsack.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knapsack.MyAdapter;
import com.example.knapsack.R;
import com.example.knapsack.goku.nav_menu;
import com.example.knapsack.knapsack.FileListActivity;
import com.example.knapsack.knapsack.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Filelist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Filelist extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String path = "param1";
    private static String ARG_PARAM2 = "param2";
    private static String level = "level";
    private  static int nivel;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Filelist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Filelist.
     */
    // TODO: Rename and change types and number of parameters
    public static Filelist newInstance(String param1, String param2, int nivel) {
        Filelist fragment = new Filelist();
        Bundle args = new Bundle();
        args.putString(path, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(level, nivel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(path);
            mParam2 = getArguments().getString(ARG_PARAM2);
            nivel = getArguments().getInt(level);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_filelist,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        Button back= view.findViewById(R.id.back_button);
        TextView noFilesText = view.findViewById(R.id.nofiles_textview);
        FloatingActionButton fab=view.findViewById(R.id.fab);
        //       Toast.makeText(getActivity(), mParam2, Toast.LENGTH_SHORT).show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Action", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), FileListActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("targetLocation",mParam1);
                intent.putExtra("path",path);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file_father= new File(mParam2);
                if (nivel>0 && mParam2 != "superman" && mParam1 != "spiderman" && mParam2!="/storage"&& mParam1!="/storage/emulated/0" && mParam2!="/storage/emulated/0" && mParam2!="/storage/emulated" && file_father.getParent().toString()!="/storage") {
                    // Toast.makeText(getActivity(), "No, -"+ file_father.getParent().toString()+"- es diferente de "  , Toast.LENGTH_SHORT).show();
                    try {

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setReorderingAllowed(true);

                        // Replace whatever is in the fragment_container view with this fragment

                        File file = new File(mParam2);
                        transaction.replace(R.id.almacenamiento_vista, Filelist.newInstance(mParam2, file.getParent().toString(), nivel-1));
                        //getActivity().prueba("s");
                        // Commit the transaction
                        transaction.commit();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if(mParam1=="spiderman")
        {
            mParam1 = Environment.getExternalStorageDirectory().toString();

        }
        ((nav_menu) getActivity()).setActionBarTitle(mParam1);
        //Toast.makeText(getActivity(),mParam1,Toast.LENGTH_SHORT).show();
        File root = new File(mParam1);
        File[] filesAndFolders = root.listFiles();

        if(filesAndFolders==null || filesAndFolders.length ==0){
            noFilesText.setVisibility(View.VISIBLE);
            return view;
        }

        noFilesText.setVisibility(View.INVISIBLE);
        FragmentManager activity;
        activity= getActivity().getSupportFragmentManager();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(new MyAdapter(getActivity().getApplicationContext(),filesAndFolders, activity, nivel));
        return view;
    }
}