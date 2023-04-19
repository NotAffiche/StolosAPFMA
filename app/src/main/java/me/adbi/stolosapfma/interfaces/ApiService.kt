package me.adbi.stolosapfma.interfaces

import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.PostModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("Driver")
    fun getDrivers(): Call<ArrayList<DriverModel>>
    @GET("posts")
    fun getPosts(): Call<ArrayList<PostModel>>
}