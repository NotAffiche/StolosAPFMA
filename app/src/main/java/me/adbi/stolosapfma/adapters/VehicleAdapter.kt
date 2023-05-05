package me.adbi.stolosapfma.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.ActivityDetailVehicle
import me.adbi.stolosapfma.R
import me.adbi.stolosapfma.models.VehicleModel

class VehicleAdapter(val context: Context, objects:ArrayList<VehicleModel>) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

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
        private val tvVehicle: TextView = itemView.findViewById(R.id.tvVehicle)
        fun bindView(v: VehicleModel) {
            tvVehicle.text = "${v.brandModel} - ${v.licensePlate}"
            itemView.setOnClickListener{
                context.startActivity(Intent(context, ActivityDetailVehicle::class.java).putExtra("vehicleVin", v.vin))
            }
        }
    }
}