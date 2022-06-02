package com.example.knapsack.Entidades;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.knapsack.R;
import com.example.knapsack.goku.nav_menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class loginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginContra;
    private ProgressBar progressBar;
    private FirebaseAuth authPerfil;
    private static final String TAG = "loginActivity";
    private StorageReference mStorageRef;
    int i;
    boolean aux = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        editTextLoginEmail = findViewById(R.id.edtxtingemail);
        editTextLoginContra = findViewById(R.id.edtxtingcontra);
        progressBar = findViewById(R.id.progressbarlogin);

        authPerfil = FirebaseAuth.getInstance();

        String a="texto/no(", b=").txt";
        mStorageRef.child("texto/no.txt").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Toast.makeText(loginActivity.this, "EXISTEEEEE", Toast.LENGTH_SHORT).show();

                i=1;
                aux=false;
                    while (!aux) {

                        if(uploadfile(i, a, b))
                        {
                            aux=true;
                        }

                        i++;

                    }
                Toast.makeText(loginActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                        try {
                            Toast.makeText(loginActivity.this, "texto/no" + "(" + i + ")" + ".txt", Toast.LENGTH_SHORT).show();
                            Toast.makeText(loginActivity.this, "no existe" + i, Toast.LENGTH_SHORT).show();
                            aux = true;
                            Toast.makeText(loginActivity.this, "NO EXISTEEEEE", Toast.LENGTH_SHORT).show();
                            StorageReference referencia = mStorageRef.child("texto/no" + "(" + i + ")" + ".txt");
                            InputStream archivo = getResources().openRawResource(R.raw.storage);
                            referencia.putStream(archivo)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get a URL to the uploaded content
                                            Toast.makeText(loginActivity.this, "SUBIDO", Toast.LENGTH_SHORT).show();
                                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                            aux = true;


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            // ...
                                            Toast.makeText(loginActivity.this, "ERROR ffff", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            Toast.makeText(loginActivity.this, "Algo falló", Toast.LENGTH_SHORT).show();
                        }
                        i++;
                        if (aux) {
                            i=0;
                        }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(loginActivity.this, "NO EXISTEEEEE", Toast.LENGTH_SHORT).show();
                final  StorageReference referencia = mStorageRef.child("texto/"+ "no"+"." + "txt");
                InputStream archivo = getResources().openRawResource(R.raw.storage);
                referencia.putStream(archivo)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Toast.makeText(loginActivity.this, "SUBIDO", Toast.LENGTH_SHORT).show();
                                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(loginActivity.this, "ERROR ffff", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });




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

    private boolean uploadfile(int i, String a, String b) {

        return false;
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