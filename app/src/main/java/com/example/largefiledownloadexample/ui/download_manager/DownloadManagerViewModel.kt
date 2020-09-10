package com.example.largefiledownloadexample.ui.download_manager

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.example.largefiledownloadexample.utils.FileUtils
import java.net.URL

class DownloadManagerViewModel(application: Application) : AndroidViewModel(application) {
    fun startDownloadManager(url: String){
        val fileName = URL(url).file.replace("/", "")
        val file = FileUtils.createFileAtDownloadsFolder(fileName)

        val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(file))
                .setTitle(fileName)
                .setDescription("Downloading $fileName")
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        } else {
            DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(file))
                .setTitle(fileName)
                .setDescription("Downloading $fileName")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        }

        val downloadManager = getApplication<Application>()
            .applicationContext
            .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

    }

}