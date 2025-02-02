package com.example.kaszuby

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.IdpResponse

class LoginActivity : AppCompatActivity() {

    private val loginResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Logowanie powiodło się
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "Zalogowano jako: ${user?.email}", Toast.LENGTH_SHORT).show()
                // Przechodzimy do MapsActivity po udanym logowaniu
                navigateToMapsActivity()
            } else {
                // Obsługuje błąd logowania
                Toast.makeText(this, "Logowanie nie powiodło się.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Jeśli masz odpowiedni layout

        // Sprawdzamy, czy użytkownik jest już zalogowany
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Jeśli użytkownik jest zalogowany, przechodzimy do MapsActivity
            navigateToMapsActivity()
        } else {
            // Uruchamiamy proces logowania, jeśli użytkownik nie jest zalogowany
            launchLoginActivity()
        }
    }

    // Funkcja do uruchomienia ekranu logowania
    private fun launchLoginActivity() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        loginResultLauncher.launch(signInIntent)
    }

    // Funkcja przechodząca do MapsActivity po udanym logowaniu
    private fun navigateToMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
        finish() // Zakończenie LoginActivity, aby użytkownik nie wrócił do niego po kliknięciu przycisku "Back"
    }
}
