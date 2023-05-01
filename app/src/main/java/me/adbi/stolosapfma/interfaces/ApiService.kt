package me.adbi.stolosapfma.interfaces

import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.VehicleModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("Driver")
    fun getDrivers(): Call<ArrayList<DriverModel>>

    @GET("Vehicle")
    fun getVehicles(): Call<ArrayList<VehicleModel>>
}