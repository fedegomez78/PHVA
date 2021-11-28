package com.empresa.phva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText editTextNombreUsuario, editContraseña,editTextNombre, editTextApellido, editTextCedula, editTextTelefono, editCelular, editDireccion;
    private TextView TxvSelectRole;
    private CheckBox checkBox;
    private Button btnfinalizar;
    TextView txvRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextNombreUsuario = findViewById(R.id.editTextNombreUsuario);
        editContraseña = findViewById(R.id.editContraseña);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextCedula = findViewById(R.id.editTextCedula);
        editTextTelefono = findViewById(R.id.editTextTelefono);
        editCelular = findViewById(R.id.editCelular);
        editDireccion = findViewById(R.id.editDireccion);
        checkBox = findViewById(R.id.checkBox);
        btnfinalizar = findViewById(R.id.finalizar);
        TxvSelectRole = findViewById(R.id.TxvSelectRole);
        txvRol = (TextView) findViewById(R.id.TxvSelectRole);
        txvRol.setOnClickListener(v -> showAlertDialog());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.select_roles, android.R.layout.simple_spinner_item);
    }

    public void createUser(){
        String nameUser = editTextNombreUsuario.getText().toString();
        String password = editContraseña.getText().toString();
        String name = editTextNombre.getText().toString();
        String apellido = editTextApellido.getText().toString();
        String cedula = editTextCedula.getText().toString();
        String telefono = editTextTelefono.getText().toString();
        String celular = editCelular.getText().toString();
        String direccion = editDireccion.getText().toString();
        String rol = TxvSelectRole.getText().toString();

        if(TextUtils.isEmpty(nameUser)){
            editTextNombreUsuario.requestFocus();
        }else  if(TextUtils.isEmpty(password)){
            editContraseña.requestFocus();
        }else if(TextUtils.isEmpty(name)){
            editTextNombre.requestFocus();
        }else if(TextUtils.isEmpty(apellido)){
            editTextApellido.requestFocus();
        }else if(TextUtils.isEmpty(cedula)){
            editTextCedula.requestFocus();
        }else if(TextUtils.isEmpty(telefono)){
            editTextTelefono.requestFocus();
        }else if(TextUtils.isEmpty(celular)){
            editCelular.requestFocus();
        }else if(TextUtils.isEmpty(direccion)){
            editDireccion.requestFocus();
        }else if(TextUtils.isEmpty(rol)){
            TxvSelectRole.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(nameUser, password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userId = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = db.collection("users").document(userId);

                                Map<String,Object> user = new HashMap<>();
                                user.put("Usuario", nameUser);
                                user.put("Nombre", name);
                                user.put("Apellido", apellido);
                                user.put("Cedula",cedula);
                                user.put("Telefono",telefono);
                                user.put("Celular", celular);
                                user.put("Direccion", direccion);
                                user.put("Rol", rol);

                                //regstro los datos nuevos con el metodo del set.(user)
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>(){
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Toast.makeText(Registro.this, "Datos registrados", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(Registro.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Registro.this, AccesoModulos.class));
                            } else {
                                Toast.makeText(Registro.this, "Usuario no registrado"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public void modulos (View view){
        Intent modulos = new Intent(this, MainActivity.class);
        startActivity(modulos);
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Registro.this);
        alertDialog.setTitle("Seleccione su perfil.");
        String[] items = {"Administrador", "Empleado", "Supervisor", "Presidente COCOLA", "Presidente BE", "Presidente COPASST"};
        boolean[] checkedItems = {false, false, false, false, false, false};
        alertDialog.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                SharedPreferences preferences = getSharedPreferences("which", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                for (int i=0; i < items.length; i++ ) {

                    Log.e("LOG", "booleano : " + checkedItems[i]);
                    Log.e("LOG", "nombre Item: " + items[i]);

                    if (checkedItems[i]==true){
                        txvRol.setText(items[i]);
                        dialog.dismiss();
                    }

                }

                editor.putBoolean("ischeck", isChecked);
                editor.putInt("case", which);
                editor.commit();
                //ShowAdmin();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    private void ShowAdmin() {
        //Intent admin = new Intent(this, home_principal.class);
        //startActivity(admin);
    }

}