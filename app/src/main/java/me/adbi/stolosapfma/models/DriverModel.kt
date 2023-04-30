package me.adbi.stolosapfma.models

import java.util.Date

/*data class DriverModel (
    val driverID: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val natRegNum: String,
    val licenses: List<String>,
    val address: String? = "",

    val vin: String? = "",
    val gasCardNum: String? = ""
)*/
data class DriverModel (
    val firstName: String,
    val lastName: String
)