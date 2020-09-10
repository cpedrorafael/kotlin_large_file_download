package com.example.largefiledownloadexample.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.largefiledownloadexample.utils.DownloadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class DownloadWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override suspend fun doWork(): Result {
        return try {
            if (isStopped) Result.failure()
            val url = inputData.getString("downloadUrl") ?: return Result.failure()
            val rangeFrom = inputData.getInt("rangeFrom", 0)
            val rangeTo = inputData.getInt("rangeTo", 0)

            val outputFile = createOutputChunkFile(rangeFrom)

            if (!outputFile.createNewFile()) {
                Result.failure(workDataOf("Error" to "File not created"))
            }

            downloadChunk(url, outputFile, rangeFrom, rangeTo)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun downloadChunk(
        url: String,
        outputFile: File,
        rangeFrom: Int,
        rangeTo: Int
    ) {
        withContext(Dispatchers.IO) {
            DownloadUtils.downloadFile(url, outputFile, Pair(rangeFrom, rangeTo))
        }
    }

    private fun createOutputChunkFile(rangeFrom: Int): File {
        return File(
            applicationContext.filesDir,
            "$rangeFrom"
        )
    }
}

