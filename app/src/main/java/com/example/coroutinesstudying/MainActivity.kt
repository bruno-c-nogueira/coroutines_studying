package com.example.coroutinesstudying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.example.coroutinesstudying.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000
    private lateinit var job: CompletableJob

    private val TIMEOUT = 1900L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.jobButton.setOnClickListener {
            updateJobCompletedTextView("Clicked")
            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {
        GlobalScope.launch(IO) {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    Log.i("debug", "launching job 1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }.await()
                updateJobCompletedTextView(result1)
                val result2 = async {
                    Log.i("debug", "launching job 2: ${Thread.currentThread().name}")
                    try {
                        getResult2FromApi(result1)
                    }catch (e: Exception){
                        e.message
                    }
                }.await()
                updateJobCompletedTextView(result2)
                Log.i("debug", "Ended here $result2")
            }
            Log.i("debug", "Debug: total elapsed time: $executionTime ms")
        }
    }

    private fun updateJobCompletedTextView(text: String?) {
        GlobalScope.launch(Main) {
            binding.jobCompleteText.text = text
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "RESULT 1#"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(2000)
        if (result1 == "RESULT 1#") {
            return "$result1 - RESULT 2#"
        }
        throw CancellationException("Result 1 was incorrect")
    }


}