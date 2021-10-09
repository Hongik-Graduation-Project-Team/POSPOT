package org.tensorflow.codelabs.objectdetection

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.util.HashMap
import java.net.URL
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.ArrayList

// manifest에 추가

var mArrayListPose = ArrayList<HashMap<String, String>>()
var mArrayListSpot = ArrayList<HashMap<String, String>>()

private const val TAG_JSON = "posedata"
private const val TAG_JSON_2 = "spotdata"
private const val TAG_ID = "id"
private const val TAG_ADDRESS = "address"
private const val TAG_EXPLAIN = "explain"
private const val TAG_REALADDRESS = "realaddress"

class PoseRequestThread : Thread() {
    override fun run() {
        // searchKeyword1에 보낼 라벨
        val searchKeyword1 = "chair"
        val serverURL = "http://3.35.171.19/query.php"
        val postParameters = "label1=$searchKeyword1"
        try {
            val url = URL(serverURL)
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setReadTimeout(5000)
            httpURLConnection.setConnectTimeout(5000)
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.setDoInput(true)
            httpURLConnection.connect()
            val outputStream: OutputStream = httpURLConnection.getOutputStream()
            outputStream.write(postParameters.toByteArray(charset("UTF-8")))
            outputStream.flush()
            outputStream.close()
            val responseStatusCode: Int = httpURLConnection.getResponseCode()
            val inputStream: InputStream
            inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                httpURLConnection.getInputStream()
            } else {
                httpURLConnection.getErrorStream()
            }
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            bufferedReader.close()

            poseShowResult(sb.toString())
        } catch (e: Exception) {
            Log.e("error","requestthread Error",e)
        }
    }
}

fun poseShowResult(mJsonString: String) {
    try {
        val jsonObject = JSONObject(mJsonString)
        val jsonArray = jsonObject.getJSONArray(TAG_JSON)
        Log.i("000000","showresult: "+jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.getString(TAG_ID)
            val address = item.getString(TAG_ADDRESS)
            val hashMap = HashMap<String, String>() //
            hashMap[TAG_ID] = id
            hashMap[TAG_ADDRESS] = address
            mArrayListPose.add(hashMap)
        }
    } catch (e: JSONException) {
    }
}

class SpotRequestThread : Thread() {
    override fun run() {
        // searchKeyword1에 보낼 라벨
        val searchKeyword1 = "chair"
        val serverURL = "http://3.35.171.19/query.php"
        val postParameters = "label1=$searchKeyword1"
        try {
            val url = URL(serverURL)
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setReadTimeout(5000)
            httpURLConnection.setConnectTimeout(5000)
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.setDoInput(true)
            httpURLConnection.connect()
            val outputStream: OutputStream = httpURLConnection.getOutputStream()
            outputStream.write(postParameters.toByteArray(charset("UTF-8")))
            outputStream.flush()
            outputStream.close()
            val responseStatusCode: Int = httpURLConnection.getResponseCode()
            val inputStream: InputStream
            inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                httpURLConnection.getInputStream()
            } else {
                httpURLConnection.getErrorStream()
            }
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            bufferedReader.close()

            spotShowResult(sb.toString())
        } catch (e: Exception) {
            Log.e("error","requestthread Error",e)
        }
    }
}

fun spotShowResult(mJsonString: String) {
    try {
        val jsonObject = JSONObject(mJsonString)
        val jsonArray = jsonObject.getJSONArray(TAG_JSON_2)
        Log.i("000000","showresult: "+jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val id = item.getString(TAG_ID)
            val address = item.getString(TAG_ADDRESS)
            val realaddress = item.getString(TAG_REALADDRESS)
            val explain = item.getString(TAG_EXPLAIN)
            val hashMap = HashMap<String, String>() //
            hashMap[TAG_ID] = id
            hashMap[TAG_ADDRESS] = address
            hashMap[TAG_REALADDRESS] = realaddress
            hashMap[TAG_EXPLAIN] = explain
            mArrayListSpot.add(hashMap)
        }
    } catch (e: JSONException) {
    }
}