package me.adbi.stolosapfma.models

data class DriverModel (
    val driverID: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val natRegNum: String,
    val licenses: Array<String>,
    val address: String?,

    val vehicleVin: String?,
    val gasCardNum: String?
)