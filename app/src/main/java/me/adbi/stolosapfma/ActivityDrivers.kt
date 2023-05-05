package me.adbi.stolosapfma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.adapters.DriverAdapter
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.DriverModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityDrivers : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drivers)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ActivityDrivers, MainActivity::class.java))
            }
        })

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        val deletedInfo: String? = intent.getStringExtra("DELETED-INFO")
        if (!deletedInfo.isNullOrBlank()) {
            Toast.makeText(this, "$deletedInfo deleted.", Toast.LENGTH_SHORT)
        }

        val rv: RecyclerView = findViewById(R.id.rvDrivers)

        val btnAddDriver: Button = findViewById(R.id.btnAddDriver)
        btnAddDriver.setOnClickListener{
            startActivity(Intent(this@ActivityDrivers, ActivityDetailDriver::class.java).putExtra("driverID", 0))
        }

        api.getDrivers().enqueue(object : Callback<ArrayList<DriverModel>> {
            override fun onResponse(call: Call<ArrayList<DriverModel>>, response: Response<ArrayList<DriverModel>>) {
                if(response.isSuccessful) {
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityDrivers)
                        adapter = DriverAdapter(context = context, response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<DriverModel>>, t: Throwable) {
                Log.e("ADBILOGSTOLOS", t.message.toString())
            }
        })
    }
}