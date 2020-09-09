package com.example.largefiledownloadexample
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.largefiledownloadexample.workers.CleanupWorker
import com.example.largefiledownloadexample.workers.DownloadWorker
import com.example.largefiledownloadexample.workers.FileWorker
import kotlinx.coroutines.*
import java.net.URL
import java.util.concurrent.TimeUnit

val CHUNK_WORKER_TAG = "CHUNK_WORKER"
val FILE_WORKER_TAG = "FILE_WORKER"
val CLEANUP_WORKER_TAG = "CLEANUP_WORKER"
private const val chunkSize = 5248820

class DownloadViewModel(application: Application): AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    internal val outputWorkInfos: LiveData<List<WorkInfo>>

    private val _chunkWorkIds = MutableLiveData<List<String>>()
    val chunkWorkIds: LiveData<List<String>>
    get() = _chunkWorkIds

    init {
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(CHUNK_WORKER_TAG)
        _chunkWorkIds.value = listOf<String>()
    }

    private val context = getApplication<Application>().applicationContext

     fun cancelDownload() {
        workManager.cancelAllWorkByTag(CHUNK_WORKER_TAG)
        workManager.cancelAllWorkByTag(FILE_WORKER_TAG)
         val cleanupWorker = getCleanupWorker()
         workManager.enqueueUniqueWork(CLEANUP_WORKER_TAG, ExistingWorkPolicy.REPLACE,cleanupWorker)
    }

    fun startDownload(url: String) {
        val fileName = URL(url).file
        val chunkWorkers = getChunkWorkers(url)
        _chunkWorkIds.value = chunkWorkers.map { it.id.toString() }.toList()
        val fileWorker = getFileWorker(fileName)
        val cleanupWorker = getCleanupWorker()
        workManager.beginWith(chunkWorkers)
            .then(fileWorker)
            .then(cleanupWorker)
            .enqueue()
    }

    private fun getFileWorker(fileName: String?): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<FileWorker>()
            .setInputData(workDataOf("fileName" to fileName))
            .addTag(FILE_WORKER_TAG)
            .build()

    private fun getCleanupWorker(): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<CleanupWorker>()
            .addTag(CLEANUP_WORKER_TAG)
            .build()

    private fun getChunkWorkers(url:String) : List<OneTimeWorkRequest> = runBlocking{
        val acceptRanges =
            withContext(Dispatchers.IO) {
                DownloadUtil.getAcceptRanges(url)
            }
        val chunks = DownloadUtil.getChunks(acceptRanges.second, chunkSize)
        return@runBlocking chunks.map { createChunkWorkRequest(url, Pair(it.first, it.second)) }
    }

    private fun createChunkWorkRequest(url: String, range: Pair<Int, Int>): OneTimeWorkRequest {
        val data = Data.Builder()
        data.putString("downloadUrl", url)
        data.putInt("rangeFrom", range.first)
        data.putInt("rangeTo", range.second)
        return OneTimeWorkRequestBuilder<DownloadWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(CHUNK_WORKER_TAG)
            .setInputData(data.build())
            .build()
    }
}