package com.example.coroutinesstudying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutinesstudying.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        main()
        binding.jobButton.setOnClickListener {
            job.cancel()
        }
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.i("debug", "Exception thrown in one of the children: $exception")
    }

    private fun main() {
        val parentJob = CoroutineScope(IO).launch(handler) {
            val jobA = launch {
                val resultA = getResult(1)
                Log.i("debug", "resultA: $resultA")
            }
            jobA.invokeOnCompletion {
                if (it != null) {
                    Log.i("debug", "Error getting resultA : $it")
                }
            }

            val jobB = launch {
                val resultB = getResult(2)
                Log.i("debug", "resultB: $resultB")
            }
            jobB.invokeOnCompletion {
                if (it != null) {
                    Log.i("debug", "Error getting resultB: $it")
                }
            }

            val jobC = launch {
                val resultC = getResult(3)
                Log.i("debug", "resultC: $resultC")
            }
            jobC.invokeOnCompletion {
                if (it != null) {
                    Log.i("debug", "Error getting resultC : $it")
                }
            }
        }
        parentJob.invokeOnCompletion {
            if (it != null) {
                Log.i("debug", "Parent Job failed : # $it")
            } else {
                Log.i("debug", "Success in parent job")
            }
        }
    }

    private suspend fun getResult(number: Int): Int {
        delay(number.times(500L))
        if (number == 2) {
            throw CancellationException("Error getting result for number $number")
        }
        return number * 2
    }

    private fun printLn(message: String) {
        Log.i("debug", message)
    }

}