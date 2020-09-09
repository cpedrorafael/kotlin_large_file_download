package com.example.largefiledownloadexample.utils

import android.util.Log
import java.io.*
import java.net.URL
import java.net.URLConnection

class DownloadUtils {
    companion object {
        fun getAcceptRanges(url: String) : Pair<Boolean, Int> {
            try {
                val u = URL(url)
                val conn: URLConnection = u.openConnection()
                val acceptRange = conn.getHeaderField("Accept-Ranges") ?: "none"
                return if(acceptRange != "none") Pair(true, conn.contentLength) else Pair(false, 0)
            } catch (e: IOException) {
                throw e
            }
        }

        fun downloadFile(url: String, outputFile: File, range: Pair<Int,Int>) {
            Log.i("DownloadUtil", "getting range ${range.first} to ${range.second}")
            try {
                val u = URL(url)
                val conn: URLConnection = u.openConnection()
                if(range.second > 0) conn.setRequestProperty("Range", "bytes=${range.first}-${range.second}")
                val contentLength: Int = conn.contentLength
                val stream = DataInputStream(u.openStream())
                val buffer = ByteArray(contentLength)
                stream.readFully(buffer)
                stream.close()
                val fos = DataOutputStream(FileOutputStream(outputFile))
                fos.write(buffer)
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                throw e
            } catch (e: IOException) {
                throw e
            }
        }
    }
}