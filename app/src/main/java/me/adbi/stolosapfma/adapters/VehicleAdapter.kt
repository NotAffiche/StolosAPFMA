package me.adbi.stolosapfma.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.R
import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.VehicleModel

class VehicleAdapter(objects:ArrayList<VehicleModel>) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    private val vehicles = objects

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vehicles.size
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle: VehicleModel = vehicles[position]
        return holder.bindView(vehicle)
    }

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvVin: TextView = itemView.findViewById(R.id.tvVin)
        private val tvLicensePlate: TextView = itemView.findViewById(R.id.tvLicensePlate)
        private val tvBrandModel: TextView = itemView.findViewById(R.id.tvBrandModel)
        private val tvFuelType: TextView = itemView.findViewById(R.id.tvFuelType)
        private val tvVehicleType: TextView = itemView.findViewById(R.id.tvVehicleType)
        private val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        private val tvDoors: TextView = itemView.findViewById(R.id.tvDoors)
        private val tvDriver: TextView = itemView.findViewById(R.id.tvDriver)

        fun bindView(v: VehicleModel) {
            tvVin.text = "${v.vin}"
            tvLicensePlate.text = "${v.licensePlate}"
            tvBrandModel.text = "${v.brandModel}"
            tvFuelType.text = "${v.fuelType}"
            tvVehicleType.text = "${v.vehicleType}"
            tvColor.text = "Unknown color"
            tvDoors.text = "Unknown door amount"
            tvDriver.text = "No driver"
            if (v.color!=null) {
                tvColor.text = "${v.color}"
            }
            if (v.doors!=null) {
                tvDoors.text = "${v.doors.toString()}"
            }
            if (v.driverId!=null) {
                tvDriver.text = "${v.driverId.toString()}"
            }

            itemView.setOnClickListener(OnClickListener {
                Log.d("TAG", "CLICKED ON ${v.vin} - ${v.licensePlate}")
            })
        }
    }
}