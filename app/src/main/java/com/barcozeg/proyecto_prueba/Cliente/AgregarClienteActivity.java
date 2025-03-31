package com.barcozeg.proyecto_prueba.Cliente;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
    private TextView TituloCliente;
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
        TituloCliente = findViewById(R.id.TituloCliente);

        // Obtener el objeto Cliente desde el Intent
        Cliente cliente = (Cliente) getIntent().getSerializableExtra("cliente");

        // Verificar si estamos en modo edición
        if (getIntent().hasExtra("cliente")) {
            // Opción 1: Usando el objeto Cliente serializado (recomendado)
            cliente = (Cliente) getIntent().getSerializableExtra("cliente");
            if (cliente != null) {
                llenarCampos(cliente);
                btnGuardarCLI.setText("Actualizar Cliente"); // Cambiar texto del botón
                TituloCliente.setText("Actualizar Cliente"); // Cambiar texto del titulo
            }
        }

        btnGuardarCLI.setOnClickListener(v -> {
            if (getIntent().hasExtra("cliente") || getIntent().hasExtra("id_cliente")) {
                actualizarCliente();
            } else {
                registrarCliente();
                mostrarAvisoConfirmacion();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void llenarCampos(Cliente cliente) {
        etNombreCLI.setText(cliente.getNombres());
        etApellidosCLI.setText(cliente.getApellidos());
        etCorreoCLI.setText(cliente.getCorreo());
        etDniCLI.setText(cliente.getDni());
        etTelefonoCLI.setText(cliente.getTelefono());
        etDireccionCLI.setText(cliente.getDireccion());
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
        if (nombresCLI.isEmpty() || apellidosCLI.isEmpty() || telefonoCLI.isEmpty()) {
            mostrarAvisoAdvertencia();
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

    private void actualizarCliente() {
        // 1. Validación del ID del cliente (con dos métodos alternativos)
        String id_clienteCLI = null;

        // Opción A: Si recibiste el objeto Cliente completo
        if (getIntent().hasExtra("cliente")) {
            Cliente cliente = (Cliente) getIntent().getSerializableExtra("cliente");
            if (cliente != null) {
                id_clienteCLI = cliente.getId_cliente();
            }
        }
        // Opción B: Si recibiste solo el ID como string
        else if (getIntent().hasExtra("id_cliente")) {
            id_clienteCLI = getIntent().getStringExtra("id_cliente");
        }

        // 2. Validación estricta del ID
        if (id_clienteCLI == null || id_clienteCLI.isEmpty()) {
            Toast.makeText(this, "Error: ID de cliente no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Validación del UID de usuario
        if (uid_clienteCLI == null || uid_clienteCLI.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Obtener valores de los campos con trim()
        String nombresCLI = etNombreCLI.getText().toString().trim();
        String apellidosCLI = etApellidosCLI.getText().toString().trim();
        String correoCLI = etCorreoCLI.getText().toString().trim();
        String telefonoCLI = etTelefonoCLI.getText().toString().trim();
        String dniCLI = etDniCLI.getText().toString().trim();
        String direccionCLI = etDireccionCLI.getText().toString().trim();

        // 5. Validación de campos obligatorios
        if (nombresCLI.isEmpty() || apellidosCLI.isEmpty() || telefonoCLI.isEmpty()) {
            mostrarAvisoAdvertencia();
            return;
        }

        // 6. Construcción de la referencia con verificación
        DatabaseReference clienteRef;
        try {
            clienteRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios")
                    .child(uid_clienteCLI)
                    .child("clientes")
                    .child(id_clienteCLI);
        } catch (Exception e) {
            Toast.makeText(this, "Error en estructura de base de datos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 7. Actualización con manejo de errores mejorado
        try {
            Cliente clienteActualizado = new Cliente(
                    id_clienteCLI,
                    uid_clienteCLI,
                    nombresCLI,
                    apellidosCLI,
                    correoCLI,
                    telefonoCLI,
                    dniCLI,
                    direccionCLI
            );

            clienteRef.setValue(clienteActualizado)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear objeto Cliente", Toast.LENGTH_SHORT).show();

        }
    }


    private void mostrarAvisoAdvertencia() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_advertencia, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Configurar el botón para cerrar el diálogo
        Button btnEntendidoCliente = dialogView.findViewById(R.id.btnEntendidoCliente);
        btnEntendidoCliente.setOnClickListener(v -> dialog.dismiss());

        // Hacer que el diálogo aparezca centrado y sin tocar los bordes
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Fondo transparente
            window.setGravity(Gravity.CENTER); // Centrar en la pantalla
        }
    }

    private void mostrarAvisoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_confirmacion, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Hacer que el diálogo aparezca centrado y sin tocar los bordes
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Fondo transparente
            window.setGravity(Gravity.CENTER); // Centrar en la pantalla

        }

        // Cerrar el diálogo automáticamente
        new Handler().postDelayed(dialog::dismiss, 4000);
    }

}
