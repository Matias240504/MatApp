package com.barcozeg.proyecto_prueba;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.barcozeg.proyecto_prueba.Cliente.ListaClienteActivity;
import com.barcozeg.proyecto_prueba.Favoritos.FavoritosActivity;
import com.barcozeg.proyecto_prueba.Gastos.GastosActivity;
import com.barcozeg.proyecto_prueba.ListaTareas.ListaTareasActivity;
import com.barcozeg.proyecto_prueba.MisDatos.MisDatosActivity;
import com.barcozeg.proyecto_prueba.Tareas.TareasActivity;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    private TextView textViewNombre;
    private String nombreUsuario = "Usuario";
    private String correoUsuario = "No disponible";
    private FirebaseAuth mAuth;
    private Dialog dialogoDev;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        dialogoDev = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();
        textViewNombre = findViewById(R.id.textViewNombre);

        // Cargar datos de sesión desde SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        nombreUsuario = preferences.getString("nombre_usuario", "Usuario");

        // Obtener el nombre y correo desde el Intent
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("nombre_usuario")) {
                nombreUsuario = intent.getStringExtra("nombre_usuario");
            }
            if (intent.hasExtra("correo_usuario")) {
                correoUsuario = intent.getStringExtra("correo_usuario");
            }
        }

        // Si el correo sigue vacío, intenta obtenerlo de Firebase
        if (correoUsuario.equals("No disponible") && mAuth.getCurrentUser() != null) {
            correoUsuario = mAuth.getCurrentUser().getEmail();
        }

        // Mostrar el nombre del usuario en el TextView
        textViewNombre.setText("Usuario Nombre: " + nombreUsuario);

        // Inicializar los CardViews
        CardView cvClientes = findViewById(R.id.cvEmpresa);
        CardView cvGastos = findViewById(R.id.cvGastos);
        CardView cvTareas = findViewById(R.id.cvTareas);
        CardView cvListaTareas = findViewById(R.id.cvListaTareas);
        CardView cvFavoritos = findViewById(R.id.cvFavoritos);
        CardView cvMisDatos = findViewById(R.id.cvMisDatos);

        // Asignar OnClickListener a cada CardView
        cvClientes.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ListaClienteActivity.class);
            intent1.putExtra("id_usuario", mAuth.getCurrentUser().getUid());
            startActivity(intent1);
        });

        cvGastos.setOnClickListener(v -> startActivity(new Intent(this, GastosActivity.class)));
        cvTareas.setOnClickListener(v -> startActivity(new Intent(this, TareasActivity.class)));
        cvListaTareas.setOnClickListener(v -> startActivity(new Intent(this, ListaTareasActivity.class)));
        cvFavoritos.setOnClickListener(v -> startActivity(new Intent(this, FavoritosActivity.class)));

        // Pasar los datos a MisDatosActivity
        cvMisDatos.setOnClickListener(v -> {
            Intent misDatosIntent = new Intent(DashboardActivity.this, MisDatosActivity.class);
            misDatosIntent.putExtra("correo_usuario", correoUsuario);
            misDatosIntent.putExtra("nombre_usuario", nombreUsuario);
            startActivity(misDatosIntent);
        });

        // Botón para cerrar sesión
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // Boton para Dialogo
        Button btndev = findViewById(R.id.btndev);
        btndev.setOnClickListener (v -> desarrollador());

        // Ajustes de UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void cerrarSesion() {
        mAuth.signOut();

        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void desarrollador(){
        Button btnVolver;
        ImageButton btnTelefonodev, btnYotubedev;

        dialogoDev.setContentView(R.layout.dialogo_descripcion);

        Window window = dialogoDev.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }


        btnVolver = dialogoDev.findViewById(R.id.btnVolver);
        btnTelefonodev = dialogoDev.findViewById(R.id.btntelefonodev);
        btnYotubedev = dialogoDev.findViewById(R.id.btnyoutubedev);

        btnTelefonodev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = "938801663";
                Uri uri = Uri.parse("tel:" + numero);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        btnYotubedev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.youtube.com/watch?v=qAmulKjcHoo");
                Intent intent= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoDev.dismiss();
            }
        });

        dialogoDev.show();
        dialogoDev.setCanceledOnTouchOutside(false);
    }
}