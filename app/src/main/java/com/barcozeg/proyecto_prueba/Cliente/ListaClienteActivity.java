package com.barcozeg.proyecto_prueba.Cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barcozeg.proyecto_prueba.Clases.Cliente;
import com.barcozeg.proyecto_prueba.R;
import com.barcozeg.proyecto_prueba.ViewHolder.ViewHolderCliente;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListaClienteActivity extends AppCompatActivity {
    RecyclerView recyclerViewClientes;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BD_usuarios;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseRecyclerAdapter <Cliente, ViewHolderCliente> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions <Cliente>firebaseRecyclerOptions;
    FloatingActionButton btnAgregarClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewClientes = findViewById(R.id.recyclerViewClientes);
        recyclerViewClientes.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        BD_usuarios = firebaseDatabase.getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        String idUsuario = getIntent().getStringExtra("id_usuario");

        btnAgregarClient = findViewById(R.id.btnAgregarClient);

        btnAgregarClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListaClienteActivity.this, "Hola Jovenes, Bienvenidos", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListaClienteActivity.this, AgregarClienteActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                startActivity(intent);
            }
        });
        listarClientes();
    }

    private void listarClientes() {
        Query query = BD_usuarios.child(firebaseUser.getUid()).child("clientes").orderByChild("nombres");

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Cliente>().setQuery(query, Cliente.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Cliente, ViewHolderCliente>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCliente viewHolderCliente, int position, @NonNull Cliente cliente) {
                viewHolderCliente.setearDatosCliente(
                        getApplicationContext(),
                        cliente.getId_cliente(),
                        cliente.getUid_cliente(),
                        cliente.getNombres(),
                        cliente.getApellidos(),
                        cliente.getCorreo(),
                        cliente.getDni(),
                        cliente.getTelefono(),
                        cliente.getDireccion()
                );
            }

            @NonNull
            @Override
            public ViewHolderCliente onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
                ViewHolderCliente viewHolderCliente = new ViewHolderCliente(view);
                viewHolderCliente.setOnClickListener(new ViewHolderCliente.clicklistener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(ListaClienteActivity.this, "Un click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Cliente clienteSeleccionado = getItem(position);
                        mostrarDialogoOpciones(clienteSeleccionado);
                    }
                });
                return viewHolderCliente;
            }
        };

        recyclerViewClientes.setLayoutManager(new GridLayoutManager(ListaClienteActivity.this,2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewClientes.setAdapter(firebaseRecyclerAdapter);
    }

    private void mostrarDialogoOpciones(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_clientes, null);
        builder.setView(dialogView);

        ImageView btnEditar = dialogView.findViewById(R.id.btnEditarCliente);
        ImageView btnEliminar = dialogView.findViewById(R.id.btnEliminarCliente);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Hacer que el diálogo aparezca centrado y sin tocar los bordes
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Fondo transparente
            window.setGravity(Gravity.CENTER); // Centrar en la pantalla
        }

        // Acción para EDITAR (Versión mejorada)
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(ListaClienteActivity.this, AgregarClienteActivity.class);

            // Opción 1 (Recomendada): Pasar el objeto Cliente completo
            intent.putExtra("cliente", cliente); // Asegúrate que Cliente implementa Serializable

            // Opción 2: Si prefieres pasar solo el ID y luego cargar los datos desde DB
            // intent.putExtra("cliente_id", cliente.getId_cliente());

            startActivity(intent);
            dialog.dismiss();
        });

        // Acción para ELIMINAR (se mantiene igual)
        btnEliminar.setOnClickListener(v -> {
            eliminarCliente(cliente);
            dialog.dismiss();
        });
    }

    private void eliminarCliente(Cliente cliente) {
        DatabaseReference clienteRef = BD_usuarios.child(firebaseUser.getUid()).child("clientes").child(cliente.getId_cliente());

        clienteRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(ListaClienteActivity.this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
            firebaseRecyclerAdapter.notifyDataSetChanged(); // Refresca la lista
        }).addOnFailureListener(e -> {
            Toast.makeText(ListaClienteActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

}