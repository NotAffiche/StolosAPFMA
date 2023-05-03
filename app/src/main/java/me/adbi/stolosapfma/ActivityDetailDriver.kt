package me.adbi.stolosapfma

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
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
import okhttp3.Interceptor
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class ActivityDetailDriver : ComponentActivity() {

    private val BASE_URL: String = "https://affiche.me:7144"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_driver)

        val aLogger = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.i("STOLOS HTTP", message)
            }
        })

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
            .client(okHttpClient)//okHttpClient defined in ACCEPT_SPECIFIC_TRUSTED_CERTIFICATE//.client(okHttpClient)//okHttpClient defined in IGNORE_UNTRUSTED_HTTPS
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api: ApiService = retrofit.create(ApiService::class.java)

        //region TEXT VIEWS
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
        //endregion

        //region EDIT TEXTS
        val evFirstName = findViewById<EditText>(R.id.evFirstName)
        val evLastName = findViewById<EditText>(R.id.evLastName)
        val evBirthDate = findViewById<EditText>(R.id.evBirthDate)
        val evRRN = findViewById<EditText>(R.id.evRRN)
        val evLicenses = findViewById<EditText>(R.id.evLicenses)
        val evAddress = findViewById<EditText>(R.id.evAddress)
        val evVehicle = findViewById<EditText>(R.id.evVehicle)
        val evGasCard = findViewById<EditText>(R.id.evGasCard)
        //endregion

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        val driverId: Int = intent.getIntExtra("driverID", 0)

        if (driverId!=0) {
            btnDelete.setVisibility(View.VISIBLE)
            api.getDriverById(driverId).enqueue(object : Callback<DriverModel> {
                override fun onResponse(call: Call<DriverModel>, response: Response<DriverModel>) {
                    if (response.isSuccessful) {
                        Log.i("SUCCESS GET DETAIL", response.body().toString())
                        //region FILL EDIT TEXTS
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
                        //endregion

                        //region REGISTER UPDATE
                        btnSave.setOnClickListener(View.OnClickListener {
                            var newD: DriverModel = DriverModel(driverId, evFirstName.text.toString(), evLastName.text.toString(), evBirthDate.text.toString(),
                            evRRN.text.toString(), evLicenses.text.toString().split(","), evAddress.text.toString(), evVehicle.text.toString(), evGasCard.text.toString())
                            val call: Call<Void> = api.updateDriverById(newD.driverID!!, newD)
                            call.enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    Log.i("SUCCESS UPDATE DETAIL ${driverId}", response.code().toString())
                                    Log.i("SUCCESS UPDATE DETAIL ${driverId}", response.body().toString())
                                    Log.i("SUCCESS UPDATE DETAIL ${driverId}", response.message().toString())
                                    Log.i("SUCCESS UPDATE DETAIL ${driverId}", response.headers().toString())
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("ERROR", t.message.toString())
                                }
                            })
                        })
                        //endregion

                        //region REGISTER DELETE
                        btnDelete.setOnClickListener {
                            val client = OkHttpClient.Builder()
                            /*
                            val call: Call<Unit> = api.deleteDriverById(driverId)
                            call.enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    if (response.isSuccessful) {
                                        Log.i("SUCCESS DELETE ${driverId}", response.body().toString())
                                    } else {
                                        Log.e("FAIL DELETE ${driverId}", response.code().toString())
                                        Log.e("FAIL DELETE ${driverId}", response.body().toString())
                                        Log.e("FAIL DELETE ${driverId}", response.message().toString())
                                        Log.e("FAIL DELETE ${driverId}", response.headers().toString())
                                    }
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Log.e("ERROR", t.message.toString())
                                }
                            })
                            */
                        }
                        //endregion
                    }
                }

                override fun onFailure(call: Call<DriverModel>, t: Throwable) {
                    Toast.makeText(this@ActivityDetailDriver, "Error", Toast.LENGTH_SHORT)
                    Log.e("error", t.message.toString())
                }
            })
        } else {// code for creating new driver
            btnDelete.setVisibility(View.GONE)
            //region REGISTER ADD

            btnSave.setOnClickListener(View.OnClickListener {
                var licenses = ArrayList<String>()
                for (l in evLicenses.text.toString().split(",")) {
                    licenses.add("$l")
                }
                val newD = DriverModel(driverId, evFirstName.text.toString(), evLastName.text.toString(), evBirthDate.text.toString(),
                    evRRN.text.toString(), licenses, evAddress.text.toString(), evVehicle.text.toString(), evGasCard.text.toString())
                Log.i("INFO TEST LOG", newD.toString())
            })
            //endregion
        }
    }
}