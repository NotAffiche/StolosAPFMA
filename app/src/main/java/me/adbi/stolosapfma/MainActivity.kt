package me.adbi.stolosapfma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val BASE_URL: String = "https://jsonplaceholder.typicode.com"//"https://localhost:7144/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDrivers = findViewById<Button>(R.id.btnDrivers)
        val btnVehicles = findViewById<Button>(R.id.btnVehicles)
        val btnFuelCards = findViewById<Button>(R.id.btnFuelCards)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val api: ApiService = retrofit.create(ApiService::class.java)

        btnDrivers.setOnClickListener {
            Toast.makeText(this, "Drivers", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ActivityDrivers::class.java))
        }
        btnVehicles.setOnClickListener {
            Toast.makeText(this, "Vehicles", Toast.LENGTH_SHORT).show()
        }
        btnFuelCards.setOnClickListener {
            Toast.makeText(this, "Fuel cards", Toast.LENGTH_SHORT).show()
        }
    }
}