package me.adbi.stolosapfma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.adapters.DriverAdapter
import me.adbi.stolosapfma.adapters.VehicleAdapter
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.VehicleModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class ActivityVehicles : ComponentActivity() {

    private val BASE_URL: String = "https://affiche.me:7144"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicles)

        val deletedInfo: String? = intent.getStringExtra("DELETED-INFO")
        if (!deletedInfo.isNullOrBlank()) {
            Toast.makeText(this, "$deletedInfo deleted.", Toast.LENGTH_SHORT)
        }

        val rv: RecyclerView = findViewById<RecyclerView>(R.id.rvVehicles)

        //region ACCEPT_SPECIFIC_TRUSTED_CERTIFICATE
        fun readCertificateFromFile(filePath: InputStream): Certificate {
            //val file = File(filePath)
            //val inputStream = FileInputStream(file)
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificate = certificateFactory.generateCertificate(filePath)
            filePath.close()
            return certificate
        }

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        val certificate = readCertificateFromFile(assets.open("localhost.pem"))
        keyStore.setCertificateEntry("server_cert", certificate)

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        val trustManagers = trustManagerFactory.trustManagers
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)

        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
            .hostnameVerifier{_,_ -> true}
            .build()
        //endregion

        //region IGNORE_UNTRUSTED_HTTPS
        /*
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
            .addInterceptor(aLogger)
            .build()
        */
        //endregion

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) //okHttpClient defined in IGNORE_UNTRUSTED_HTTPS
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiService = retrofit.create(ApiService::class.java)

        val btnAddDriver: Button = findViewById(R.id.btnAddVehicle)
        btnAddDriver.setOnClickListener{
            startActivity(Intent(this@ActivityVehicles, ActivityDetailVehicle::class.java).putExtra("vehicleVin", ""))
        }

        api.getVehicles().enqueue(object : Callback<ArrayList<VehicleModel>> {
            override fun onResponse(call: Call<ArrayList<VehicleModel>>, response: Response<ArrayList<VehicleModel>>) {
                if(response.isSuccessful) {
                    Log.i("success", response.body().toString())
                    rv.apply {
                        layoutManager = LinearLayoutManager(this@ActivityVehicles)
                        adapter = VehicleAdapter(context = context,response.body()!!)
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<VehicleModel>>, t: Throwable) {
                Log.e("error", t.message.toString())
            }
        })
    }
}