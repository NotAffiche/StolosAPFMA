package me.adbi.stolosapfma.models

data class GasCardModel(
    val cardNumber: String?,
    val expiringDate: String,
    val pincode: Int? = null,
    val fuelTypes: List<String>,
    val blocked: Boolean,

    val driverId: Int? = null
)

{
    override fun toString(): String {
        return cardNumber ?: "Unset"
    }
}