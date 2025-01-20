package com.example.reto8;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button, reset_button;
    ImageView empty_imageview;
    TextView no_data;
    EditText search_name, search_clasificacion;
    Button search_button;

    MyDatabaseHelper myDB;
    ArrayList<String> id, name, url, telefono, email, producto, clasificacion;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);
        add_button = findViewById(R.id.add_button);
        reset_button = findViewById(R.id.resetButton);

        // Filtros
        search_name = findViewById(R.id.search_name);
        search_clasificacion = findViewById(R.id.search_clasificacion);
        search_button = findViewById(R.id.search_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        myDB = new MyDatabaseHelper(MainActivity.this);
        id = new ArrayList<>();
        name = new ArrayList<>();
        url = new ArrayList<>();
        telefono = new ArrayList<>();
        email = new ArrayList<>();
        producto = new ArrayList<>();
        clasificacion = new ArrayList<>();

        customAdapter = new CustomAdapter(MainActivity.this, this, id, name, url,
                telefono,email,producto,clasificacion);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        // Inicializa los datos sin filtro
        storeDataInArrays(myDB.readAllData());

        // Configura el botón de búsqueda
        search_button.setOnClickListener(v -> {
            String name = search_name.getText().toString().trim();
            String clas = search_clasificacion.getText().toString().trim();

            // Llamar a searchData y actualizar el RecyclerView
            MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
            Cursor cursor = myDB.searchData(name, clas);

            if (cursor != null) {
                storeDataInArrays(cursor); // Actualiza los datos en el RecyclerView
                cursor.close();
            }
        });

        reset_button.setOnClickListener(v -> {
            // Leer todos los datos de la base de datos
            MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
            Cursor cursor = myDB.readAllData();

            if (cursor != null) {
                storeDataInArrays(cursor); // Actualiza los datos en el RecyclerView
                cursor.close();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays(Cursor cursor) {
        id.clear();
        name.clear();
        url.clear();
        telefono.clear();
        email.clear();
        producto.clear();
        clasificacion.clear();

        if (cursor.getCount() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                id.add(cursor.getString(0));
                name.add(cursor.getString(1));
                url.add(cursor.getString(2));
                telefono.add(cursor.getString(3));
                email.add(cursor.getString(4));
                producto.add(cursor.getString(5));
                clasificacion.add(cursor.getString(6));
            }
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_all){
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
                myDB.deleteAllData();
                //Refresh Activity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
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