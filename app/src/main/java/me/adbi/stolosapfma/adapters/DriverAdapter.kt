package me.adbi.stolosapfma.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.R
import me.adbi.stolosapfma.models.DriverModel

class DriverAdapter(private val driverModel:ArrayList<DriverModel>) :RecyclerView.Adapter<DriverViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_driver, parent, false)
        return DriverViewHolder(view)
    }

    override fun getItemCount(): Int {
        return driverModel.size
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        return holder.bindView(driverModel[position])
    }
}

class DriverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvName: TextView = itemView.findViewById(R.id.tvName)

    fun bindView(driverModel: DriverModel) {
        tvName.text = driverModel.firstName + " " + driverModel.lastName
    }

}