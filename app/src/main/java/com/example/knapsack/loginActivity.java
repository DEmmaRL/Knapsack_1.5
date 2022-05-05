package com.example.knapsack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.knapsack.activity.InicioActivity;
import com.example.knapsack.goku.nav_menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginContra;
    private ProgressBar progressBar;
    private FirebaseAuth authPerfil;
    private static final  String TAG = "loginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.edtxtingemail);
        editTextLoginContra = findViewById(R.id.edtxtingcontra);
        progressBar = findViewById(R.id.progressbarlogin);

        authPerfil = FirebaseAuth.getInstance();

        Button btnLogin = findViewById(R.id.btningresar);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtEmail = editTextLoginEmail.getText().toString();
                String txtContra = editTextLoginContra.getText().toString();

                if(TextUtils.isEmpty(txtEmail)){
                    Toast.makeText(loginActivity.this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Se requiere un correo electrónico");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
                    Toast.makeText(loginActivity.this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Se requiere un correo electrónico");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(txtContra)){
                    Toast.makeText(loginActivity.this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show();
                    editTextLoginContra.setError("Se requiere un correo electrónico válido");
                    editTextLoginContra.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    ingresarUsuario(txtEmail, txtContra);
                }
            }
        });
    }

    private void ingresarUsuario(String email, String contra) {
        authPerfil.signInWithEmailAndPassword(email, contra).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = authPerfil.getCurrentUser();
                    Toast.makeText(loginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(loginActivity.this, nav_menu.class));
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("El usuario no existe o es inválido. Inténtalo de nuevo.");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("El correo electrónico o contraseña es incorrecto. Inténtalo de nuevo.");
                        editTextLoginEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(loginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(loginActivity.this, "Inicio de sesión fallido",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    protected void onStart() {
        super.onStart();

        if (authPerfil.getCurrentUser() != null){
            Toast.makeText(loginActivity.this, "Ya has iniciado sesión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(loginActivity.this, nav_menu.class));
            finish();
        } else {
            Toast.makeText(loginActivity.this, "Ya puedes iniciar sesión", Toast.LENGTH_SHORT).show();
        }
    }
}