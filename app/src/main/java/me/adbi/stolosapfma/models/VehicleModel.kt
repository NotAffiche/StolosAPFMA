package me.adbi.stolosapfma.models

data class VehicleModel(
    val vin: String?,
    val brandModel: String,
    val licensePlate: String,
    val fuelType: String,
    val vehicleType: String,
    val color: String? = null,
    val doors: Int? = null,

    val driverId: Int? = null
)

{
    override fun toString(): String {
        return if (vin.isNullOrEmpty() && brandModel.isEmpty() && licensePlate.isEmpty()) {
            "Unset"
        } else {
            "${brandModel} ${licensePlate}"
        }
    }
}