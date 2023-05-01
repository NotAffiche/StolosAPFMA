package me.adbi.stolosapfma.models

data class VehicleModel(
    val vin: String,
    val brandModel: String,
    val licensePlate: String,
    val fuelType: String,
    val vehicleType: String,
    val color: String?,
    val doors: Int?,

    val driverId: Int?
)