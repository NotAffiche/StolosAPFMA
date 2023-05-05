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
import me.adbi.stolosapfma.adapters.VehicleAdapter
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.VehicleModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityVehicles : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicles)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ActivityVehicles, MainActivity::class.java))
            }
        })

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        val deletedInfo: String? = intent.getStringExtra("DELETED-INFO")
        if (!deletedInfo.isNullOrBlank()) {
            Toast.makeText(this, "$deletedInfo deleted.", Toast.LENGTH_SHORT)
        }

        val rv: RecyclerView = findViewById(R.id.rvVehicles)

        val btnAddVehicle: Button = findViewById(R.id.btnAddVehicle)
        btnAddVehicle.setOnClickListener{
            startActivity(Intent(this@ActivityVehicles, ActivityDetailVehicle::class.java).putExtra("vehicleVin", ""))
        }

        api.getVehicles().enqueue(object : Callback<ArrayList<VehicleModel>> {
            override fun onResponse(call: Call<ArrayList<VehicleModel>>, response: Response<ArrayList<VehicleModel>>) {
                if(response.isSuccessful) {
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityVehicles)
                        adapter = VehicleAdapter(context = context,response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<VehicleModel>>, t: Throwable) {
                Log.e("ADBILOGSTOLOS", t.message.toString())
            }
        })
    }
}