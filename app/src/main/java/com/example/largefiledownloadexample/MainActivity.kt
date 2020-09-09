package com.example.largefiledownloadexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.INTERNET,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: DownloadViewModel
    private var isDownloading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)
        if (allPermissionsGranted()) {
            setup()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
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
                    this,
                    "Permissions not granted.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setup() {

        viewModel.outputWorkInfos.observe(this) { workInfoList ->
            val list = workInfoList.filter { it.id.toString() in viewModel.chunkWorkIds.value!! }
            val finished = list.filter {
                    it.state == WorkInfo.State.SUCCEEDED }.size
            val progress = (finished.toDouble() / list.size) * 100
            progressBar.progress = progress.toInt()
            if (progress >= 100)
                finished()
        }


        downloadButton.setOnClickListener {
            if (urlInput.text.isEmpty()) return@setOnClickListener
            if (isDownloading) {
                viewModel.cancelDownload()
                resetUI()
                return@setOnClickListener
            }
            viewModel.startDownload(urlInput.text.toString())
            isDownloading = true
            progressBar.visibility = View.VISIBLE
            (it as Button).text = getString(R.string.cancel)
        }

    }

    private fun resetUI() {
        isDownloading = false
        progressBar.visibility = View.INVISIBLE
        progressBar.progress = 0
        downloadButton.text = "Download"
    }

    private fun finished() {
        resetUI()
    }

}