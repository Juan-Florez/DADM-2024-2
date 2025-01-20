package com.example.reto8;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {

    EditText name_input, url_input, telefono_input, email_input, producto_input;
    Spinner clasificacion_input;
    Button update_button, delete_button;

    String id, name, url, telefono, email, producto, clasificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name_input = findViewById(R.id.name2);
        url_input = findViewById(R.id.url2);
        telefono_input = findViewById(R.id.telefono2);
        email_input = findViewById(R.id.email2);
        producto_input = findViewById(R.id.producto2);
        clasificacion_input = findViewById(R.id.clasificacion2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        // Configurar Spinner para clasificacion
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.clasificaciones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clasificacion_input.setAdapter(adapter);

        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(name);
        }

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                name = name_input.getText().toString().trim();
                url = url_input.getText().toString().trim();
                telefono = telefono_input.getText().toString().trim();
                email = email_input.getText().toString().trim();
                producto = producto_input.getText().toString().trim();
                clasificacion = clasificacion_input.getSelectedItem().toString().trim();
                myDB.updateData(id, name, url, telefono,email,producto,clasificacion);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }
    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name") &&
                getIntent().hasExtra("url") && getIntent().hasExtra("telefono")
                && getIntent().hasExtra("email") && getIntent().hasExtra("producto")
                && getIntent().hasExtra("clasificacion")){
            //Getting Data from Intent
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            url = getIntent().getStringExtra("url");
            telefono = getIntent().getStringExtra("telefono");
            email = getIntent().getStringExtra("email");
            producto = getIntent().getStringExtra("producto");
            clasificacion = getIntent().getStringExtra("clasificacion");

            //Setting Intent Data
            name_input.setText(name);
            url_input.setText(url);
            telefono_input.setText(telefono);
            email_input.setText((email));
            producto_input.setText((producto));
            int spinnerPosition = ((ArrayAdapter) clasificacion_input.getAdapter()).getPosition(clasificacion);
            clasificacion_input.setSelection(spinnerPosition);
            Log.d("stev", name+" "+url+" "+telefono+" "+email+" "+producto+" "+clasificacion);
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + name + " ?");
        builder.setMessage("Are you sure you want to delete " + name + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}