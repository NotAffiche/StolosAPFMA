package me.adbi.stolosapfma.interfaces

import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.GasCardModel
import me.adbi.stolosapfma.models.VehicleModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("Driver")
    fun getDrivers(): Call<ArrayList<DriverModel>>
    @GET("Vehicle")
    fun getVehicles(): Call<ArrayList<VehicleModel>>
    @GET("GasCard")
    fun getGasCards(): Call<ArrayList<GasCardModel>>

    @GET("Driver/{id}")
    fun getDriverById(@Path("id") driverId: Int): Call<DriverModel>
}