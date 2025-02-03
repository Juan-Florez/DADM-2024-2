package com.example.reto10

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: DataAdapter
    private lateinit var tvStats: TextView
    private lateinit var tvErrorMessage: TextView
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = DataAdapter(emptyList())

        val rvResults = findViewById<RecyclerView>(R.id.rvResults)
        rvResults.layoutManager = LinearLayoutManager(this)
        rvResults.adapter = adapter

        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val spinnerOptions = findViewById<Spinner>(R.id.spinnerOptions)
        tvStats = findViewById(R.id.tvStats)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        // Cargar datos iniciales
        viewModel.loadInitialData()

        btnSearch.setOnClickListener {
            val selectedOption = spinnerOptions.selectedItem.toString()
            when (selectedOption) {
                "Filtrar por Nacionalidad" -> {
                    val nacionalidad = findViewById<EditText>(R.id.etSearch).text.toString()
                    viewModel.searchData(nacionalidad, null, null)
                }
                "Filtrar por Año" -> {
                    val anoSolicitud = findViewById<EditText>(R.id.etSearch).text.toString()
                    viewModel.searchData(null, anoSolicitud, null)
                }
                "Filtrar por Sexo" -> {
                    val sexo = findViewById<EditText>(R.id.etSearch).text.toString()
                    viewModel.searchData(null, null, sexo)
                }
                "Estadísticas por Nacionalidad" -> {
                    val stats = viewModel.calculateStats(viewModel.dataItems.value ?: emptyList())
                    mostrarEstadisticas(stats["nacionalidadCount"].toString())
                }
                "Estadísticas por Año" -> {
                    val stats = viewModel.calculateStats(viewModel.dataItems.value ?: emptyList())
                    mostrarEstadisticas("Estadísticas por Año:\n${stats["anoSolicitudCount"]}")
                }
                "Promedio de Edad" -> {
                    val stats = viewModel.calculateStats(viewModel.dataItems.value ?: emptyList())
                    mostrarEstadisticas("Promedio de Edad:\n${stats["averageAge"]} años")
                }
            }
        }

        // Observar cambios en los datos
        viewModel.dataItems.observe(this, { dataItems ->
            adapter = DataAdapter(dataItems)
            rvResults.adapter = adapter
        })

        // Observar errores
        viewModel.errorMessage.observe(this, { errorMessage ->
            if (errorMessage != null) {
                tvErrorMessage.visibility = View.VISIBLE
                tvErrorMessage.text = errorMessage
            } else {
                tvErrorMessage.visibility = View.GONE
            }
        })

        // Observar estado de carga
        viewModel.isLoading.observe(this, { loading ->
            isLoading = loading
        })

        // Agregar ScrollListener para paginación
        rvResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Verificar si el usuario llegó al final de la lista
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    isLoading = true
                    viewModel.loadMoreData(null, null, null) // Cargar más datos
                }
            }
        })
    }

    private fun mostrarEstadisticas(stats: String) {
        tvStats.visibility = View.VISIBLE
        tvStats.text = stats
    }
}