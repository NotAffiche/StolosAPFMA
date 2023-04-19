package me.adbi.stolosapfma.models

import java.util.Date

data class DriverModel (
    val driverID: Int? = 0,
    val firstName: String? = "",
    val lastName: String? = "",
    val birthDate: Date?,
    val natRegNum: String? = "",
    val licenses: IntArray?,
    val address: String? = ""
)