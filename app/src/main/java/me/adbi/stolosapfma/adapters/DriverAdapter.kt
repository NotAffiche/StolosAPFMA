package me.adbi.stolosapfma.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.adbi.stolosapfma.ActivityDetailDriver
import me.adbi.stolosapfma.R
import me.adbi.stolosapfma.models.DriverModel

class DriverAdapter(var context: Context, objects:ArrayList<DriverModel>) : RecyclerView.Adapter<DriverAdapter.DriverViewHolder>() {

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
        fun bindView(d: DriverModel) {
            tvName.text = "${d.firstName} ${d.lastName}"
            itemView.setOnClickListener(OnClickListener {
                Log.d("TAG", "CLICKED ON ${d.driverID} - ${d.firstName} - ${d.lastName}")
                context.startActivity(Intent(context, ActivityDetailDriver::class.java).putExtra("driverID", d.driverID))
            })
        }
    }
}