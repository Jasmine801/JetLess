package com.example.jetless

import android.app.DownloadManager.Request
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jetless.data.WeatherModel
import com.example.jetless.screens.MainCard
import com.example.jetless.screens.TabLayout
import com.example.jetless.ui.theme.JetLessTheme
import org.json.JSONObject

/*import com.meter_alc_rgb*/

const val API_KEY = "18aeadf901a7413fb00184735231408"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetLessTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherModel(
                            "",
                            "",
                            "0.0",
                            "",
                            "",
                            "0.0",
                            "0.0",
                            "",
                        )
                    )
                }
                getData("Moscow", this, daysList, currentDay)

                Image(
                    painter = painterResource(id = R.drawable.skyscreen),
                    contentDescription = "im1",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard(currentDay)
                    TabLayout(daysList, currentDay)
                }
            }
        }
    }
}

fun getData(city: String, context: Context,
            dayList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>){
     val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
             "&q=$city" +
             "&days=" +
             "3" +
             "&aqi=no&alerts=no\n"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        com.android.volley.Request.Method.GET,
        url,
        {
            response ->
            val weatherModelList = getWeatherByDays(response)
            currentDay.value = weatherModelList[0]
            dayList.value = weatherModelList
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}

fun getWeatherByDays(response: String) : List<WeatherModel>{
    if(response.isEmpty()) return emptyList()

    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()

            )
        )

    }

    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}

private fun getResult(city: String, state: MutableState<String>, context: Context){
    val url = "https://api.weatherapi.com/v1/current.json" +
            "?key=$API_KEY&" +
            "q=$city" +
            "&aqi=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        com.android.volley.Request.Method.GET,
        url,
        {
            response ->
            val obj = JSONObject(response)
            state.value = obj.getJSONObject("current").getString("temp_c")
        },
        {
            error ->
        }

    )
    queue.add(stringRequest)
}