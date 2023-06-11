package me.adbi.stolosapfma

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.DriverModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ActivityDetailDriver : ComponentActivity() {

    //var licensesToSave: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_driver)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ActivityDetailDriver, ActivityDrivers::class.java))
            }
        })

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        //region TEXT VIEWS
        val tvFirstName = findViewById<TextView>(R.id.tvFirstName)
        tvFirstName.text = "First Name:"
        val tvLastName = findViewById<TextView>(R.id.tvLastName)
        tvLastName.text = "Last Name:"
        val tvBirthDate = findViewById<TextView>(R.id.tvBirthDate)
        tvBirthDate.text = "Birth Date:"
        val tvRRN = findViewById<TextView>(R.id.tvRRN)
        tvRRN.text = "RRN:"
        val tvLicenses = findViewById<TextView>(R.id.tvLicenses)
        tvLicenses.text = "Licenses:"
        val tvAddress = findViewById<TextView>(R.id.tvAddress)
        tvAddress.text = "Address:"
        val tvVehicle = findViewById<TextView>(R.id.tvVehicle)
        tvVehicle.text = "Vehicle VIN:"
        val tvGasCard = findViewById<TextView>(R.id.tvGasCard)
        tvGasCard.text = "GC Num:"
        //endregion

        //region EDIT TEXTS
        val evFirstName = findViewById<EditText>(R.id.evFirstName)
        val evLastName = findViewById<EditText>(R.id.evLastName)
        val tvBirthDateDisplayValue = findViewById<TextView>(R.id.tvBirthDateDisplayValue)
        val evRRN = findViewById<EditText>(R.id.evRRN)
        val tvLicensesSelect = findViewById<TextView>(R.id.tvLicensesSelect)
        val evAddress = findViewById<EditText>(R.id.evAddress)
        val evVehicle = findViewById<EditText>(R.id.evVehicle)
        val evGasCard = findViewById<EditText>(R.id.evGasCard)
        //endregion

        //datepicker
        var year: Int = 1970
        var month: Int = 1
        var day: Int = 1
        tvBirthDateDisplayValue.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val calendar = Calendar.getInstance()
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    year = selectedYear
                    month = selectedMonth + 1
                    day = selectedDay
                    val selDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    tvBirthDateDisplayValue.text = selDate
                },
                year,
                month - 1,
                day
            )

            datePickerDialog.show()
        }

        // Format RRN EditText
        evRRN.inputType = InputType.TYPE_CLASS_NUMBER
        evRRN.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private val rrnPattern: String = "##.##.##-###.##"

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) {
                    return
                }

                isFormatting = true

                // Remove any existing formatting
                val unformattedRRN = s?.toString()?.replace("[^0-9]".toRegex(), "")

                if (unformattedRRN != null && unformattedRRN.isNotEmpty()) {
                    val formattedRRN = StringBuilder()
                    var currentIndex = 0

                    for (i in rrnPattern.indices) {
                        val currentPatternChar = rrnPattern[i]

                        if (currentIndex >= unformattedRRN.length) {
                            break
                        }

                        if (currentPatternChar == '#') {
                            formattedRRN.append(unformattedRRN[currentIndex])
                            currentIndex++
                        } else {
                            formattedRRN.append(currentPatternChar)
                        }
                    }

                    evRRN.setText(formattedRRN)
                    evRRN.setSelection(formattedRRN.length)
                }

                isFormatting = false
            }
        })

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        val licenses = resources.getStringArray(R.array.licenses)
        var selectedLicenses = BooleanArray(licenses.size) { false }
        val selection = mutableListOf<Int>()
        //region licenses select
        tvLicensesSelect.setOnClickListener {

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
                    strb.append(licenses[selection[i]])
                    if (i != selection.size - 1) {
                        strb.append(",")
                    }
                }
                tvLicensesSelect.text = strb.toString()
                //licensesToSave = strb.toString()
                //Log.i("ADBILOGSTOLOS", licensesToSave)
                dialog.dismiss()
            }

            val negativeButtonClickListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }

            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setMultiChoiceItems(licenses, selectedLicenses, licenseSelectionClickListener)
                .setPositiveButton("OK", positiveButtonClickListener)
                .setNegativeButton("Cancel", negativeButtonClickListener)

            val alertDialog = alertBuilder.create()
            alertDialog.show()
        }
        //endregion

        val driverId: Int = intent.getIntExtra("driverID", 0)

        if (driverId!=0) {
            btnDelete.setVisibility(View.VISIBLE)
            api.getDriverById(driverId).enqueue(object : Callback<DriverModel> {
                override fun onResponse(call: Call<DriverModel>, response: Response<DriverModel>) {
                    if (response.isSuccessful) {
                        Log.i("ADBILOGSTOLOS", response.body().toString())
                        //region FILL EDIT TEXTS
                        val d: DriverModel = response.body()!!
                        Toast.makeText(this@ActivityDetailDriver, "DriverID: ${d.driverID}", Toast.LENGTH_SHORT).show()
                        evFirstName.text = Editable.Factory.getInstance().newEditable(d.firstName)
                        evLastName.text = Editable.Factory.getInstance().newEditable(d.lastName)
                        tvBirthDateDisplayValue.text = Editable.Factory.getInstance().newEditable(d.birthDate.split("T")[0])
                        Log.i("ADBILOGSTOLOS", tvBirthDateDisplayValue.text.toString())
                        year = tvBirthDateDisplayValue.text.split("-")[0].toInt()
                        month = tvBirthDateDisplayValue.text.split("-")[1].toInt()
                        day = tvBirthDateDisplayValue.text.split("-")[2].toInt()
                        Log.i("ADBILOGSTOLOS", "year: $year; month: $month; day: $day")
                        evRRN.text = Editable.Factory.getInstance().newEditable(d.natRegNum)
                        //
                        for (license in d.licenses) {
                            val index = licenses.indexOf(license)
                            selection.add(index)
                            if (index != -1) {
                                selectedLicenses[index] = true
                            }
                        }
                        val strb = StringBuilder()
                        for (i in 0 until d.licenses.size) {
                            strb.append(d.licenses[i])
                            if (i != d.licenses.size - 1) {
                                strb.append(",")
                            }
                        }
                        tvLicensesSelect.text = strb.toString()
                        //licensesToSave = strb.toString()
                        //
                        evAddress.text = Editable.Factory.getInstance().newEditable("")
                        evVehicle.text = Editable.Factory.getInstance().newEditable("")
                        evAddress.text = Editable.Factory.getInstance().newEditable("")
                        if (d.address!=null) {
                            evAddress.text = Editable.Factory.getInstance().newEditable(d.address)
                        }
                        if (d.vehicleVin!=null) {
                            evVehicle.text = Editable.Factory.getInstance().newEditable(d.vehicleVin)
                        }
                        if (d.gasCardNum!=null) {
                            evGasCard.text = Editable.Factory.getInstance().newEditable(d.gasCardNum)
                        }
                        //endregion

                        //region REGISTER UPDATE
                        btnSave.setOnClickListener(View.OnClickListener {
                            var updatedD = DriverModel(driverId, evFirstName.text.toString(), evLastName.text.toString(), tvBirthDateDisplayValue.text.toString(),
                            evRRN.text.toString(), tvLicensesSelect.text.split(","), evAddress.text.toString(), evVehicle.text.toString(), evGasCard.text.toString())
                            val call: Call<Unit> = api.updateDriverById(updatedD)
                            call.enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    startActivity(Intent(this@ActivityDetailDriver, ActivityDrivers::class.java))
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Log.e("ADBILOGSTOLOS", t.message.toString())
                                }
                            })
                        })
                        //endregion

                        //region REGISTER DELETE
                        btnDelete.setOnClickListener {
                            val call: Call<Unit> = api.deleteDriverById(driverId)
                            call.enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    if (response.isSuccessful) {
                                        startActivity(Intent(this@ActivityDetailDriver, ActivityDrivers::class.java).putExtra("DELETED-INFO",
                                            "${d.firstName} ${d.lastName}"))
                                    } else {
                                        Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                                        Log.e("ADBILOGSTOLOS ${driverId}", response.code().toString())
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

                override fun onFailure(call: Call<DriverModel>, t: Throwable) {
                    Toast.makeText(this@ActivityDetailDriver, "Error", Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", t.message.toString())
                }
            })
        } else {// code for creating new driver
            btnDelete.setVisibility(View.GONE)
            //region REGISTER ADD

            btnSave.setOnClickListener{
                var cDriver = DriverModel(
                null,
                    evFirstName.text.toString(),
                    evLastName.text.toString(),
                    tvBirthDateDisplayValue.text.toString(),
                    evRRN.text.toString(),
                    tvLicensesSelect.text.split(","),
                    evAddress.text.toString(),
                    evVehicle.text.toString(),
                    evGasCard.text.toString()
                )

                Log.i("ADBILOGSTOLOS", cDriver.toString())
                api.addDriver(cDriver).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        startActivity(Intent(this@ActivityDetailDriver, ActivityDrivers::class.java))
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Toast.makeText(this@ActivityDetailDriver, "Error", Toast.LENGTH_SHORT).show()
                        Log.e("ADBILOGSTOLOS", t.message.toString())
                    }
                })
            }
            //endregion
        }
    }
}