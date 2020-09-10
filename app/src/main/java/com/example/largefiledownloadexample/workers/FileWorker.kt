package com.example.largefiledownloadexample.workers

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.largefiledownloadexample.utils.FileUtils
import java.io.File


class FileWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            var fileName = inputData.getString("fileName") ?: return Result.failure()
            fileName = clean(fileName)
            val outputDir = getOutputFileDir()
            var fileNameList = mutableListOf<String>()
            getChunkFileNameList(outputDir, fileNameList)
            removeFolderName(fileNameList)
            fileNameList = orderChunkFiles(fileNameList)
            val outputFile = FileUtils.createFileAtDownloadsFolder(fileName)
            deleteCurrentFile(outputFile)
            writeToOutputFile(fileNameList, outputFile, outputDir)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun writeToOutputFile(
        fileNameList: MutableList<String>,
        outputFile: File,
        outputDir: File
    ) {
        fileNameList.forEach {
            outputFile.appendBytes(File(outputDir, it).readBytes())
        }
    }

    private fun removeFolderName(fileNameList: MutableList<String>) {
        fileNameList.removeAt(0)
    }

    private fun deleteCurrentFile(outputFile: File) {
        outputFile.writeBytes(byteArrayOf(0))
    }



    private fun orderChunkFiles(fileNameList: MutableList<String>) =
        fileNameList.sortedBy { it.toInt() }.toMutableList()

    private fun getChunkFileNameList(
        outputDir: File,
        fileNameList: MutableList<String>
    ) {
        outputDir.walk().forEach {
            fileNameList.add(it.name)
        }
    }

    private fun getOutputFileDir(): File {
        return File(
            applicationContext.filesDir, ""
        )
    }

    private fun clean(fileName: String) = fileName.replace("/", "")

}