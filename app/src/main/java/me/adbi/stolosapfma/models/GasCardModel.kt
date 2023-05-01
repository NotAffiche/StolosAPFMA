package me.adbi.stolosapfma.models

data class GasCardModel(
    val cardNumber: String,
    val expiringDate: String,
    val pincode: Int?,
    val fuelTypes: Array<String>,
    val blocked: Boolean,

    val driverId: Int?
)