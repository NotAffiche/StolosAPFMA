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
import me.adbi.stolosapfma.ActivityDetailGasCard
import me.adbi.stolosapfma.R
import me.adbi.stolosapfma.models.GasCardModel

class GasCardAdapter(val context: Context, objects:ArrayList<GasCardModel>) : RecyclerView.Adapter<GasCardAdapter.GasCardViewHolder>() {

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
        fun bindView(gc: GasCardModel) {
            tvCardNumber.text = "${gc.cardNumber}"

            itemView.setOnClickListener {
                context.startActivity(Intent(context, ActivityDetailGasCard::class.java).putExtra("gasCardNum", gc.cardNumber))            }
        }
    }
}