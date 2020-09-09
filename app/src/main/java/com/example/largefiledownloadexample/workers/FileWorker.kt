package com.example.largefiledownloadexample.workers

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File


class FileWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters){
    override suspend fun doWork(): Result {
        return try {
            var fileName = inputData.getString("fileName") ?: return Result.failure()
            fileName = fileName.replace("/", "")
            val outputDir = File(
                applicationContext.filesDir, ""
            )
            val arrays = outputDir.listFiles()!!.sortedBy { it.name.toInt() }.toList().map { it.readBytes() }
            val joinedArray = arrays.reduce { acc, bytes -> acc + bytes }
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName).writeBytes(joinedArray)
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

}