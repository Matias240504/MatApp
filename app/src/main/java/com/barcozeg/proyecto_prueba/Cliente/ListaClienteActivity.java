package com.barcozeg.proyecto_prueba.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.barcozeg.proyecto_prueba.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListaClienteActivity extends AppCompatActivity {

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
    }
}