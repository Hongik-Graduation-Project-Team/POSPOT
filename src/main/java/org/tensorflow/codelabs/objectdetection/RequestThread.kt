package org.tensorflow.codelabs.objectdetection

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
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

private const val TAG_JSON = "posedata"
private const val TAG_JSON_2 = "spotdata"
private const val TAG_ADDRESS = "address"
private const val TAG_LINK = "link"
private const val TAG_REALADDRESS = "realaddress"
private const val TAG_NAME = "name"

class PoseRequestThread : Thread(){

    override fun run() {
        val serverURL = "http://3.35.171.19/test.php"

        var postParameters = ""
        for (i in 0 until LabelData.yolo.size ) {
            if(i>0) postParameters += "&"
            postParameters += "label" + (i+1) + "=" + LabelData.yolo[i]
        }

        postParameters += "&scene="+LabelData.resnet

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
        ArrayListData.mArrayListPose.clear()
        LabelData.yolo.clear()
        val jsonObject = JSONObject(mJsonString)
        val jsonArray = jsonObject.getJSONArray(TAG_JSON)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val address = item.getString(TAG_ADDRESS)
            val name = item.getString(TAG_NAME)
            val hashMap = HashMap<String, String>() //
            hashMap[TAG_NAME] = name
            hashMap[TAG_ADDRESS] = address
            ArrayListData.mArrayListPose.add(hashMap)
        }
    } catch (e: JSONException) {
    }
}

class SpotRequestThread : Thread() {
    override fun run() {
        val serverURL = "http://3.35.171.19/test3.php"
        val url = URL(serverURL)
        var postParameters = ""

        for (i in 0 until LabelData.yolo.size ) {
            if(i>0) postParameters += "&"
            postParameters += "label" + (i+1) + "=" + LabelData.yolo[i]
        }
        postParameters += "&scene="+ LabelData.resnet

        Log.d(TAG_JSON_2, postParameters)
        try {
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
            Log.d(TAG_JSON_2, responseStatusCode.toString())
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
        ArrayListData.mArrayListSpot.clear()
        LabelData.yolo.clear()
        val jsonObject = JSONObject(mJsonString)
        val jsonArray = jsonObject.getJSONArray(TAG_JSON_2)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val address = item.getString(TAG_ADDRESS)
            val realaddress = item.getString(TAG_REALADDRESS)
            val link = item.getString(TAG_LINK)
            val name = item.getString(TAG_NAME)
            val hashMap = HashMap<String, String>()

            hashMap[TAG_ADDRESS] = address
            hashMap[TAG_REALADDRESS] = realaddress
            hashMap[TAG_LINK] = link
            hashMap[TAG_NAME] = name
            ArrayListData.mArrayListSpot.add(hashMap)
        }
    } catch (e: JSONException) {
    }
}