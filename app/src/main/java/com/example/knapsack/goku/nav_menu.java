package com.example.knapsack.goku;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.knapsack.Entidades.Persona;
import com.example.knapsack.Fragments.DetallePersonaFragment;
import com.example.knapsack.Fragments.FragmentAlmacenamiento;
import com.example.knapsack.Fragments.FragmentPersonas;
import com.example.knapsack.Fragments.PersonasFragment;
import com.example.knapsack.R;
import com.example.knapsack.iComunicaFragments;
import com.google.android.material.navigation.NavigationView;

public class nav_menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, iComunicaFragments {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    String s;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    //variable del fragment detalle
    DetallePersonaFragment detallePersonaFragment;
    //DetallePersonaFragment detallePersonaFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        //lo sgt se implementa luego de haber implementado NavigationView.OnNavigationItemSelectedListener
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        //cargar fragment principal en la actividad
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment,new FragmentAlmacenamiento());
        fragmentTransaction.commit();
    }
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Estás seguro de querer cerrar Knapsack?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent =new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }
*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //para cerrar automaticamente el menu
        drawerLayout.closeDrawer(GravityCompat.START);
        if(menuItem.getItemId() == R.id.personas){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new PersonasFragment());
            fragmentTransaction.commit();
        }
        if(menuItem.getItemId() == R.id.account){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new FragmentPersonas());
            fragmentTransaction.commit();
        }
        if(menuItem.getItemId() == R.id.almacenamiento){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new FragmentAlmacenamiento());
            fragmentTransaction.commit();
        }
        return false;
    }

    @Override
    public void enviarPersona(Persona persona) {
        detallePersonaFragment = new DetallePersonaFragment();
        Bundle bundleEnvio = new Bundle();
        //se manda el objeto que le esta llegando:
        bundleEnvio.putSerializable("objeto",persona);
        detallePersonaFragment.setArguments(bundleEnvio);

        //CArgar fragment en el activity
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, detallePersonaFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    
}