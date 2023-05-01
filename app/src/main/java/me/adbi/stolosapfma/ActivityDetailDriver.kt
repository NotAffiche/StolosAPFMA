package me.adbi.stolosapfma

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.adapters.DriverAdapter
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.DriverModel
import okhttp3.OkHttpClient
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


class ActivityDetailDriver : ComponentActivity() {

    private val BASE_URL: String = "https://affiche.me:7144"//"https://jsonplaceholder.typicode.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_driver)

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

        api.getDriverById(intent.getIntExtra("driverID", 0)).enqueue(object : Callback<DriverModel> {
            override fun onResponse(call: Call<DriverModel>, response: Response<DriverModel>) {
                if (response.isSuccessful) {
                    Log.i("succuess", response.body().toString())
                    Log.i("success", intent.getIntExtra("driverID", 0).toString())
                    //
                    val tvFirstName = findViewById<TextView>(R.id.tvFirstName)
                    tvFirstName.text = "First Name:"
                    val tvLastName = findViewById<TextView>(R.id.tvLastName)
                    tvLastName.text = "Last Name:"
                    val tvBirthDate = findViewById<TextView>(R.id.tvBirthDate)
                    tvBirthDate.text = "Birth Date:"
                    val tvRRN = findViewById<TextView>(R.id.tvRRN)
                    tvRRN.text = "RRN:"
                    val tvLicenses = findViewById<TextView>(R.id.tvLicenses)
                    tvLicenses.text = "Licenses:"
                    val tvAddress = findViewById<TextView>(R.id.tvAddress)
                    tvAddress.text = "Address:"
                    val tvVehicle = findViewById<TextView>(R.id.tvVehicle)
                    tvVehicle.text = "Vehicle VIN:"
                    val tvGasCard = findViewById<TextView>(R.id.tvGasCard)
                    tvGasCard.text = "GC Num:"
                    //
                    val evFirstName = findViewById<EditText>(R.id.evFirstName)
                    val evLastName = findViewById<EditText>(R.id.evLastName)
                    val evBirthDate = findViewById<EditText>(R.id.evBirthDate)
                    val evRRN = findViewById<EditText>(R.id.evRRN)
                    val evLicenses = findViewById<EditText>(R.id.evLicenses)
                    val evAddress = findViewById<EditText>(R.id.evAddress)
                    val evVehicle = findViewById<EditText>(R.id.evVehicle)
                    val evGasCard = findViewById<EditText>(R.id.evGasCard)

                    val d: DriverModel = response.body()!!
                    evFirstName.text = Editable.Factory.getInstance().newEditable(d.firstName)
                    evLastName.text = Editable.Factory.getInstance().newEditable(d.lastName)
                    evBirthDate.text = Editable.Factory.getInstance().newEditable(d.birthDate.split("T")[0])
                    evRRN.text = Editable.Factory.getInstance().newEditable(d.natRegNum)
                    evLicenses.text = Editable.Factory.getInstance().newEditable(d.licenses.joinToString(prefix = "[", separator = ",", postfix = "]"))
                    evAddress.text = Editable.Factory.getInstance().newEditable("")
                    evVehicle.text = Editable.Factory.getInstance().newEditable("")
                    evAddress.text = Editable.Factory.getInstance().newEditable("")
                    if (d.address!=null) {
                        evAddress.text = Editable.Factory.getInstance().newEditable(d.address)
                    }
                    if (d.vehicleVin!=null) {
                        evVehicle.text = Editable.Factory.getInstance().newEditable(d.vehicleVin)
                    }
                    if (d.gasCardNum!=null) {
                        evGasCard.text = Editable.Factory.getInstance().newEditable(d.gasCardNum)
                    }
                }
            }

            override fun onFailure(call: Call<DriverModel>, t: Throwable) {
                Toast.makeText(this@ActivityDetailDriver, "Error", Toast.LENGTH_SHORT)
                Log.e("error", t.message.toString())
            }
        })
    }
}