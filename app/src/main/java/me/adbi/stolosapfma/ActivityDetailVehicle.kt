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
import me.adbi.stolosapfma.factories.RetrofitFactory
import me.adbi.stolosapfma.interfaces.ApiService
import me.adbi.stolosapfma.models.VehicleModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityDetailVehicle : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_detail_vehicle)

        val api: ApiService = RetrofitFactory(this).Retrofit().create(ApiService::class.java)

        //region TEXT VIEWS
        val tvVin = findViewById<TextView>(R.id.tvVin)
        tvVin.text = "VIN:"
        val tvVinVal = findViewById<TextView>(R.id.tvVinVal)
        val tvBrandModel = findViewById<TextView>(R.id.tvBrandModel)
        tvBrandModel.text = "Brand Mode:"
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
        val evDriver = findViewById<EditText>(R.id.evDriver)
        //endregion

        //region BUTTONS
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        //endregion

        val fuelTypes = resources.getStringArray(R.array.fueltypes)
        val vehicleTypes = resources.getStringArray(R.array.vehicletypes)

        spFuelTypes.adapter = ArrayAdapter(this@ActivityDetailVehicle, android.R.layout.simple_spinner_item, fuelTypes)
        spVehicleType.adapter = ArrayAdapter(this@ActivityDetailVehicle, android.R.layout.simple_spinner_item, vehicleTypes)


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
                        tvVinVal.text = v.vin
                        evBrandModel.text = Editable.Factory.getInstance().newEditable(v.brandModel)
                        evLicensePlate.text = Editable.Factory.getInstance().newEditable(v.licensePlate)

                        spFuelTypes.setSelection(fuelTypes.indexOf(v.fuelType))
                        spVehicleType.setSelection(vehicleTypes.indexOf(v.vehicleType))

                        evColor.text = Editable.Factory.getInstance().newEditable("")
                        evDoors.text = Editable.Factory.getInstance().newEditable("")
                        evDriver.text = Editable.Factory.getInstance().newEditable("")
                        if (v.color!=null) {
                            evColor.text = Editable.Factory.getInstance().newEditable(v.color)
                        }
                        if (v.doors!=null) {
                            evDoors.text = Editable.Factory.getInstance().newEditable(v.doors.toString())
                        }
                        if (v.driverId!=null) {
                            evDriver.text = Editable.Factory.getInstance().newEditable(v.driverId.toString())
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
                            if (!evDriver.text.toString().equals("")) {
                                driverId = evDriver.text.toString().toInt()
                            }

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
                                        Toast.makeText(baseContext, response.code().toString(), Toast.LENGTH_SHORT)
                                        Log.e("ADBILOGSTOLOS ${vehicleVin}", response.code().toString())
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

                override fun onFailure(call: Call<VehicleModel>, t: Throwable) {
                    Toast.makeText(this@ActivityDetailVehicle, "Error", Toast.LENGTH_SHORT)
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
                if (!evDriver.text.toString().equals("")) {
                    driverId = evDriver.text.toString().toInt()
                }
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
                        Log.e("ADBILOGSTOLOS", t.message.toString())
                    }
                })
            }
            //endregion
        }
    }
}