package com.example.coroutinesstudying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coroutinesstudying.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val RESULT_1 = "RESULT 1#"

    private val TIMEOUT = 1900L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
            setText("CLICKED")
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        withContext(IO) {
            val jobTime = withTimeoutOrNull(TIMEOUT){
                val result1 = getResultOneFromApi()
                setText("Got $result1")

                val result2 = getResultOneFromApi()
                setText("Got $result2")
            }

            if (jobTime == null){
                val cancelMessage = "Job has canceled"
                setText(cancelMessage)
            }

        }
    }

    private suspend fun getResultOneFromApi(): String {
        delay(1000)
        return RESULT_1
    }

    private suspend fun setText(methodName: String) {
        withContext(Main){
            val newText = "${binding.mainText.text}\n$methodName"
            binding.mainText.text = newText
            print(methodName + Thread.currentThread().name)
        }

    }
}