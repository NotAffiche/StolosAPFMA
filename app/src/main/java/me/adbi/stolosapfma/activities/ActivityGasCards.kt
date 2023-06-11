package me.adbi.stolosapfma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.adapters.GasCardAdapter
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.GasCardModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityGasCards : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gascards)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ActivityGasCards, MainActivity::class.java))
            }
        })

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        val deletedInfo: String? = intent.getStringExtra("DELETED-INFO")
        if (!deletedInfo.isNullOrBlank()) {
            Toast.makeText(this, "$deletedInfo deleted.", Toast.LENGTH_SHORT).show()
        }

        val rv: RecyclerView = findViewById(R.id.rvGasCards)

        val btnAddGasCard: Button = findViewById(R.id.btnAddGasCard)
        btnAddGasCard.setOnClickListener{
            startActivity(Intent(this@ActivityGasCards, ActivityDetailGasCard::class.java).putExtra("gasCardNum", ""))
        }

        api.getGasCards().enqueue(object : Callback<ArrayList<GasCardModel>> {
            override fun onResponse(call: Call<ArrayList<GasCardModel>>, response: Response<ArrayList<GasCardModel>>) {
                if(response.isSuccessful) {
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityGasCards)
                        adapter = GasCardAdapter(this@ActivityGasCards, response.body()!!)
                    }
                } else {
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityGasCards)
                        adapter = null
                        View.inflate(context, R.layout.item_loading, this)
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<GasCardModel>>, t: Throwable) {
                Toast.makeText(this@ActivityGasCards, "Error", Toast.LENGTH_SHORT).show()
                Log.e("error", t.message.toString())
            }
        })
    }
}