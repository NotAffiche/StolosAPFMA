package me.adbi.stolosapfma.factories

import android.content.Context
import okhttp3.OkHttpClient
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

class RetrofitFactory(context: Context) {
    private val BASE_URL: String = "https://affiche.me:7144"
    private val ctx: Context = context

    fun Retrofit(): Retrofit {

        //region ACCEPT_SPECIFIC_TRUSTED_CERTIFICATE

        fun readCertificateFromFile(filePath: InputStream): Certificate {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificate = certificateFactory.generateCertificate(filePath)
            filePath.close()
            return certificate
        }

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        //val certificate = readCertificateFromFile(assets.open("localhost.pem"))
        val certificate = readCertificateFromFile(ctx.assets.open("localhost.pem"))
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
            .build()
        */
        //endregion

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)//okHttpClient defined in ACCEPT_SPECIFIC_TRUSTED_CERTIFICATE//.client(okHttpClient)//okHttpClient defined in IGNORE_UNTRUSTED_HTTPS
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}