package com.example.largefiledownloadexample.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class CleanupWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        return try {
            val outputDirectory = File(applicationContext.filesDir, "")
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty()) {
                            entry.delete()
                            Log.i("Cleanup:", name)
                        }
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            Log.e("Cleanup" ,exception.message ?: "Error")
            Result.failure()
        }
    }
}
