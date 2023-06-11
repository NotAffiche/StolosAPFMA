package me.adbi.stolosapfma

import android.content.Intent
import android.os.Bundle
import android.text.Editable
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


class ActivityDetailVehicle : ComponentActivity() {

    private lateinit var driverAdapter: ArrayAdapter<DriverModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_vehicle)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@ActivityDetailVehicle, ActivityVehicles::class.java))
            }
        })

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        //region TEXT VIEWS
        val tvVin = findViewById<TextView>(R.id.tvVin)
        tvVin.text = "VIN:"
        val tvVinVal = findViewById<TextView>(R.id.tvVinVal)
        val tvBrandModel = findViewById<TextView>(R.id.tvBrandModel)
        tvBrandModel.text = "Brand Model:"
        val tvLicensePlate = findViewById<TextView>(R.id.tvLicensePlate)
        tvLicensePlate.text = "License Plate:"
        val tvFuelTypes = findViewById<TextView>(R.id.tvFuelTypes)
        tvFuelTypes.text = "Fuel Type:"
        val tvVehicleTypes = findViewById<TextView>(R.id.tvVehicleType)
        tvVehicleTypes.text = "Vehicle Type:"
        val tvColor = findViewById<TextView>(R.id.tvColor)
        tvColor.text = "Color:"
        val tvDoors = findViewById<TextView>(R.id.tvDoors)
        tvDoors.text = "Doors:"
        val tvDriver = findViewById<TextView>(R.id.tvDriver)
        tvDriver.text = "Driver Id:"
        //endregion

        //region EDIT TEXTS / SPINNERS
        val evVin = findViewById<EditText>(R.id.evVin)
        val evBrandModel = findViewById<EditText>(R.id.evBrandModel)
        val evLicensePlate = findViewById<EditText>(R.id.evLicensePlate)
        val spFuelTypes = findViewById<Spinner>(R.id.spFuelTypes)
        val spVehicleType = findViewById<Spinner>(R.id.spVehicleTypes)
        val evColor = findViewById<EditText>(R.id.evColor)
        val evDoors = findViewById<EditText>(R.id.evDoors)
        //endregion

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        val fuelTypes = resources.getStringArray(R.array.fueltypes)
        val vehicleTypes = resources.getStringArray(R.array.vehicletypes)

        spFuelTypes.adapter = ArrayAdapter(this@ActivityDetailVehicle, android.R.layout.simple_spinner_item, fuelTypes)
        spVehicleType.adapter = ArrayAdapter(this@ActivityDetailVehicle, android.R.layout.simple_spinner_item, vehicleTypes)

        //region FETCH DRIVERS FOR COMBOBOX (w/out gc)
        var driversWithoutGC: ArrayList<DriverModel> = ArrayList()

        val emptyDriver = DriverModel(null, "", "", "", "", listOf(), "", "", "")
        driversWithoutGC.add(emptyDriver)

        val spDriver = findViewById<Spinner>(R.id.spDriver)
        //endregion

        val vehicleVin: String? = intent.getStringExtra("vehicleVin")

        if (!vehicleVin.isNullOrEmpty()) {
            btnDelete.setVisibility(View.VISIBLE)
            tvVinVal.setVisibility(View.VISIBLE)
            evVin.setVisibility(View.GONE)
            api.getVehicleByVIN(vehicleVin).enqueue(object : Callback<VehicleModel> {
                override fun onResponse(call: Call<VehicleModel>, response: Response<VehicleModel>) {
                    if (response.isSuccessful) {
                        //region FILL EDIT TEXTS
                        val v: VehicleModel = response.body()!!
                        setupSpinner(api, v)
                        tvVinVal.text = v.vin
                        evBrandModel.text = Editable.Factory.getInstance().newEditable(v.brandModel)
                        evLicensePlate.text = Editable.Factory.getInstance().newEditable(v.licensePlate)

                        spFuelTypes.setSelection(fuelTypes.indexOf(v.fuelType))
                        spVehicleType.setSelection(vehicleTypes.indexOf(v.vehicleType))

                        evColor.text = Editable.Factory.getInstance().newEditable("")
                        evDoors.text = Editable.Factory.getInstance().newEditable("")
                        if (v.color!=null) {
                            evColor.text = Editable.Factory.getInstance().newEditable(v.color)
                        }
                        if (v.doors!=null) {
                            evDoors.text = Editable.Factory.getInstance().newEditable(v.doors.toString())
                        }
                        //endregion

                        //region REGISTER UPDATE
                        btnSave.setOnClickListener{
                            var color: String? = null
                            var doors: Int? = null
                            var driverId: Int? = null
                            if (!evColor.text.toString().equals("")) {
                                color = evColor.text.toString()
                            }
                            if (!evDoors.text.toString().equals("")) {
                                doors = evDoors.text.toString().toInt()
                            }
                            val selectedDriver = spDriver.selectedItem as DriverModel
                            driverId = selectedDriver.driverID

                            val updatedV = VehicleModel(
                                vehicleVin,
                                evBrandModel.text.toString(),
                                evLicensePlate.text.toString(),
                                fuelTypes.get(spFuelTypes.selectedItemPosition),
                                vehicleTypes.get(spVehicleType.selectedItemPosition),
                                color,
                                doors,
                                driverId)

                            api.updateVehicleByVin(updatedV).enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    startActivity(Intent(this@ActivityDetailVehicle, ActivityVehicles::class.java))
                                }

                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    Log.e("ADBILOGSTOLOS", t.message.toString())
                                }
                            })
                        }
                        //endregion

                        //region REGISTER DELETE
                        btnDelete.setOnClickListener {
                            api.deleteVehicleByVin(vehicleVin).enqueue(object : Callback<Unit> {
                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    if (response.isSuccessful) {
                                        startActivity(Intent(this@ActivityDetailVehicle, ActivityVehicles::class.java).putExtra("DELETED-INFO",
                                            "${v.brandModel} [+] ${v.licensePlate}"))
                                    } else {
                                        Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                                        Log.e("ADBILOGSTOLOS ${vehicleVin}", response.code().toString())
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

                override fun onFailure(call: Call<VehicleModel>, t: Throwable) {
                    Toast.makeText(this@ActivityDetailVehicle, "Error", Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", t.message.toString())
                }
            })
        } else {// code for creating new driver
            btnDelete.setVisibility(View.GONE)
            tvVinVal.setVisibility(View.GONE)
            evVin.setVisibility(View.VISIBLE)

            //region REGISTER ADD

            btnSave.setOnClickListener{
                var color: String? = null
                var doors: Int? = null
                var driverId: Int? = null
                if (!evColor.text.toString().equals("")) {
                    color = evColor.text.toString()
                }
                if (!evDoors.text.toString().equals("")) {
                    doors = evDoors.text.toString().toInt()
                }
                val selectedDriver = spDriver.selectedItem as DriverModel
                driverId = 0
                val createdV = VehicleModel(
                    evVin.text.toString(),
                    evBrandModel.text.toString(),
                    evLicensePlate.text.toString(),
                    fuelTypes.get(spFuelTypes.selectedItemPosition),
                    vehicleTypes.get(spVehicleType.selectedItemPosition),
                    color,
                    doors,
                    driverId)

                api.addVehicle(createdV).enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        startActivity(Intent(this@ActivityDetailVehicle, ActivityVehicles::class.java))
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Toast.makeText(this@ActivityDetailVehicle, "Error", Toast.LENGTH_SHORT).show()
                        Log.e("ADBILOGSTOLOS", t.message.toString())
                    }
                })
            }
            //endregion
        }
    }

    private fun setupSpinner(api: ApiService, v: VehicleModel?) {
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
                        if (v != null) {
                            val filteredDrivers = allDrivers.filter { it.vehicleVin == v.vin }
                            driversWithoutGC.addAll(filteredDrivers)
                        }
                        driversWithoutGC.addAll(allDrivers.filter { it.vehicleVin == null })
                        Log.i("ADBILOGSTOLOS", driversWithoutGC.toString())
                    } else {
                        Log.e("ADBILOGSTOLOS", "Empty driver list.")
                    }
                } else {
                    Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                    Log.e("ADBILOGSTOLOS", "Error getting drivers without vehicles.")
                }
                handleAPIResponseAllWithoutGasCard(driversWithoutGC, spDriver, v)
            }

            override fun onFailure(call: Call<ArrayList<DriverModel>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
                Log.e("ADBILOGSTOLOS", "Connection error getting drivers without vehicles.")
            }
        })
        //endregion
    }

    private fun setSpinnerAdapter(driversWithoutVehicles: ArrayList<DriverModel>, spDriver: Spinner, v: VehicleModel?) {
        driverAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, driversWithoutVehicles)
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDriver.adapter = driverAdapter
        if (v != null) {
            if (v.driverId != null) {
                spDriver.setSelection(1, true)
            } else {
                spDriver.setSelection(0, true)
            }
            Log.i("ADBILOGSTOLOS", "${v.vin.toString()} ${v.driverId.toString()}")
        } else {
            spDriver.setSelection(0, true)
        }
    }

    private fun handleAPIResponseAllWithoutGasCard(driversWithoutVehicles: ArrayList<DriverModel>, spDriver: Spinner, v: VehicleModel?) {
        if (driversWithoutVehicles.isNotEmpty()) {
            setSpinnerAdapter(driversWithoutVehicles, spDriver, v)
        }
    }
}