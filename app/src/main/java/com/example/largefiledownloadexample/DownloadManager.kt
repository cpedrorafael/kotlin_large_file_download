package com.example.largefiledownloadexample

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.largefiledownloadexample.workers.DownloadWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val chunkSize = 5248820
class DownloadManager(private val url: String, private val context: Context, val onProgressUpdated: (progress: Int) -> Unit) {
    private val acceptRange: Pair<Boolean, Int> = DownloadUtil.getAcceptRanges(url)
    var progress: Int = 0
    var totalTasks = 0
    var completedTasks = 0

    fun startDownload(){
        val chunks = getChunks(acceptRange.second, chunkSize)
        totalTasks = chunks.size
        CoroutineScope(IO).launch {
            if(chunks.size == 1) {
                startDownloadCoroutine(Pair(0,0))
                return@launch
            }
            chunks.forEach {
                startDownloadCoroutine(Pair(it.first, it.second))
            }
        }
    }

    private fun getChunks(value: Int, chunkSize: Int): List<Pair<Int, Int>>{
        val list: MutableList<Pair<Int,Int>> = ArrayList()
        for (i in 0 until value step chunkSize){
            list.add(Pair(i + 1, Math.min(value, i + chunkSize)))
        }
        return list
    }

    private fun startDownloadCoroutine(range: Pair<Int, Int>) {
        val task = DownloadTask(context, UUID.randomUUID(), range){
            completedTasks += 1
            progress = (completedTasks / totalTasks) * 100
            onProgressUpdated(progress)
        }
        task.startDownload(url)
    }
}

private class DownloadTask(private val context: Context, private val workId: UUID, private val range: Pair<Int, Int>, val callback: () -> Unit) {
    var isCompleted = false

    fun cancelTask() {
//        WorkManager.getInstance(context).cancelWorkById(workId)
        WorkManager.getInstance(context).cancelUniqueWork(workId.toString())
    }

    fun startDownload(url: String) {
        val workRequest: WorkRequest = createWorkRequest(url)
        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(workId.toString(), ExistingWorkPolicy.KEEP, workRequest as OneTimeWorkRequest)
        startDownloadObserver(workRequest)
    }

    private fun startDownloadObserver(workRequest: OneTimeWorkRequest) {
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id).observeForever {
            if (it.state == WorkInfo.State.SUCCEEDED) {
                Log.i("Download", "Completed")
                isCompleted = true
                callback()
                WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id).removeObserver { }
            }
        }
    }

    private fun createWorkRequest(url: String): WorkRequest =
        OneTimeWorkRequestBuilder<DownloadWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(CHUNK_WORKER_TAG)
            .setInputData(workDataOf("downloadUrl" to url))
            .setInputData(workDataOf("rangeFrom" to range.first))
            .setInputData(workDataOf("rangeTo" to range.second))
            .build()

}