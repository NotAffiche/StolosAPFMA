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
import android.widget.ArrayAdapter
import android.widget.Button
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


class ActivityDetailDriver : ComponentActivity() {

    private lateinit var vehicleAdapter: ArrayAdapter<VehicleModel>
    private lateinit var gasCardAdapter: ArrayAdapter<GasCardModel>

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
        //endregion

        //region DATEPICKER
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
        //endregion

        //region RRN FORMAT
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
        //endregion

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        //region LICENSES MULTISELECT
        val licenses = resources.getStringArray(R.array.licenses)
        var selectedLicenses = BooleanArray(licenses.size) { false }
        val selection = mutableListOf<Int>()
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

        //region FETCH VEHICLES & GASCARDS FOR COMBOBOX (values w/out Drivers)
        val spVeh = findViewById<Spinner>(R.id.spVeh)
        val spGC = findViewById<Spinner>(R.id.spGC)
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
                        setupVehicleSpinner(api, spVeh, d)
                        setupGasCardSpinner(api, spGC, d)
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
                        evAddress.text = Editable.Factory.getInstance().newEditable("")
                        if (d.address!=null) {
                            evAddress.text = Editable.Factory.getInstance().newEditable(d.address)
                        }
                        //endregion

                        //region REGISTER UPDATE
                        btnSave.setOnClickListener(View.OnClickListener {
                            val selectedVehicle = spVeh.selectedItem as VehicleModel
                            val selectedGasCard = spGC.selectedItem as GasCardModel

                            val selectedVehValue = selectedVehicle.vin?.toString()
                            val selectedGcValue = selectedGasCard.cardNumber ?: null

                            val evAddressValue = evAddress.text.toString().takeIf { it.isNotEmpty() } ?: null
                            val updatedD = DriverModel(
                                driverId,
                                evFirstName.text.toString(),
                                evLastName.text.toString(),
                                tvBirthDateDisplayValue.text.toString(),
                                evRRN.text.toString(),
                                tvLicensesSelect.text.split(","),
                                evAddressValue,
                                selectedVehValue,
                                selectedGcValue
                            )

                            api.updateDriverById(updatedD).enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    startActivity(Intent(this@ActivityDetailDriver, ActivityDrivers::class.java))
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Toast.makeText(this@ActivityDetailDriver, "Error", Toast.LENGTH_SHORT).show()
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
            setupVehicleSpinner(api, spVeh, null)
            setupGasCardSpinner(api, spGC, null)
            //region REGISTER ADD

            btnSave.setOnClickListener{
                val selectedVehicle = spVeh.selectedItem as VehicleModel
                val selectedGasCard = spGC.selectedItem as GasCardModel
                val evAddressValue = evAddress.text.toString().takeIf { it.isNotEmpty() } ?: null
                var cDriver = DriverModel(
                null,
                    evFirstName.text.toString(),
                    evLastName.text.toString(),
                    tvBirthDateDisplayValue.text.toString(),
                    evRRN.text.toString(),
                    tvLicensesSelect.text.split(","),
                    evAddressValue,
                    selectedVehicle.vin,
                    selectedGasCard.cardNumber
                )
                Log.i("ADBILOGSTOLOS", cDriver.address.toString() ?: "no address")
                Log.i("ADBILOGSTOLOS", selectedVehicle.vin.toString())
                Log.i("ADBILOGSTOLOS", selectedGasCard.cardNumber.toString())
                Log.i("ADBILOGSTOLOS", cDriver.toString())
                val c = cDriver
                Log.i("ADBILOGSTOLOS", "${c.firstName} ${c.lastName} ${c.birthDate.toString()} ${c.natRegNum.toString()} ${c.licenses.toString()} ${c.address} ${c.vehicleVin} ${c.gasCardNum}")

                api.addDriver(cDriver).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        if (response.isSuccessful) {
                            startActivity(Intent(this@ActivityDetailDriver, ActivityDrivers::class.java))
                        } else {
                            Toast.makeText(this@ActivityDetailDriver, "Error", Toast.LENGTH_SHORT).show()
                            Log.e("ADBILOGSTOLOS", response.body().toString())
                        }
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

    private fun setupVehicleSpinner(api: ApiService, spVeh: Spinner, d: DriverModel?) {
        //region GET VEHICLES FOR COMBOBOX (values w/out Drivers)
        var vehiclesWithoutDriver: ArrayList<VehicleModel> = ArrayList()

        val unsetVehicle = VehicleModel(null, "", "", "", "", null, null, null)
        vehiclesWithoutDriver.add(unsetVehicle)

        api.getVehicles().enqueue(object : Callback<ArrayList<VehicleModel>> {
            override fun onResponse(call: Call<ArrayList<VehicleModel>>, response: Response<ArrayList<VehicleModel>>) {
                if (response.isSuccessful) {
                    val allVehicles = response.body()
                    if (allVehicles != null) {
                        if (d != null) {
                            val filteredVehicles = allVehicles.filter { it.vin == d.vehicleVin }
                            vehiclesWithoutDriver.addAll(filteredVehicles)
                        }
                        vehiclesWithoutDriver.addAll(allVehicles.filter { it.driverId == null })
                        Log.i("ADBILOGSTOLOS", vehiclesWithoutDriver.toString())
                    } else {
                        Log.e("ADBILOGSTOLOS", "Empty vehicle list.")
                    }
                } else {
                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", "Error getting vehicles without driver.")
                }
                handleAPIResponseVehiclesWithoutDriver(vehiclesWithoutDriver, spVeh, d)
            }

            override fun onFailure(call: Call<ArrayList<VehicleModel>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                Log.e("ADBILOGSTOLOS", "Connection error getting vehicles without driver.")
            }
        })
        //endregion
    }
    private fun setupGasCardSpinner(api: ApiService, spGC: Spinner, d: DriverModel?) {
        //region GET VEHICLES FOR COMBOBOX (values w/out Drivers)
        var gascardsWithoutDriver: ArrayList<GasCardModel> = ArrayList()

        val unsetGascard = GasCardModel(null, "", null, listOf(), false, null)
        gascardsWithoutDriver.add(unsetGascard)

        api.getGasCards().enqueue(object : Callback<ArrayList<GasCardModel>> {
            override fun onResponse(call: Call<ArrayList<GasCardModel>>, response: Response<ArrayList<GasCardModel>>) {
                if (response.isSuccessful) {
                    val allGascards = response.body()
                    if (allGascards != null) {
                        if (d != null) {
                            val filteredGascards = allGascards.filter { it.cardNumber == d.gasCardNum }
                            gascardsWithoutDriver.addAll(filteredGascards)
                        }
                        gascardsWithoutDriver.addAll(allGascards.filter { it.driverId == null })
                        Log.i("ADBILOGSTOLOS", gascardsWithoutDriver.toString())
                    } else {
                        Log.e("ADBILOGSTOLOS", "Empty gascard list.")
                    }
                } else {
                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", "Error getting gascards without driver.")
                }
                handleAPIResponseGascardsWithoutDriver(gascardsWithoutDriver, spGC, d)
            }

            override fun onFailure(call: Call<ArrayList<GasCardModel>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                Log.e("ADBILOGSTOLOS", "Connection error getting gascards without driver.")
            }
        })
        //endregion
    }

    private fun setSpinnerVehicleAdapter(driversWithoutGascards: ArrayList<VehicleModel>, spVeh: Spinner, d: DriverModel?) {
        vehicleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, driversWithoutGascards)
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spVeh.adapter = vehicleAdapter
        if (d != null) {
            if (d.vehicleVin != null) {
                spVeh.setSelection(1, true)
            } else {
                spVeh.setSelection(0, true)
            }
            Log.i("ADBILOGSTOLOS", d.vehicleVin.toString())
        } else {
            spVeh.setSelection(0, true)
        }
    }
    private fun setSpinnerGascardAdapter(driversWithoutGascards: ArrayList<GasCardModel>, spGC: Spinner, d: DriverModel?) {
        gasCardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, driversWithoutGascards)
        gasCardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGC.adapter = gasCardAdapter
        if (d != null) {
            if (d.gasCardNum != null) {
                spGC.setSelection(1, true)
            } else {
                spGC.setSelection(0, true)
            }
            Log.i("ADBILOGSTOLOS", d.vehicleVin.toString())
        } else {
            spGC.setSelection(0, true)
        }
    }

    private fun handleAPIResponseVehiclesWithoutDriver(vehiclesWithoutDriver: ArrayList<VehicleModel>, spVeg: Spinner, d: DriverModel?) {
        if (vehiclesWithoutDriver.isNotEmpty()) {
            setSpinnerVehicleAdapter(vehiclesWithoutDriver, spVeg, d)
        }
    }
    private fun handleAPIResponseGascardsWithoutDriver(gascardsWithoutDriver: ArrayList<GasCardModel>, spGC: Spinner, d: DriverModel?) {
        if (gascardsWithoutDriver.isNotEmpty()) {
            setSpinnerGascardAdapter(gascardsWithoutDriver, spGC, d)
        }
    }

    /*
    private fun setupSpinners(api: ApiService, d: DriverModel?) {
        //region GET VEHICLES & GASCARDS FOR COMBOBOX (values w/out Drivers)
        var vehiclesWithoutDrivers: ArrayList<VehicleModel> = ArrayList()
        var gasCardsWithoutDrivers: ArrayList<GasCardModel> = ArrayList()

        val emptyVehicle = VehicleModel(null, "", "", "", "", "", null, null)
        vehiclesWithoutDrivers.add(emptyVehicle)

        val emptyGasCard = GasCardModel(null, "", null, listOf(), false, null)
        gasCardsWithoutDrivers.add(emptyGasCard)

        val spVeh = findViewById<Spinner>(R.id.spVeh)
        val spGC = findViewById<Spinner>(R.id.spGC)

        api.getVehicles().enqueue(object : Callback<ArrayList<VehicleModel>> {
            override fun onResponse(call: Call<ArrayList<VehicleModel>>, response: Response<ArrayList<VehicleModel>>) {
                if (response.isSuccessful) {
                    val allVehicles = response.body()
                    if (allVehicles != null) {
                        if (d != null && d.driverID != 0) {
                            val filteredVehicles = allVehicles.filter { it.driverId == d.driverID }
                            vehiclesWithoutDrivers.addAll(filteredVehicles)
                        }
                        vehiclesWithoutDrivers.addAll(allVehicles.filter { it.driverId == null })
                        Log.i("ADBILOGSTOLOS", vehiclesWithoutDrivers.toString())
                    } else {
                        Log.e("ADBILOGSTOLOS", "Empty vehicle list.")
                    }
                } else {
                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", "Error getting vehicles without drivers.")
                }
                handleAPIResponseAllWithoutDrivers(vehiclesWithoutDrivers, spVeh, gasCardsWithoutDrivers, spGC, d)
            }

            override fun onFailure(call: Call<ArrayList<VehicleModel>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                Log.e("ADBILOGSTOLOS", "Connection error getting vehicles without drivers.")
            }
        })
        api.getGasCards().enqueue(object : Callback<ArrayList<GasCardModel>> {
            override fun onResponse(call: Call<ArrayList<GasCardModel>>, response: Response<ArrayList<GasCardModel>>) {
                if (response.isSuccessful) {
                    val allGascards = response.body()
                    if (allGascards != null) {
                        if (d != null && d.driverID != 0) {
                            val filteredGasCards = allGascards.filter { it.driverId == d.driverID }
                            gasCardsWithoutDrivers.addAll(filteredGasCards)
                        }
                        gasCardsWithoutDrivers.addAll(allGascards.filter { it.driverId == null })
                        Log.i("ADBILOGSTOLOS", gasCardsWithoutDrivers.toString())
                    } else {
                        Log.e("ADBILOGSTOLOS", "Empty vehicle list.")
                    }
                } else {
                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", "Error getting gascards without drivers.")
                }
                handleAPIResponseAllWithoutDrivers(vehiclesWithoutDrivers, spVeh, gasCardsWithoutDrivers, spGC, d)
            }

            override fun onFailure(call: Call<ArrayList<GasCardModel>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                Log.e("ADBILOGSTOLOS", "Connection error getting gascards without drivers.")
            }

        })
        //endregion
    }

    private fun setSpinnersAdapters(vehiclesWithoutDrivers: ArrayList<VehicleModel>, spVeh: Spinner, gasCardsWithoutDrivers: ArrayList<GasCardModel>, spGC:Spinner, d: DriverModel?) {
        vehicleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vehiclesWithoutDrivers)
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spVeh.adapter = vehicleAdapter

        gasCardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gasCardsWithoutDrivers)
        gasCardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGC.adapter = gasCardAdapter

        //TODO: fix this mess
        /*
        if (d != null) {
            if (d.gasCardNum != null) {
                spGC.setSelection(1, true)
            } else {
                spGC.setSelection(0, true)
            }
            if (d.vehicleVin != null) {
                spVeh.setSelection(1, true)
            } else {
                spVeh.setSelection(0, true)
            }
            Log.i("ADBILOGSTOLOS", "${d.natRegNum.toString()} ${d.gasCardNum.toString()}")
            Log.i("ADBILOGSTOLOS", "${d.natRegNum.toString()} ${d.vehicleVin.toString()}")
        } else {
            spGC.setSelection(0, true)
            spVeh.setSelection(0, true)
        }
        */
    }

    private fun handleAPIResponseAllWithoutDrivers(vehiclesWithoutDrivers: ArrayList<VehicleModel>, spVeh: Spinner, gasCardsWithoutDrivers: ArrayList<GasCardModel>, spGC:Spinner, d: DriverModel?) {
        if (vehiclesWithoutDrivers.isNotEmpty() && gasCardsWithoutDrivers.isNotEmpty()) {
            setSpinnersAdapters(vehiclesWithoutDrivers, spVeh, gasCardsWithoutDrivers, spGC, d)
        }
    }
    */
}