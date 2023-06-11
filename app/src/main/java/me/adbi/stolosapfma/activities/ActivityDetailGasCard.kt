package me.adbi.stolosapfma

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.DriverModel
import me.adbi.stolosapfma.models.GasCardModel
import me.adbi.stolosapfma.models.VehicleModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ActivityDetailGasCard : ComponentActivity() {

    private lateinit var driverAdapter: ArrayAdapter<DriverModel>

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
        val tvExpiringDateDisplayValue = findViewById<TextView>(R.id.tvExpiringDateDisplayValue)
        val evPincode = findViewById<EditText>(R.id.evPincode)
        val tvFuelTypesSelect = findViewById<TextView>(R.id.tvFuelTypesSelect)
        val cbBlocked = findViewById<CheckBox>(R.id.cbBlocked)
        //endregion

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        //region DATEPICKER
        //datepicker
        var year: Int = 1970
        var month: Int = 1
        var day: Int = 1
        tvExpiringDateDisplayValue.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val calendar = Calendar.getInstance()
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    year = selectedYear
                    month = selectedMonth + 1
                    day = selectedDay
                    val selDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    tvExpiringDateDisplayValue.text = selDate
                },
                year,
                month - 1,
                day
            )

            datePickerDialog.show()
        }
        //endregion

        //region MULTISELECT FUELTYPES
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
        //endregion

        //region FETCH DRIVERS FOR COMBOBOX (w/out gc)
        var driversWithoutGC: ArrayList<DriverModel> = ArrayList()

        val emptyDriver = DriverModel(null, "", "", "", "", listOf(), "", "", "")
        driversWithoutGC.add(emptyDriver)

        val spDriver = findViewById<Spinner>(R.id.spDriver)
        //endregion

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
                        setupSpinner(api, gc)
                        tvCardNumberVal.text = gc.cardNumber
                        tvExpiringDateDisplayValue.text = Editable.Factory.getInstance().newEditable(gc.expiringDate.split("T")[0])
                        Log.i("ADBILOGSTOLOS", tvExpiringDateDisplayValue.text.toString())
                        year = tvExpiringDateDisplayValue.text.split("-")[0].toInt()
                        month = tvExpiringDateDisplayValue.text.split("-")[1].toInt()
                        day = tvExpiringDateDisplayValue.text.split("-")[2].toInt()
                        Log.i("ADBILOGSTOLOS", "year: $year; month: $month; day: $day")
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
                        if (gc.pincode!=null) {
                            evPincode.text = Editable.Factory.getInstance().newEditable(gc.pincode.toString())
                        }
                        //endregion

                        //region REGISTER UPDATE
                        btnSave.setOnClickListener{
                            var pin: Int? = null
                            var driverId: Int? = null
                            if (!evPincode.text.toString().equals("")) {
                                pin = evPincode.text.toString().toInt()
                            }
                            val selectedDriver = spDriver.selectedItem as DriverModel
                            driverId = selectedDriver.driverID
                            var updatedGC = GasCardModel(
                                gasCardNum,
                                tvExpiringDateDisplayValue.text.toString(),
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
                                        Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                                        Log.e("ADBILOGSTOLOS ${gasCardNum}", response.code().toString())
                                    }
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                                    Log.e("ADBILOGSTOLOS", t.message.toString())
                                }
                            })
                        }
                        //endregion
                    }
                }

                override fun onFailure(call: Call<GasCardModel>, t: Throwable) {
                    Toast.makeText(this@ActivityDetailGasCard, "Error", Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", t.message.toString())
                }
            })
        } else {// code for creating new driver
            btnDelete.setVisibility(View.GONE)
            tvCardNumberVal.setVisibility(View.GONE)
            evCardNumber.setVisibility(View.VISIBLE)
            setupSpinner(api, null)
            //region REGISTER ADD

            btnSave.setOnClickListener{
                var pin: Int? = null
                var driverId: Int? = null
                if (!evPincode.text.toString().equals("")) {
                    pin = evPincode.text.toString().toInt()
                }
                val selectedDriver = spDriver.selectedItem as DriverModel
                driverId = selectedDriver.driverID
                var createdGC = GasCardModel(
                    evCardNumber.text.toString(),
                    tvExpiringDateDisplayValue.text.toString(),
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
                        Toast.makeText(this@ActivityDetailGasCard, "Error", Toast.LENGTH_SHORT).show()
                        Log.e("ADBILOGSTOLOS", t.message.toString())
                    }
                })
            }
            //endregion
        }
    }
    private fun setupSpinner(api: ApiService, gc: GasCardModel?) {
        //region GET VEHICLES & GASCARDS FOR COMBOBOX (values w/out Drivers)
        var driversWithoutGC: ArrayList<DriverModel> = ArrayList()

        val emptyDriver = DriverModel(null, "", "", "", "", listOf(), "", "", "")
        driversWithoutGC.add(emptyDriver)

        val spDriver = findViewById<Spinner>(R.id.spDriver)

        api.getDrivers().enqueue(object : Callback<ArrayList<DriverModel>> {
            override fun onResponse(call: Call<ArrayList<DriverModel>>, response: Response<ArrayList<DriverModel>>) {
                if (response.isSuccessful) {
                    val allDrivers = response.body()
                    if (allDrivers != null) {
                        if (gc != null) {
                            val filteredDrivers = allDrivers.filter { it.gasCardNum == gc.cardNumber }
                            driversWithoutGC.addAll(filteredDrivers)
                        }
                        driversWithoutGC.addAll(allDrivers.filter { it.gasCardNum == null })
                        Log.i("ADBILOGSTOLOS", driversWithoutGC.toString())
                    } else {
                        Log.e("ADBILOGSTOLOS", "Empty driver list.")
                    }
                } else {
                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", "Error getting drivers without gascards.")
                }
                handleAPIResponseAllWithoutGasCard(driversWithoutGC, spDriver, gc)
            }

            override fun onFailure(call: Call<ArrayList<DriverModel>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                Log.e("ADBILOGSTOLOS", "Connection error getting drivers without gascards.")
            }
        })
        //endregion
    }

    private fun setSpinnerAdapter(driversWithoutGascards: ArrayList<DriverModel>, spDriver: Spinner, gc: GasCardModel?) {
        driverAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, driversWithoutGascards)
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDriver.adapter = driverAdapter
        if (gc != null) {
            if (gc.driverId != null) {
                spDriver.setSelection(1, true)
            } else {
                spDriver.setSelection(0, true)
            }
            Log.i("ADBILOGSTOLOS", "${gc.cardNumber.toString()} ${gc.driverId.toString()}")
        } else {
            spDriver.setSelection(0, true)
        }
    }

    private fun handleAPIResponseAllWithoutGasCard(gascardsWithoutDrivers: ArrayList<DriverModel>, spDriver: Spinner, gc: GasCardModel?) {
        if (gascardsWithoutDrivers.isNotEmpty()) {
            setSpinnerAdapter(gascardsWithoutDrivers, spDriver, gc)
        }
    }
}