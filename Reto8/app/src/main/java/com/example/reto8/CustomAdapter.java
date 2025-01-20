package com.example.reto8;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private Activity activity;
    private ArrayList<String> id, name, url, telefono, email, producto, clasificacion;

    CustomAdapter(Activity activity,
                  Context context,
                  ArrayList id,
                  ArrayList name,
                  ArrayList url,
                  ArrayList telefono ,
                  ArrayList email ,
                  ArrayList producto,
                  ArrayList clasificacion){
        this.activity = activity;
        this.context = context;
        this.id = id;
        this.name = name;
        this.url = url;
        this.telefono = telefono;
        this.email = email;
        this.producto = producto;
        this.clasificacion = clasificacion;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.id_empresa_txt.setText(String.valueOf(id.get(position)));
        holder.name_txt.setText(String.valueOf(name.get(position)));
        holder.url_txt.setText(String.valueOf(url.get(position)));
        holder.telefono_txt.setText(String.valueOf(telefono.get(position)));
        holder.email_txt.setText(String.valueOf(email.get(position)));
        holder.productos_txt.setText(String.valueOf(producto.get(position)));
        holder.clasificacion_txt.setText(String.valueOf(clasificacion.get(position)));

        //Recyclerview onClickListener
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(id.get(position)));
                intent.putExtra("name", String.valueOf(name.get(position)));
                intent.putExtra("url", String.valueOf(url.get(position)));
                intent.putExtra("telefono", String.valueOf(telefono.get(position)));
                intent.putExtra("email", String.valueOf(email.get(position)));
                intent.putExtra("producto", String.valueOf(producto.get(position)));
                intent.putExtra("clasificacion", String.valueOf(clasificacion.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id_empresa_txt, name_txt, url_txt, telefono_txt, email_txt, productos_txt, clasificacion_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id_empresa_txt = itemView.findViewById(R.id.id_empresa_txt);
            name_txt = itemView.findViewById(R.id.name_txt);
            url_txt = itemView.findViewById(R.id.url_txt);
            telefono_txt = itemView.findViewById(R.id.telefono_txt);
            email_txt = itemView.findViewById(R.id.email_txt);
            productos_txt = itemView.findViewById(R.id.productos_txt);
            clasificacion_txt = itemView.findViewById(R.id.clasificacion_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }

    }

}
