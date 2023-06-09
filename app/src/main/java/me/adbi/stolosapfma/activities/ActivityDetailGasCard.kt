package me.adbi.stolosapfma

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.GasCardModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityDetailGasCard : ComponentActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_gascard)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ActivityDetailGasCard, ActivityGasCards::class.java))
            }
        })

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        //region TEXT VIEWS
        val tvCardNumber = findViewById<TextView>(R.id.tvCardNumber)
        tvCardNumber.text = "Card Number:"
        val tvCardNumberVal = findViewById<TextView>(R.id.tvCardNumberVal)
        val tvExpiringDate = findViewById<TextView>(R.id.tvExpiringDate)
        tvExpiringDate.text = "Expiring Date:"
        val tvPincode = findViewById<TextView>(R.id.tvPincode)
        tvPincode.text = "Pin code:"
        val tvFuelTypes = findViewById<TextView>(R.id.tvFuelTypes)
        tvFuelTypes.text = "Fuel Types:"
        val tvBlocked = findViewById<TextView>(R.id.tvBlocked)
        tvBlocked.text = "Blocked:"
        val tvDriver = findViewById<TextView>(R.id.tvDriver)
        tvDriver.text = "Driver Id:"
        //endregion

        //region EDIT TEXTS
        val evCardNumber = findViewById<EditText>(R.id.evCardNumber)
        val evExpiringDate = findViewById<EditText>(R.id.evExpiringDate)
        val evPincode = findViewById<EditText>(R.id.evPincode)
        val tvFuelTypesSelect = findViewById<TextView>(R.id.tvFuelTypesSelect)
        val cbBlocked = findViewById<CheckBox>(R.id.cbBlocked)
        val evDriver = findViewById<EditText>(R.id.evDriver)
        //endregion

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        //region licenses select
        val fueltypes = resources.getStringArray(R.array.fueltypes)
        var selectedFueltypes = BooleanArray(fueltypes.size) { false }
        val selection = mutableListOf<Int>()

        tvFuelTypesSelect.setOnClickListener {

            val licenseSelectionClickListener = DialogInterface.OnMultiChoiceClickListener { _, which, isChecked ->
                if (isChecked) {
                    selection.add(which)
                } else {
                    selection.remove(which)
                }
            }

            val positiveButtonClickListener = DialogInterface.OnClickListener { dialog, _ ->
                val strb = StringBuilder()
                for (i in 0 until selection.size) {
                    strb.append(fueltypes[selection[i]])
                    if (i != selection.size - 1) {
                        strb.append(",")
                    }
                }
                tvFuelTypesSelect.text = strb.toString()
                //licensesToSave = strb.toString()
                //Log.i("ADBILOGSTOLOS", licensesToSave)
                dialog.dismiss()
            }

            val negativeButtonClickListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }

            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setMultiChoiceItems(fueltypes, selectedFueltypes, licenseSelectionClickListener)
                .setPositiveButton("OK", positiveButtonClickListener)
                .setNegativeButton("Cancel", negativeButtonClickListener)

            val alertDialog = alertBuilder.create()
            alertDialog.show()
        }

        val gasCardNum: String? = intent.getStringExtra("gasCardNum")

        if (!gasCardNum.isNullOrEmpty()) {
            btnDelete.setVisibility(View.VISIBLE)
            tvCardNumberVal.setVisibility(View.VISIBLE)
            evCardNumber.setVisibility(View.GONE)
            api.getGasCardByCardNum(gasCardNum).enqueue(object : Callback<GasCardModel> {
                override fun onResponse(call: Call<GasCardModel>, response: Response<GasCardModel>) {
                    if (response.isSuccessful) {
                        Log.i("ADBILOGSTOLOS", response.body().toString())
                        //region FILL EDIT TEXTS
                        val gc: GasCardModel = response.body()!!
                        tvCardNumberVal.text = gc.cardNumber
                        evExpiringDate.text = Editable.Factory.getInstance().newEditable(gc.expiringDate.split("T")[0])
                        evPincode.text = Editable.Factory.getInstance().newEditable("")
                        //tvFuelTypesSelect.text = Editable.Factory.getInstance().newEditable(gc.fuelTypes.joinToString(separator = ","))
                        //
                        for (ft in gc.fuelTypes) {
                            val index = fueltypes.indexOf(ft)
                            selection.add(index)
                            if (index != -1) {
                                selectedFueltypes[index] = true
                            }
                        }
                        val strb = StringBuilder()
                        for (i in 0 until gc.fuelTypes.size) {
                            strb.append(gc.fuelTypes[i])
                            if (i != gc.fuelTypes.size - 1) {
                                strb.append(",")
                            }
                        }
                        tvFuelTypesSelect.text = strb.toString()
                        //
                        cbBlocked.isChecked = gc.blocked
                        evDriver.text = Editable.Factory.getInstance().newEditable("")
                        if (gc.pincode!=null) {
                            evPincode.text = Editable.Factory.getInstance().newEditable(gc.pincode.toString())
                        }
                        if (gc.driverId!=null) {
                            evDriver.text = Editable.Factory.getInstance().newEditable(gc.driverId.toString())
                        }
                        //endregion

                        //region REGISTER UPDATE
                        btnSave.setOnClickListener{
                            var pin: Int? = null
                            var driverId: Int? = null
                            if (!evPincode.text.toString().equals("")) {
                                pin = evPincode.text.toString().toInt()
                            }
                            if (!evDriver.text.toString().equals("")) {
                                driverId = evDriver.text.toString().toInt()
                            }
                            var updatedGC = GasCardModel(
                                gasCardNum,
                                evExpiringDate.text.toString(),
                                pin,
                                tvFuelTypesSelect.text.toString().split(","),
                                cbBlocked.isChecked,
                                driverId)
                            val call: Call<Unit> = api.updateGasCardByCardNum(updatedGC)
                            call.enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    startActivity(Intent(this@ActivityDetailGasCard, ActivityGasCards::class.java))
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Log.e("ADBILOGSTOLOS", t.message.toString())
                                }
                            })
                        }
                        //endregion

                        //region REGISTER DELETE
                        btnDelete.setOnClickListener {
                            val call: Call<Unit> = api.deleteGasCardByCardNum(gasCardNum)
                            call.enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    if (response.isSuccessful) {
                                        startActivity(Intent(this@ActivityDetailGasCard, ActivityGasCards::class.java).putExtra("DELETED-INFO",
                                            "${gasCardNum}"))
                                    } else {
                                        Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT)
                                        Log.e("ADBILOGSTOLOS ${gasCardNum}", response.code().toString())
                                    }
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT)
                                    Log.e("ADBILOGSTOLOS", t.message.toString())
                                }
                            })
                        }
                        //endregion
                    }
                }

                override fun onFailure(call: Call<GasCardModel>, t: Throwable) {
                    Toast.makeText(this@ActivityDetailGasCard, "Error", Toast.LENGTH_SHORT)
                    Log.e("ADBILOGSTOLOS", t.message.toString())
                }
            })
        } else {// code for creating new driver
            btnDelete.setVisibility(View.GONE)
            tvCardNumberVal.setVisibility(View.GONE)
            evCardNumber.setVisibility(View.VISIBLE)
            //region REGISTER ADD

            btnSave.setOnClickListener{
                var pin: Int? = null
                var driverId: Int? = null
                if (!evPincode.text.toString().equals("")) {
                    pin = evPincode.text.toString().toInt()
                }
                if (!evDriver.text.toString().equals("")) {
                    driverId = evDriver.text.toString().toInt()
                }
                var createdGC = GasCardModel(
                    evCardNumber.text.toString(),
                    evExpiringDate.text.toString(),
                    pin,
                    tvFuelTypesSelect.text.split(","),
                    cbBlocked.isChecked,
                    driverId)

                Log.i("ADBILOGSTOLOS", createdGC.toString())
                api.addGasCard(createdGC).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        startActivity(Intent(this@ActivityDetailGasCard, ActivityGasCards::class.java))
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Log.e("ADBILOGSTOLOS", t.message.toString())
                    }
                })
            }
            //endregion
        }
    }
}