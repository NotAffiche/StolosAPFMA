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
import me.adbi.stolosapfma.models.GasCardModel

class GasCardAdapter(objects:ArrayList<GasCardModel>) : RecyclerView.Adapter<GasCardAdapter.GasCardViewHolder>() {

    private val gasCards = objects

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GasCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_gascard, parent, false)
        return GasCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gasCards.size
    }

    override fun onBindViewHolder(holder: GasCardViewHolder, position: Int) {
        val gc: GasCardModel = gasCards[position]
        return holder.bindView(gc)
    }

    inner class GasCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCardNumber: TextView = itemView.findViewById(R.id.tvCardNumber)
        private val tvExpiringDate: TextView = itemView.findViewById(R.id.tvExpiringDate)
        private val tvPin: TextView = itemView.findViewById(R.id.tvPin)
        private val tvFuelTypes: TextView = itemView.findViewById(R.id.tvFuelTypes)
        private val tvBlocked: TextView = itemView.findViewById(R.id.tvBlocked)
        private val tvDriver: TextView = itemView.findViewById(R.id.tvDriver)
        fun bindView(gc: GasCardModel) {
            tvCardNumber.text = "${gc.cardNumber}"
            tvExpiringDate.text = "${gc.expiringDate.split("T")[0]}"
            tvPin.text = "No pin"
            tvFuelTypes.text = "${gc.fuelTypes.joinToString(prefix = "[", separator = ",", postfix = "]")}"
            tvBlocked.text = "Blocked: ${gc.blocked.toString()}"
            tvDriver.text = "No driver"
            if (gc.pincode!=null) {
                tvPin.text = "${gc.pincode}"
            }
            if (gc.driverId!=null) {
                tvDriver.text = "${gc.driverId}"
            }

            itemView.setOnClickListener(OnClickListener {
                Log.d("TAG", "CLICKED ON ${gc.cardNumber}")
            })
        }
    }
}