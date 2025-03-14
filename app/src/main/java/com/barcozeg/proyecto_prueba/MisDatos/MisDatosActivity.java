package com.barcozeg.proyecto_prueba.MisDatos;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.DatePickerDialog;
import java.util.Calendar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

import com.barcozeg.proyecto_prueba.R;
import com.google.firebase.database.ValueEventListener;
import com.barcozeg.proyecto_prueba.RegistroActivity;


public class MisDatosActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText etnombresMd, etapellidosMD, etfechaNacimientoMd, etedadMd, ettelefonoMd, etdomicilioMd, etprofesionMd, ettiktokMd;
    private Button btnguardarMd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_datos);

        // Inicializar Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();



        // Enlazar los componestes del XML
        etnombresMd = findViewById(R.id.etnombresMd);
        etapellidosMD = findViewById(R.id.etapellidosMd);
        etfechaNacimientoMd = findViewById(R.id.etfechaNacimientoMd);
        etedadMd = findViewById(R.id.etedadMd);
        ettelefonoMd = findViewById(R.id.ettelefonoMd);
        etdomicilioMd = findViewById(R.id.etdomicilioMd);
        etprofesionMd = findViewById(R.id.etprofesionMd);
        ettiktokMd = findViewById(R.id.ettiktokMd);
        btnguardarMd = findViewById(R.id.btnguardarMd);

        // Evento de clic para guardar datos
        btnguardarMd.setOnClickListener(v -> {
            guardarDatos();
            actualizarDatos();
            mostrarAviso();
        });

        // Evento para abrir el DatePicker al tocar el EditText
        etfechaNacimientoMd.setOnClickListener(v -> mostrarDatePicker());

        // Obtener referencias a los TextViews
        TextView textViewNombre = findViewById(R.id.obtNombreMd);
        TextView textViewCorreo = findViewById(R.id.obtCorreoMd);


        // Obtener el nombre enviado desde MainActivity
        String nombreUsuario = getIntent().getStringExtra("nombre_usuario");
        String correoUsuario = getIntent().getStringExtra("correo_usuario");

        // Manejar valores nulos
        nombreUsuario = (nombreUsuario != null) ? nombreUsuario : "No disponible";
        correoUsuario = (correoUsuario != null) ? correoUsuario : "Correo no disponible";

        // Mostrarlo en un TextView
        textViewNombre.setText("Codigo Usuario: " + nombreUsuario);
        textViewCorreo.setText("Correo Usuario: " + correoUsuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lecturaDatos();
    }

    private void guardarDatos() {
        String nombreMD = etnombresMd.getText().toString().trim();
        String apellidoMD = etapellidosMD.getText().toString().trim();
        String fechaNacimientoMD = etfechaNacimientoMd.getText().toString().trim();
        String edadMD = etedadMd.getText().toString().trim();
        String telefonoMD = ettelefonoMd.getText().toString().trim();
        String domicilioMD = etdomicilioMd.getText().toString().trim();
        String profesionMD = etprofesionMd.getText().toString().trim();
        String tiktokMD = ettiktokMd.getText().toString().trim();

        if (nombreMD.isEmpty() || apellidoMD.isEmpty() || fechaNacimientoMD.isEmpty() ||
                edadMD.isEmpty() || telefonoMD.isEmpty() || domicilioMD.isEmpty() || tiktokMD.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fechaNacimientoMD.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Formato de fecha incorrecto (YYYY-MM-DD requerido)", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Obtener correo y contraseña antes de actualizar los datos
        mDatabase.child("usuarios").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String correoActual = snapshot.child("correo").getValue(String.class);
                    String passwordActual = snapshot.child("password").getValue(String.class);

                    correoActual = (correoActual != null) ? correoActual : "";
                    passwordActual = (passwordActual != null) ? passwordActual : "";

                    // Crear el HashMap con los datos
                    Map<String, Object> user = new HashMap<>();
                    user.put("fecha_nacimiento", fechaNacimientoMD);
                    user.put("nombres", nombreMD);
                    user.put("apellidos", apellidoMD);
                    user.put("edad", edadMD);
                    user.put("telefono", telefonoMD);
                    user.put("domicilio", domicilioMD);
                    user.put("profesion", profesionMD);
                    user.put("tiktok", tiktokMD);
                    user.put("correo", correoActual);
                    user.put("password", passwordActual);

                    // Guardar en Firebase
                    mDatabase.child("usuarios").child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MisDatosActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MisDatosActivity.this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MisDatosActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Guardar fecha en formato YYYY-MM-DD
                    String fechaSeleccionada = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
                    etfechaNacimientoMd.setText(fechaSeleccionada);

                    // Calcular edad
                    int edadCalculada = calcularEdad(year, month, dayOfMonth);
                    etedadMd.setText(String.valueOf(edadCalculada));
                },
                anio, mes, dia
        );

        datePickerDialog.show();
    }

    private int calcularEdad(int anioNacimiento, int mesNacimiento, int diaNacimiento) {
        // Obtener la fecha actual
        Calendar hoy = Calendar.getInstance();
        int anioActual = hoy.get(Calendar.YEAR);
        int mesActual = hoy.get(Calendar.MONTH);
        int diaActual = hoy.get(Calendar.DAY_OF_MONTH);

        // Calcular la edad
        int edad = anioActual - anioNacimiento;

        // Verificar si el cumpleaños aún no ha pasado en el año actual
        if (mesActual < mesNacimiento || (mesActual == mesNacimiento && diaActual < diaNacimiento)) {
            edad--; // Restar 1 si el cumpleaños no ha ocurrido aún
        }

        return edad;
    }

    private void lecturaDatos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etnombresMd.setText(snapshot.child("nombres").getValue(String.class));
                    etapellidosMD.setText(snapshot.child("apellidos").getValue(String.class));
                    etfechaNacimientoMd.setText(snapshot.child("fecha_nacimiento").getValue(String.class));
                    etedadMd.setText(snapshot.child("edad").getValue(String.class));
                    ettelefonoMd.setText(snapshot.child("telefono").getValue(String.class));
                    etdomicilioMd.setText(snapshot.child("domicilio").getValue(String.class));
                    etprofesionMd.setText(snapshot.child("profesion").getValue(String.class));
                    ettiktokMd.setText(snapshot.child("tiktok").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MisDatosActivity.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarDatos(){
        String nombre_act = etnombresMd.getText().toString().trim();
        String apellido_act = etapellidosMD.getText().toString().trim();
        String fechanaciemiento_act = etfechaNacimientoMd.getText().toString().trim();
        String edad_act = etedadMd.getText().toString().trim();
        String telefono_act = ettelefonoMd.getText().toString().trim();
        String domicilio_act = etdomicilioMd.getText().toString().trim();
        String profesion_act = etprofesionMd.getText().toString().trim();
        String tiktok_act = ettiktokMd.getText().toString().trim();

        HashMap<String, Object> datos_actualizar = new HashMap<>();
        datos_actualizar.put("nombres", nombre_act);
        datos_actualizar.put("apellidos", apellido_act);
        datos_actualizar.put("fecha_nacimiento", fechanaciemiento_act);
        datos_actualizar.put("edad", edad_act);
        datos_actualizar.put("telefono", telefono_act);
        datos_actualizar.put("domicilio", domicilio_act);
        datos_actualizar.put("profesion", profesion_act);
        datos_actualizar.put("tiktok", tiktok_act);

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(userId).updateChildren(datos_actualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MisDatosActivity.this,"Esperando datos ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarAviso() {
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

