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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registroActivity extends AppCompatActivity {
    private EditText editTextNombre, editTextEmail, editTextFecha, editTextContra, editTextConf;
    private ProgressBar progressBar;
    private static final  String TAG = "registroActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Toast.makeText(registroActivity.this, "Ya puedes registrarte", Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.progressbar);
        editTextNombre = findViewById(R.id.edtxtnombre);
        editTextEmail = findViewById(R.id.edtxtemail);
        editTextFecha = findViewById(R.id.edtxtfecha);
        editTextContra = findViewById(R.id.edtxtcontra);
        editTextConf = findViewById(R.id.edtxtconfirm);

        Button buttonRegistro = findViewById(R.id.btnregistrar);
        buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtNombre = editTextNombre.getText().toString();
                String txtEmail = editTextEmail.getText().toString();
                String txtFecha = editTextFecha.getText().toString();
                String txtContra = editTextContra.getText().toString();
                String txtConf = editTextConf.getText().toString();

                if (TextUtils.isEmpty(txtNombre)) {
                    Toast.makeText(registroActivity.this, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show();
                    editTextNombre.setError("Se requiere un nombre");
                    editTextNombre.requestFocus();
                } else if (TextUtils.isEmpty(txtEmail)) {
                    Toast.makeText(registroActivity.this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Se requiere un correo electrónico");
                    editTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                    Toast.makeText(registroActivity.this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Se requiere un correo electrónico válido");
                    editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(txtFecha)) {
                    Toast.makeText(registroActivity.this, "Por favor, ingresa tu fecha de nacimiento", Toast.LENGTH_SHORT).show();
                    editTextContra.setError("Se requiere una fecha de nacimiento");
                } else if (TextUtils.isEmpty(txtContra)) {
                    Toast.makeText(registroActivity.this, "Por favor, ingresa una contraseña", Toast.LENGTH_SHORT).show();
                    editTextContra.setError("Se requiere una contraseña");
                    editTextContra.requestFocus();
                } else if (txtContra.length() < 6){
                    Toast.makeText(registroActivity.this, "La contraseña debe tener al menos 6 carácteres", Toast.LENGTH_SHORT).show();
                    editTextContra.setError("Contraseña muy débil");
                    editTextContra.requestFocus();
                }else if (TextUtils.isEmpty(txtConf)) {
                    Toast.makeText(registroActivity.this, "Por favor, confirma la contraseña", Toast.LENGTH_SHORT).show();
                    editTextConf.setError("Se requiere confirmar la contraseña");
                    editTextConf.requestFocus();
                } else if (!txtContra.equals(txtConf)) {
                    Toast.makeText(registroActivity.this, "Por favor, confirma la contraseña", Toast.LENGTH_SHORT).show();
                    editTextConf.setError("Confirmación de contraseña incorrecta");
                    editTextConf.requestFocus();
                    editTextConf.clearComposingText();
                    editTextContra.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registrarUsuario(txtNombre, txtEmail, txtFecha, txtContra, txtConf);
                }
            }
        });
    }

    private void registrarUsuario(String txtNombre, String txtEmail, String txtFecha, String txtContra, String txtConf) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Crear perfil de usuario
        auth.createUserWithEmailAndPassword(txtEmail, txtContra).addOnCompleteListener(registroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Guardar datos de usuario en Realtime Database
                    LeerEscribirDatosdeUsuario escribirDatos = new LeerEscribirDatosdeUsuario(txtNombre, txtFecha);

                    //Extraer la referencia de usuario desde BD para Usuarios Registrados
                    DatabaseReference referencePerfil = FirebaseDatabase.getInstance().getReference("Usuarios Registrados");

                    referencePerfil.child(firebaseUser.getUid()).setValue(escribirDatos).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                //Envía verificación por email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(registroActivity.this, "Usuario registrado exitosamente. Por favor, verifica tu correo.", Toast.LENGTH_LONG).show();

                                //Abre el perfil del usuario una vez que se registró
                                Intent intent = new Intent(registroActivity.this, perfilActivity.class);
                                //Para prevenir al usuario de volver a la actividad Registro si presiona el botón regresar después del registro
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(registroActivity.this, "Registro fallido. Inténtalo de nuevo más tarde.", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextContra.setError("La contraseña es muy débil. Por favor ingrese una combinación de letras, números y carácteres especiales.");
                        editTextContra.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        editTextEmail.setError("El correo electrónico es inválido o se encuentra en uso. Ingrese uno nuevo.");
                        editTextEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        editTextEmail.setError("Este correo electrónico se encuentra en uso.");
                        editTextEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(registroActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}