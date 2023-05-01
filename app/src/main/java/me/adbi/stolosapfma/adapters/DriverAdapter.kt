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

class DriverAdapter(objects:ArrayList<DriverModel>) : RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

    private val drivers = objects

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun getItemCount(): Int {
        return drivers.size
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver: DriverModel = drivers[position]
        return holder.bindView(driver)
    }

    inner class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvBirthDate: TextView = itemView.findViewById(R.id.tvBirthDate)
        private val tvRRN: TextView = itemView.findViewById(R.id.tvRRN)
        private val tvLicenses: TextView = itemView.findViewById(R.id.tvLicenses)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvVehicle: TextView = itemView.findViewById(R.id.tvVehicle)
        private val tvGasCard: TextView = itemView.findViewById(R.id.tvGasCard)
        fun bindView(d: DriverModel) {
            tvName.text = "${d.driverID} ${d.firstName} ${d.lastName}"
            tvBirthDate.text = "${d.birthDate.split("T")[0]}"
            tvRRN.text = "${d.natRegNum}"
            tvLicenses.text = "${d.licenses.joinToString(prefix = "[", separator = ",", postfix = "]")}"
            tvAddress.text = "No Address"
            tvVehicle.text = "No Vehicle"
            tvGasCard.text = "No GasCard"
            if (d.address!=null) {
                tvAddress.text = "${d.address}"
            }
            if (d.vehicleVin!=null) {
                tvVehicle.text = "${d.vehicleVin}"
            }
            if (d.gasCardNum!=null) {
                tvGasCard.text = "${d.gasCardNum}"
            }

            itemView.setOnClickListener(OnClickListener {
                Log.d("TAG", "CLICKED ON ${d.driverID} - ${d.firstName} - ${d.lastName}")
            })
        }
    }
}