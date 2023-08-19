package com.example.jetless

import android.app.DownloadManager.Request
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jetless.ui.theme.JetLessTheme

/*import com.meter_alc_rgb*/

const val API_KEY = "18aeadf901a7413fb00184735231408"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetLessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("London", this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, context: Context, modifier: Modifier = Modifier) {
    val state = remember{
        mutableStateOf("Unknown")
    }
    Column(modifier.fillMaxSize()){
        Box(modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Text(text = "Temp in $name = ${state.value}")
        }
        Box(modifier
            .fillMaxHeight()
            .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter){
            Button( onClick = {
                getResult(name, state, context)
                Log.d("MyTag", state.value.toString())
            },
            modifier
                .padding(5.dp)
                .fillMaxWidth()) {

            }
        }
    }

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
            state.value = response
        },
        {
            error ->
        }

    )
    queue.add(stringRequest)
}