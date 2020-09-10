package com.example.largefiledownloadexample.utils

import android.Manifest

val CHUNK_WORKER_TAG = "CHUNK_WORKER"
val FILE_WORKER_TAG = "FILE_WORKER"
val CLEANUP_WORKER_TAG = "CLEANUP_WORKER"
const val CHUNK_SIZE = 10000000
const val REQUEST_CODE_PERMISSIONS = 10
val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.INTERNET,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)
