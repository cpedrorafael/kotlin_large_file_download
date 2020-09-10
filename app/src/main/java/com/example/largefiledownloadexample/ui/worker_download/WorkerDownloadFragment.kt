package com.example.largefiledownloadexample.ui.worker_download

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.example.largefiledownloadexample.R
import com.example.largefiledownloadexample.utils.REQUEST_CODE_PERMISSIONS
import com.example.largefiledownloadexample.utils.REQUIRED_PERMISSIONS
import com.example.largefiledownloadexample.utils.showMessage
import kotlinx.android.synthetic.main.worker_download_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WorkerDownloadFragment : Fragment() {

    private lateinit var viewModel: DownloadViewModel
    private var isDownloading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.worker_download_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)
        if (allPermissionsGranted()) {
            setup()
        } else {
            ActivityCompat.requestPermissions(
                activity as Activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setup()
            } else {
                Toast.makeText(
                    context,
                    "Permissions not granted.",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            activity!!.baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setup() {

        viewModel.outputWorkInfos.observe(viewLifecycleOwner) { workInfoList ->
            val list = getCurrentWorkInfos(workInfoList)
            updateUI(checkTasksAreRunning(workInfoList))
            val finished = getFinishedTasks(list)
            val progress = updateProgress(finished, list)
            onTaskFailed(list)
            onSuccess(progress)
        }


        downloadButton.setOnClickListener {
            if (urlInput.text.isEmpty()) return@setOnClickListener
            if (isDownloading) {
                viewModel.cancelDownload()
                updateUI()
                return@setOnClickListener
            }
            updateUI(true)
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.startDownload(urlInput.text.toString())
            }
        }

    }

    private fun onSuccess(progress: Double) {
        if (progress >= 100)
            finished()
    }

    private fun onTaskFailed(list: List<WorkInfo>) {
        if (list.any { it.state == WorkInfo.State.CANCELLED }) {
            viewModel.cancelDownload()
            showMessage(context!!, getString(R.string.downloadFailed))
            updateUI()
        }
    }

    private fun updateProgress(
        finished: List<WorkInfo>,
        list: List<WorkInfo>
    ): Double {
        val progress = (finished.size.toDouble() / list.size) * 100
        progressBar.progress = progress.toInt()
        progressText.text = progress.toInt().toString() + "%"
        return progress
    }

    private fun getFinishedTasks(list: List<WorkInfo>) = list.filter {
        it.state == WorkInfo.State.SUCCEEDED
    }

    private fun checkTasksAreRunning(workInfoList: List<WorkInfo>) =
        workInfoList.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }

    private fun getCurrentWorkInfos(workInfoList: List<WorkInfo>) =
        workInfoList.filter { it.id.toString() in viewModel.chunkWorkIds.value!! }

    private fun updateUI(_isDownloading: Boolean = false) {
        isDownloading = _isDownloading
        urlInput.visibility = if (isDownloading) View.GONE else View.VISIBLE
        progressBar.visibility = if (isDownloading) View.VISIBLE else View.INVISIBLE
        progressBar2.visibility = progressBar.visibility
        progressText.visibility = if (isDownloading) View.VISIBLE else View.INVISIBLE
        if (!isDownloading) progressText.text = "0%"
        if (!isDownloading) progressBar.progress = 0
        downloadButton.text =
            if (isDownloading) getString(R.string.cancel) else getString(R.string.download)
    }

    private fun finished() {
        showMessage(context!!, getString(R.string.downloadSuccess))
        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
        updateUI()
    }

}