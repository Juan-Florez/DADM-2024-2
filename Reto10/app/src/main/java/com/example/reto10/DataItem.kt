package com.example.reto10

import com.google.gson.annotations.SerializedName

data class DataItem(
    @SerializedName("a_o_solicitud") val anoSolicitud: String,
    @SerializedName("nacionalidad") val nacionalidad: String,
    @SerializedName("sexo") val sexo: String,
    @SerializedName("fecha_de_nacimiento") val fechaNacimiento: String,
    @SerializedName("vocaci_n_de_permanencia") val vocacionPermanencia: String,
    @SerializedName("n_mero") val numero: String
)