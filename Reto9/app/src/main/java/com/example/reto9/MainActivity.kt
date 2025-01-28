package com.example.reto9

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvRadius: TextView
    private lateinit var seekBarRadius: SeekBar
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var currentRadius: Int = 5 // Radio inicial en km
    private var myLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de OSMDroid
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Referencias a los controles de la interfaz
        tvRadius = findViewById(R.id.tvRadius)
        seekBarRadius = findViewById(R.id.seekBarRadius)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        // Configurar SeekBar
        seekBarRadius.progress = currentRadius
        tvRadius.text = "Radio de búsqueda: $currentRadius km"
        seekBarRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentRadius = progress
                tvRadius.text = "Radio de búsqueda: $currentRadius km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar botón de búsqueda
        btnSearch.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                getLastLocation()
            }
        }

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener { location ->
            if (location != null) {
                val startPoint = GeoPoint(location.latitude, location.longitude)
                mapView.controller.setCenter(startPoint)
                mapView.controller.setZoom(15.0)
                addMarker(startPoint, "Mi ubicación")
                getNearbyPlaces(location.latitude, location.longitude, currentRadius * 1000.0) // Convertir km a metros
            }
        })
    }

    private fun addMarker(point: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = point
        marker.title = title

        // Si es el marcador de "Mi ubicación", lo guardamos en la variable
        if (title == "Mi ubicación") {
            myLocationMarker = marker
        }

        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun getNearbyPlaces(lat: Double, lon: Double, radius: Double) {
        val overpassUrl = "https://overpass-api.de/api/interpreter"
        val query = """
        [out:json];
        node(around:$radius,$lat,$lon)[amenity=hospital];
        node(around:$radius,$lat,$lon)[tourism=attraction];
        node(around:$radius,$lat,$lon)[amenity=restaurant];
        out;
    """.trimIndent()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$overpassUrl?data=${URLEncoder.encode(query, "UTF-8")}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    println("Respuesta de la API: $jsonData")
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val elements = jsonObject.getJSONArray("elements")
                        runOnUiThread {
                            // Limpiar los marcadores de los puntos de interés sin afectar "Mi ubicación"
                            mapView.overlays.clear()
                            if (myLocationMarker != null) {
                                mapView.overlays.add(myLocationMarker) // Reagregar "Mi ubicación"
                            }
                        }
                        for (i in 0 until elements.length()) {
                            val element = elements.getJSONObject(i)
                            val lat = element.getDouble("lat")
                            val lon = element.getDouble("lon")
                            val tags = element.getJSONObject("tags")
                            val name = tags.optString("name", "Sin nombre")
                            val type = when {
                                tags.has("amenity") -> tags.getString("amenity")
                                tags.has("tourism") -> tags.getString("tourism")
                                else -> "Desconocido"
                            }
                            val point = GeoPoint(lat, lon)
                            runOnUiThread { addMarker(point, "$name ($type)") }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    println("Error en la respuesta de la API: ${response.code}")
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
}
