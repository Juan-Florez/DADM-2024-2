package com.example.reto10

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DataAdapter(private val dataItems: List<DataItem>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAnoSolicitud: TextView = itemView.findViewById(R.id.tvAnoSolicitud)
        val tvNacionalidad: TextView = itemView.findViewById(R.id.tvNacionalidad)
        val tvSexo: TextView = itemView.findViewById(R.id.tvSexo)
        val tvFechaNacimiento: TextView = itemView.findViewById(R.id.tvFechaNacimiento)
        val tvVocacionPermanencia: TextView = itemView.findViewById(R.id.tvVocacionPermanencia)
        val tvNumero: TextView = itemView.findViewById(R.id.tvNumero)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItems[position]
        holder.tvAnoSolicitud.text = "Año de Solicitud: ${item.anoSolicitud}"
        holder.tvNacionalidad.text = "Nacionalidad: ${item.nacionalidad}"
        holder.tvSexo.text = "Sexo: ${item.sexo}"
        holder.tvFechaNacimiento.text = "Fecha de Nacimiento: ${item.fechaNacimiento}"
        holder.tvVocacionPermanencia.text = "Vocación de Permanencia: ${item.vocacionPermanencia}"
        holder.tvNumero.text = "Número: ${item.numero}"
    }

    override fun getItemCount(): Int = dataItems.size
}