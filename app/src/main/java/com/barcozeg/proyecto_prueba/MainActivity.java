package com.barcozeg.proyecto_prueba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.barcozeg.proyecto_prueba.MisDatos.MisDatosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class    MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputEditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Verificar si ya hay una sesión iniciada
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String savedUserId = preferences.getString("user_id", null);
        if (savedUserId != null) {
            // Si hay una sesión activa, ir directamente al Dashboard
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Configurar márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener referencias de los elementos UI
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        TextInputLayout layoutEmail = findViewById(R.id.editTextEmail);
        TextInputLayout layoutPassword = findViewById(R.id.editTextPassword);

        editTextEmail = (TextInputEditText) layoutEmail.getEditText();
        editTextPassword = (TextInputEditText) layoutPassword.getEditText();

        // Configurar botón de login
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                loginUser(email, password);
            }
        });

        // Botón para ir a la pantalla de registro
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInput(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid(); // Obtener el ID del usuario

                                // Referencia a la base de datos
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

                                // Obtener el nombre del usuario desde Firebase
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String nombre = snapshot.child("nombres").getValue(String.class);

                                            Log.d("DEBUG_FIREBASE", "Nombre obtenido: " + nombre);

                                            if (nombre == null || nombre.trim().isEmpty()) {
                                                nombre = "Usuario";
                                            }

                                            // Guardar sesion en SharedPreferences
                                            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("user_id", userId);
                                            editor.putString("nombre_usuario", nombre);
                                            editor.apply();

                                            // Enviar el nombre a DashboardActivity
                                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                            intent.putExtra("nombre_usuario", nombre);
                                            startActivity(intent);
                                            finish();


                                            Toast.makeText(MainActivity.this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("FIREBASE_ERROR", "Error al obtener el nombre", error.toException());
                                        Toast.makeText(MainActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Exception e = task.getException();
                            Log.e("LOGIN_ERROR", "Error en login", e);
                            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
