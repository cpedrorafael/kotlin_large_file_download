package com.example.largefiledownloadexample.utils

import org.junit.Assert.*
import org.junit.Test

class FileUtilsTest{

    @Test
    fun get_chunks_returns_zero_zero(){
        //Arrange
        val fileSize = 0
        val chunkSize = 0

        //Act
        val chunks = FileUtils.getChunks(fileSize, chunkSize)

        //Assert
        assertEquals(chunks.first(), Pair(0, 0))
    }
    @Test
    fun get_chunks_returns_value(){
        //Arrange
        val fileSize = 4
        val chunkSize = 5

        //Act
        val chunks = FileUtils.getChunks(fileSize, chunkSize)

        //Assert
        assertEquals(chunks.size, 1)
    }

    @Test
    fun get_chunks_returns_2_values(){
        //Arrange
        val fileSize = 10
        val chunkSize = 5

        //Act
        val chunks = FileUtils.getChunks(fileSize, chunkSize)

        //Assert
        assertEquals(chunks.size, 2)
    }

    @Test
    fun get_chunks_returns_3_values(){
        //Arrange
        val fileSize = 11
        val chunkSize = 5

        //Act
        val chunks = FileUtils.getChunks(fileSize, chunkSize)

        //Assert
        assertEquals(chunks.size, 3)
    }

    @Test
    fun get_chunks_10_5_returns_0_5_and_6_10(){
        //Arrange
        val fileSize = 10
        val chunkSize = 5

        //Act
        val chunks = FileUtils.getChunks(fileSize, chunkSize)

        //Assert
        assertEquals(chunks.first(), Pair(0, 5))
        assertEquals(chunks.last(), Pair(6, 10))
    }
}