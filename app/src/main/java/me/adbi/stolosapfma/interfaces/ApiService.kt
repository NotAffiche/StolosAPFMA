package me.adbi.stolosapfma.interfaces

import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.GasCardModel
import me.adbi.stolosapfma.models.VehicleModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    //region GET ALL
    @GET("Driver")
    fun getDrivers(): Call<ArrayList<DriverModel>>
    @GET("Vehicle")
    fun getVehicles(): Call<ArrayList<VehicleModel>>
    @GET("GasCard")
    fun getGasCards(): Call<ArrayList<GasCardModel>>
    //endregion

    //region GET SPECIFIC
    @GET("Driver/{id}")
    fun getDriverById(@Path("id") driverId: Int): Call<DriverModel>
    @GET("Vehicle/{vin}")
    fun getVehicleByVIN(@Path("vin") vin: String): Call<VehicleModel>
    @GET("GasCard/{cardNum}")
    fun getGasCardByCardNum(@Path("cardNum") cardNum: String): Call<GasCardModel>
    //endregion

    //region POST
    @POST("Driver")
    fun addDriver(@Body driverModel: DriverModel): Call<Void>
    //endregion

    //region PUT
    @PUT("Driver/{id}")
    fun updateDriverById(@Path("id") driverId: Int, @Body driverModel: DriverModel): Call<Void>
    //endregion

    //region DELETE
    @DELETE("Driver/{id}")
    fun deleteDriverById(@Path("id") driverId: Int): Call<Unit>
    //endregion
}