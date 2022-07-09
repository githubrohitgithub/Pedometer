package de.rohit.pedometer.weather

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import de.rohit.pedometer.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var highTemp: TextView? = null
    private var lowTemp: TextView? = null
    private var midTemp: TextView? = null
    private var humidity: TextView? = null
    private var presure: TextView? = null
    private lateinit var getloc: AppCompatButton
    private var ProgressBar: ProgressBar? = null
    private var cardView: CardView? = null
    var LATITUDE = 0.0
    var LONGITUDE = 0.0
    private val REQUEST_CODE_LOCATION_PERMISSION = 1

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        highTemp = findViewById(R.id.tv_tempMax)
        lowTemp = findViewById(R.id.tv_tempMin)
        midTemp = findViewById(R.id.tv_tempMid)
        humidity = findViewById(R.id.tv_humidity)
        presure = findViewById(R.id.tv_presure)
        ProgressBar = findViewById(R.id.ProgressBar)
        cardView = findViewById(R.id.cardView)
        getloc = findViewById(R.id.btnGetloc)


       // getLocation()

        checkp()


        getloc.setOnClickListener {

            checkp()
        }


    }



    private fun getCurrentData(lat:String,lon:String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeatherData(lat, lon, AppId)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!


                    ProgressBar?.visibility=View.GONE
                    cardView?.visibility=View.VISIBLE

                    lowTemp?.text = (weatherResponse.main!!.temp_min - 273).toString().substring(0,3) + " ºC"+ " Low"
                    highTemp?.text = (weatherResponse.main!!.temp_max - 273).toString().substring(0,3) + " ºC"+ " High"
                    midTemp?.text = (weatherResponse.main!!.temp - 273).toString().substring(0,3) + " ºC"+ " Normal"
                    humidity?.text = weatherResponse.main.humidity.toString()+ " Humidity"
                    presure?.text = weatherResponse.main.pressure.toString()+ " Presure"



//                    val stringBuilder = Html.fromHtml("<b>País:</b> " +
//                            weatherResponse.sys!!.country +
//                            "<br>" +
//                            "<b>Temperatura:</b> " +
//                            (weatherResponse.main!!.temp - 273).toString().substring(0,3) + " ºC" +
//                            "<br>" +
//                            "<b>Temperatura(Min):</b> " +
//                            (weatherResponse.main!!.temp_min - 273).toString().substring(0,3) + " ºC" +
//                            "<br>" +
//                            "<b>Temperatura(Max):</b> " +
//                            (weatherResponse.main!!.temp_max - 273).toString().substring(0,3) + " ºC" +
//                            "<br>" +
//                            "<b>Humedad:</b> " +
//                            weatherResponse.main!!.humidity +
//                            "<br>" +
//                            "<b>Presión:</b> " +
//                            weatherResponse.main!!.pressure)


                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {

                Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
            }
        })
    }


    companion object {

        var BaseUrl = "https://api.openweathermap.org/"
        var AppId = "49dd3a9e20fef83adb7bfc0143e92593"
        var deflat = "12.9716"
        var deflon = "77.5946"
    }


    override fun onStart() {
        super.onStart()

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (!report.areAllPermissionsGranted()) {
                        Toast.makeText(baseContext, "Not all permissions granted.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()


    }

    private fun checkp() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        } else {
            getcurrentLocation()
        }
    }

    private fun getcurrentLocation() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Wait..Getting your location.Please Turn on")
        progressDialog.show()
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (!report.areAllPermissionsGranted()) {
                            Toast.makeText(
                                baseContext,
                                "Not all permissions granted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val latestLocationIndex = locationResult.locations.size - 1
                        LATITUDE =
                            locationResult.locations[latestLocationIndex].latitude
                        LONGITUDE =
                            locationResult.locations[latestLocationIndex].longitude


                        getCurrentData(LATITUDE.toString(), LONGITUDE.toString())

                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                }
            }, Looper.getMainLooper())
    }


}
