package com.example.largefiledownloadexample.utils

class FileUtils {
    companion object{
        fun getChunks(value: Int, chunkSize: Int): List<Pair<Int, Int>>{
            if (chunkSize == 0) return listOf(Pair(0,0))
            val list: MutableList<Pair<Int,Int>> = ArrayList()
            for (i in 0 until value step chunkSize){
                val first = if(i == 0) 0 else i + 1
                list.add(Pair(first, Math.min(value, i + chunkSize)))
            }
            return list
        }

    }
}