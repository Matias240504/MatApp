package com.barcozeg.proyecto_prueba;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.barcozeg.proyecto_prueba.MainActivity;
import com.barcozeg.proyecto_prueba.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText etCorreo, etNombres, etApellidos, etPassword, etConfirmPassword;
    private Button btnRegistrar, btnIrLogin;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Enlazar con los componentes del XML
        etCorreo = findViewById(R.id.etcorreo);
        etNombres = findViewById(R.id.etnombres);
        etApellidos = findViewById(R.id.etapellidos);
        etPassword = findViewById(R.id.etpassword);
        etConfirmPassword = findViewById(R.id.etconfirmPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnIrLogin = findViewById(R.id.btnIrLogin);

        // Evento de clic para registrar usuario
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        // Evento de clic para ir al LoginActivity
        btnIrLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registrarUsuario() {
        String correo = etCorreo.getText().toString().trim();
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (correo.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Obtener el UID del usuario autenticado
                            String userId = mAuth.getCurrentUser().getUid();

                            // Guardar los datos del usuario en Firebase Database
                            Map<String, Object> user = new HashMap<>();
                            user.put("correo", correo);
                            user.put("nombres", nombres);
                            user.put("apellidos", apellidos);
                            user.put("password", password);
                            user.put("fecha_nacimiento","");
                            user.put("telefono","");
                            user.put("edad","");
                            user.put("domicilio","");
                            user.put("profesion","");
                            user.put("tiktok","");


                            mDatabase.child("usuarios").child(userId).setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegistroActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                        finish();  // Finaliza la actividad de registro
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(RegistroActivity.this, "Error al guardar usuario", Toast.LENGTH_SHORT).show()
                                    );

                        } else {
                            Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}