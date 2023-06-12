package me.adbi.stolosapfma.models

data class DriverModel (
    val driverID: Int?,
    var firstName: String,
    val lastName: String,
    val birthDate: String,
    val natRegNum: String,
    val licenses: List<String>,
    val address: String? = null,

    val vehicleVin: String? = null,
    val gasCardNum: String? = null
)

{
    override fun toString(): String {
        return if (driverID == null && firstName.isEmpty() && lastName.isEmpty()) {
            "Unset"
        } else {
            "$firstName $lastName $vehicleVin $gasCardNum"
        }
    }
}