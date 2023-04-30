package me.adbi.stolosapfma

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.adapters.DriverAdapter
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.DriverModel
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class ActivityDrivers : ComponentActivity() {

    private val BASE_URL: String = "https://affiche.me:7144"//"https://jsonplaceholder.typicode.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drivers)

        val rv: RecyclerView = findViewById<RecyclerView>(R.id.rvDrivers)

        //
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
        //

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiService = retrofit.create(ApiService::class.java)

        api.getDrivers().enqueue(object : Callback<ArrayList<DriverModel>> {
            override fun onResponse(call: Call<ArrayList<DriverModel>>, response: Response<ArrayList<DriverModel>>) {
                if(response.isSuccessful) {
                    Log.e("success", response.body().toString())
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityDrivers)
                        adapter = DriverAdapter(response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<DriverModel>>, t: Throwable) {
                Log.e("error", t.message.toString())
            }
        })
    }
}