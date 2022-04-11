package com.example.networktest.model.remote

import android.net.Uri
import android.util.Log
import com.example.networktest.model.BookItem
import com.example.networktest.model.BooksResponse
import com.example.networktest.model.VolumeInfo
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "Network"

class Network {

//https://www.googleapis.com/books/v1/volumes?q=pride+prejudice&maxResults=5&printType=books
//base url = "https://www.googleapis.com/"
//end_point = "books/v1/volumes"
//?
//params = [q,maxResult,printType]

    private val BASE_URL = "https://www.googleapis.com/"
    private val END_POINT = "books/v1/volumes"
    private val PARAM_Q = "q"
    private val PARAM_RESULT = "maxResult"
    private val PARAM_PRINT_TYPE = "printType"

    fun executeNetworkCall(
        bookQuery: String,
        limit: Int,
        printType: String
    ): BooksResponse? {

        val uri = Uri.parse(BASE_URL + END_POINT).buildUpon()
            .appendQueryParameter(PARAM_Q, bookQuery)
            .appendQueryParameter(PARAM_PRINT_TYPE, printType)
            .appendQueryParameter(PARAM_RESULT, limit.toString())
            .build()

        val url = URL(uri.toString())

        val httpUrlConnection =
            url.openConnection() as HttpURLConnection

        httpUrlConnection.connectTimeout = 10000
        httpUrlConnection.readTimeout = 15000
        httpUrlConnection.requestMethod = "GET"
        httpUrlConnection.doInput = true

        httpUrlConnection.connect()

        val isResponse = httpUrlConnection.inputStream
        val responseCode = httpUrlConnection.responseCode

        Log.d(TAG, "executeNetworkCall: ISResponse= $isResponse")
        Log.d(TAG, "executeNetworkCall: ResponseCode= $responseCode")

        val stringResponse = parseInputStream(isResponse)


        return transformStringToBooksResponse(
            stringResponse
        )
    }

    private fun parseInputStream(input: InputStream): String {
        val result = StringBuilder()
        val reader = BufferedReader(InputStreamReader(input))

        var currentLine = reader.readLine()

        while (currentLine != null) {
            result.append(currentLine)
            currentLine = reader.readLine()
        }

        return if (result.isEmpty()) "" else result.toString()
    }

    private fun transformStringToBooksResponse(input: String): BooksResponse {
        val jsonObject = JSONObject(input)
        val itemsArray =
            jsonObject.getJSONArray("items")

        val listOfBookItem = mutableListOf<BookItem>()
        for (i in 0 until itemsArray.length()) { // 0 to N-1
            val currentItem = itemsArray.getJSONObject(i)
            val currentVolumeInfo = currentItem.getJSONObject("volumeInfo")
            val currentTitle = currentVolumeInfo.getString("title")
            val currentAuthors = currentVolumeInfo.getJSONArray("authors")
            val listOfAuthors = mutableListOf<String>()
            for (j in 0 until currentAuthors.length()) {
                val currentAuthor = currentAuthors.get(j)
                listOfAuthors.add(currentAuthor.toString())
            }
            listOfBookItem.add(
                BookItem(
                    VolumeInfo(
                        currentTitle,
                        listOfAuthors
                    )
                )
            )
        }
        return BooksResponse(listOfBookItem)
    }
}




