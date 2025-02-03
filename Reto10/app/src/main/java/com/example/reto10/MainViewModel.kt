package com.example.reto10

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

class MainViewModel : ViewModel() {
    private val _dataItems = MutableLiveData<List<DataItem>>()
    val dataItems: LiveData<List<DataItem>> get() = _dataItems

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private var currentOffset = 0
    private val limit = 5000

    fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val response = RetrofitClient.instance.getData(null, null, null, limit, currentOffset)
                _dataItems.value = response
                currentOffset += limit
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchData(nacionalidad: String?, anoSolicitud: String?, sexo: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val response = RetrofitClient.instance.getData(nacionalidad, anoSolicitud, sexo, limit, currentOffset)
                _dataItems.value = response
                currentOffset += limit
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar los datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreData(nacionalidad: String?, anoSolicitud: String?, sexo: String?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val response = RetrofitClient.instance.getData(nacionalidad, anoSolicitud, sexo, limit, currentOffset)
                val currentList = _dataItems.value ?: emptyList()
                _dataItems.value = currentList + response
                currentOffset += limit
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar más datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun calculateStats(dataItems: List<DataItem>): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()

        // Número de registros por nacionalidad
        val nacionalidadCount = dataItems.groupingBy { it.nacionalidad }.eachCount()
        stats["nacionalidadCount"] = nacionalidadCount

        // Número de registros por año
        val anoSolicitudCount = dataItems.groupingBy { it.anoSolicitud }.eachCount()
        stats["anoSolicitudCount"] = anoSolicitudCount

        // Promedio de edad
        val totalAge = dataItems.sumOf { calculateAge(it.fechaNacimiento) }
        val averageAge = if (dataItems.isNotEmpty()) totalAge / dataItems.size else 0
        stats["averageAge"] = averageAge

        return stats
    }

    private fun calculateAge(fechaNacimiento: String): Int {
        return try {
            val birthDate = LocalDate.parse(fechaNacimiento.substring(0, 10))
            val currentDate = LocalDate.now()
            Period.between(birthDate, currentDate).years
        } catch (e: Exception) {
            0 // Si hay un error en el formato de la fecha, retornar 0
        }
    }
}