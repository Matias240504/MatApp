package com.barcozeg.proyecto_prueba.Cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.barcozeg.proyecto_prueba.Clases.Cliente;
import com.barcozeg.proyecto_prueba.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AgregarClienteActivity extends AppCompatActivity {

    private EditText etNombreCLI, etApellidosCLI, etCorreoCLI, etDniCLI, etTelefonoCLI, etDireccionCLI;
    private MaterialButton btnGuardarCLI;
    private DatabaseReference usuariosClienteRef;
    private String uid_clienteCLI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cliente);

        // Obtener UID del usuario autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid_clienteCLI = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Error: No hay usuario autenticado", Toast.LENGTH_LONG).show();
            finish(); // Cierra la actividad si no hay usuario autenticado
            return;
        }

        // Mostrar UID en la interfaz
        TextView textViewID = findViewById(R.id.idUsuariocli);
        textViewID.setText("Mi UID: " + uid_clienteCLI);

        // Inicializar Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usuariosClienteRef = database.getReference("Clientes"); // Guardamos en "Clientes"

        // Referencias a los EditText y Botón
        etNombreCLI = findViewById(R.id.etnombrecli);
        etApellidosCLI = findViewById(R.id.etapellidoscli);
        etCorreoCLI = findViewById(R.id.etcorreocli);
        etDniCLI = findViewById(R.id.etdnicli);
        etTelefonoCLI = findViewById(R.id.ettelefonocli);
        etDireccionCLI = findViewById(R.id.etdireccioncli);
        btnGuardarCLI = findViewById(R.id.btnguardarcliente);

        // Evento para guardar en Firebase
        btnGuardarCLI.setOnClickListener(v -> registrarCliente());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registrarCliente() {
        String id_clienteCLI = usuariosClienteRef.push().getKey(); // Genera un ID único en Firebase
        String nombresCLI = etNombreCLI.getText().toString();
        String apellidosCLI = etApellidosCLI.getText().toString();
        String correoCLI = etCorreoCLI.getText().toString();
        String telefonoCLI = etTelefonoCLI.getText().toString();
        String dniCLI = etDniCLI.getText().toString();
        String direccionCLI = etDireccionCLI.getText().toString();

        // Validaciones básicas
        if (nombresCLI.isEmpty() || apellidosCLI.isEmpty() || correoCLI.isEmpty() || telefonoCLI.isEmpty() || dniCLI.isEmpty() || direccionCLI.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Cliente con el id generado y el UID del usuario autenticado
        Cliente nuevoCliente = new Cliente(id_clienteCLI, uid_clienteCLI, nombresCLI, apellidosCLI, correoCLI, telefonoCLI, dniCLI, direccionCLI);

        // Guardar en Firebase
        assert id_clienteCLI != null;
        usuariosClienteRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(uid_clienteCLI)
                .child("clientes")
                .child(id_clienteCLI);

        // Guardar en Firebase usando la clase Cliente
        usuariosClienteRef.setValue(nuevoCliente)
                .addOnSuccessListener(aVoid -> Toast.makeText(AgregarClienteActivity.this, "Cliente registrado correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AgregarClienteActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show());
    }
}
