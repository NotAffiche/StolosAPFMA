package me.adbi.stolosapfma

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.adapters.PostAdapter
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.PostModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivityDrivers : ComponentActivity() {

    private val BASE_URL: String = "https://jsonplaceholder.typicode.com"//"https://localhost:7144/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drivers)

        val rv: RecyclerView = findViewById<RecyclerView>(R.id.rvDrivers)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val api: ApiService = retrofit.create(ApiService::class.java)

        getData(rv, api.getPosts())
    }

    fun getData(rv:RecyclerView, callAL: Call<ArrayList<PostModel>>) {
        callAL.enqueue(object : Callback<ArrayList<PostModel>> {
            override fun onResponse(call: Call<ArrayList<PostModel>>, response: Response<ArrayList<PostModel>>) {
                if(response.isSuccessful) {
                    Log.e("success", response.body().toString())
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityDrivers)
                        adapter = PostAdapter(response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<PostModel>>, t: Throwable) {
                Log.e("error", t.message.toString())
            }
        })
    }
}