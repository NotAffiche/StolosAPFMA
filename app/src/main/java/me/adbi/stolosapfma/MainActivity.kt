package me.adbi.stolosapfma

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import me.adbi.stolosapfma.interfaces.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishActivity(-1)
            }
        })

        val btnDrivers = findViewById<Button>(R.id.btnDrivers)
        val btnVehicles = findViewById<Button>(R.id.btnVehicles)
        val btnFuelCards = findViewById<Button>(R.id.btnFuelCards)

        btnDrivers.setOnClickListener {
            Toast.makeText(this, "Drivers", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ActivityDrivers::class.java))
        }
        btnVehicles.setOnClickListener {
            Toast.makeText(this, "Vehicles", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ActivityVehicles::class.java))
        }
        btnFuelCards.setOnClickListener {
            Toast.makeText(this, "Fuel cards", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ActivityGasCards::class.java))
        }
    }
}