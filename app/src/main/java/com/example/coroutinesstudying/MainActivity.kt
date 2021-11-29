package com.example.coroutinesstudying

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.example.coroutinesstudying.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


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
            if (!::job.isInitialized) {
                initJob()
            }
            binding.jobProgressBar.startJobOrCancel(job)

        }
    }

    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            resetJob()
            println("This job already cancelled")
        } else {
            binding.jobButton.setText("Cancel Job #1")
            CoroutineScope(IO + job).launch {
                print("coroutine $this is actived with job $job")

                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompletedTextView("Job Completed")
            }
        }
    }

    private fun updateJobCompletedTextView(text: String?) {
        GlobalScope.launch(Main) {
            binding.jobCompleteText.text = text
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    fun initJob() {
        binding.run {
            jobButton.setText("Start Job #1")
            updateJobCompletedTextView("")
            job = Job()
            job.invokeOnCompletion {
                it?.message.let {
                    var msg = it
                    if (it.isNullOrBlank()) {
                        msg = "Unknown error"
                    }
                    print("${job} was cancelled .Reason: $msg")
                    showToast(msg)
                }
            }
            binding.jobProgressBar.max = PROGRESS_MAX
            binding.jobProgressBar.progress = PROGRESS_START

        }

    }

    private fun showToast(text: String?) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
        }
    }
}