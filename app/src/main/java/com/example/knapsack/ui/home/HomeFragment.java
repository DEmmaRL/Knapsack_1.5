package com.example.knapsack.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.knapsack.FileListActivity;
import com.example.knapsack.R;
import com.example.knapsack.databinding.FragmentHomeBinding;
import com.example.knapsack.perfilActivity;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    TextView hola;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MaterialButton storageBtn = root.findViewById(R.id.storage_btn);
        hola= root.findViewById(R.id.texto);
        final TextView textView = binding.textHome;

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }

        }
        );
        storageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hola.setText("DEBUG");

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}