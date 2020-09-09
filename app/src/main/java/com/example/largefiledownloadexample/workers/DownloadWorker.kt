package com.example.largefiledownloadexample.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.largefiledownloadexample.DownloadUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class DownloadWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override suspend fun doWork(): Result {
        try {
            val url = inputData.getString("downloadUrl") ?: return Result.failure()
            val rangeFrom = inputData.getInt("rangeFrom", 0)
            val rangeTo = inputData.getInt("rangeTo", 0)

            val outputFile = File(
                applicationContext.filesDir,
                "$rangeFrom"
            )

            if (!outputFile.createNewFile()) {
                Result.failure(workDataOf("Error" to "File not created"))
            }
            withContext(Dispatchers.IO) {
                DownloadUtil.downloadFile(url, outputFile, Pair(rangeFrom, rangeTo))
            }

           return Result.success()
        } catch (e: IOException) {
            return Result.retry()
        } catch (e: java.lang.Exception) {
            return Result.failure()
        }
    }
}

