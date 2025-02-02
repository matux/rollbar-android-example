package com.rollbar.android.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import java.net.HttpURLConnection
import java.net.URL

import com.rollbar.android.Rollbar

import com.rollbar.android.example.ui.theme.RollbarAndroidExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //testEndpoint()
        Rollbar.init(this) { builder ->
            builder.endpoint("http://10.0.2.2:8000/api/1/item/")
            builder.build()
        }
        Rollbar.instance().debug("A debug message")

        Log.d("ASDF", "Endpoint: ${Rollbar.instance().config().endpoint()}")

        enableEdgeToEdge()
        setContent {
            RollbarAndroidExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Center the button in the screen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        ExceptionButton()
                    }
                }
            }
        }
    }
}

@Composable
fun ExceptionButton() {
    Button(onClick = {
        try {
            val result = 10 / 0
        } catch (e: Exception) {
            // Assumes Rollbar is initialized
            Log.d("ASDF", "Endpoint: ${Rollbar.instance().config().endpoint()}")
            Rollbar.instance().error(e, "Division by zero triggered from button")
        }
    }) {
        Text("Tap me for Exception")
    }
}

@Preview(showBackground = true)
@Composable
fun ExceptionButtonPreview() {
    RollbarAndroidExampleTheme {
        ExceptionButton()
    }
}

fun testEndpoint() {
    Thread {
        try {
            val url = URL("http://10.0.2.2:8000/api/1/item/")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val responseCode = conn.responseCode
            Log.d("ASDF", "Response Code: $responseCode")
            conn.inputStream.bufferedReader().use { reader ->
                val response = reader.readText()
                Log.d("ASDF", "Response: $response")
            }
        } catch (e: Exception) {
            Log.e("ASDF", "Error: ${e.message}")
        }
    }.start()
}
