package de.j4velin.pedometer.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import de.j4velin.pedometer.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        highTemp = findViewById(R.id.tv_tempMax)
        lowTemp = findViewById(R.id.tv_tempMin)
        midTemp = findViewById(R.id.tv_tempMid)
        humidity = findViewById(R.id.tv_humidity)
        presure = findViewById(R.id.tv_presure)

        getCurrentData();
    }

    private fun getCurrentData() {

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
        var lat = "12.9716"
        var lon = "77.5946"
    }

}
