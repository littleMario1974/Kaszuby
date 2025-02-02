package com.example.kaszuby

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Inicjalizacja Firebase
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicjalizacja mapy
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Przyciski logowania (umieszczamy go w rogu mapy)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            // Uruchamia ekran logowania, jeśli użytkownik chce się zalogować
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Ustawienie lokalizacji początkowej mapy
        val kaszuby = LatLng(54.35, 18.20) // Przykładowa lokalizacja
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kaszuby, 10f))

        // Dodanie markerów z Firestore
        db.collection("points") // Kolekcja "points" w Firestore
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshots = task.result
                    for (document in documentSnapshots!!) {
                        // Poprawne wykorzystanie QueryDocumentSnapshot
                        val latitude = document.getDouble("latitude")
                        val longitude = document.getDouble("longitude")
                        val description = document.getString("description")

                        if (latitude != null && longitude != null) {
                            val point = LatLng(latitude, longitude)
                            mMap.addMarker(MarkerOptions().position(point).title(description))
                        }
                    }
                } else {
                    Toast.makeText(this, "Błąd ładowania punktów.", Toast.LENGTH_SHORT).show()
                }
            }

        // Dodanie kliknięcia na mapie (dodawanie nowych punktów)
        mMap.setOnMapClickListener { latLng ->
            if (mAuth.currentUser != null) {
                // Dodaj marker tylko, jeśli użytkownik jest zalogowany
                mMap.addMarker(MarkerOptions().position(latLng).title("Nowy punkt"))

                // Zapis nowego punktu w Firestore
                val point = Point(latLng.latitude, latLng.longitude, "Nowy punkt")
                db.collection("points")
                    .add(point)
                    .addOnSuccessListener { _ ->
                        // Dodatkowa logika po zapisaniu punktu (np. pokazanie komunikatu)
                    }
            } else {
                // Jeśli użytkownik nie jest zalogowany, wyświetl komunikat
                Toast.makeText(this, "Musisz być zalogowany, aby dodać punkty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



