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
    @GET("api/drivers")
    fun getDrivers(): Call<ArrayList<DriverModel>>
    @GET("api/vehicles")
    fun getVehicles(): Call<ArrayList<VehicleModel>>
    @GET("api/gascards")
    fun getGasCards(): Call<ArrayList<GasCardModel>>
    //endregion

    //region GET SPECIFIC
    @GET("api/drivers/{id}")
    fun getDriverById(@Path("id") driverId: Int): Call<DriverModel>
    @GET("api/vehicles/{vin}")
    fun getVehicleByVIN(@Path("vin") vin: String): Call<VehicleModel>
    @GET("api/gascards/{cn}")
    fun getGasCardByCardNum(@Path("cn") cardNum: String): Call<GasCardModel>
    //endregion

    //region POST
    @POST("api/drivers")
    fun addDriver(@Body driverModel: DriverModel): Call<Unit>
    @POST("api/vehicles")
    fun addVehicle(@Body vehicleModel: VehicleModel): Call<Unit>
    @POST("api/gascards")
    fun addGasCard(@Body gasCardModel: GasCardModel): Call<Unit>
    //endregion

    //region PUT
    @PUT("api/drivers")
    fun updateDriverById(@Body driverModel: DriverModel): Call<Unit>
    @PUT("api/vehicles")
    fun updateVehicleByVin(@Body vehicleModel: VehicleModel): Call<Unit>
    @PUT("api/gascards")
    fun updateGasCardByCardNum(@Body gasCardModel: GasCardModel): Call<Unit>
    //endregion

    //region DELETE
    @DELETE("api/drivers/{id}")
    fun deleteDriverById(@Path("id") driverId: Int): Call<Unit>
    @DELETE("api/vehicles/{vin}")
    fun deleteVehicleByVin(@Path("vin") vin: String): Call<Unit>
    @DELETE("api/gascards/{cn}")
    fun deleteGasCardByCardNum(@Path("cn") cn: String): Call<Unit>
    //endregion
}