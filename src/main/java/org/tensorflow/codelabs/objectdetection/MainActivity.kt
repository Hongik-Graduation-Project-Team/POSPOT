/**
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.codelabs.objectdetection

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.codelabs.objectdetection.ml.Resnet
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity(){
    companion object {
        const val TAG = "TFLite - ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        const val REQ_PERMISSION_CAMERA: Int = 1001
    }
    private lateinit var currentPhotoPath: String
    private lateinit var maxResLabel: String
    private lateinit var maxYoloLabel: ArrayList<String>
    private lateinit var horizon1: Animation
    private lateinit var horizon2: Animation
    private lateinit var horizon3: Animation
    private lateinit var horizon4: Animation
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overridePendingTransition(0, 0)

        maxYoloLabel = arrayListOf("zero")

        horizon1 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter1)
        horizon2 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter2)
        horizon3 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter3)
        horizon4 = AnimationUtils.loadAnimation(this,R.anim.horizon_enter4)

        text.startAnimation(horizon1)
        btn_pose.startAnimation(horizon2)
        btn_spot.startAnimation(horizon3)
        btn_manual.startAnimation(horizon4)

        val poseThread = PoseRequestThread()
        poseThread.start()

        // ?????????
        btn_pose.setOnClickListener(View.OnClickListener {
            try {
                if(cameraPermissionGranted()) {

                    dispatchTakePictureIntent()

                }
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, e.message.toString())
            }
        })
        // SpotActivity
        btn_spot.setOnClickListener(View.OnClickListener {
            val spotIntent = Intent(this, SpotActivity::class.java)
            startActivity(spotIntent)
            overridePendingTransition(0, 0)
        })
        // manualActivity (How to use)
        btn_manual.setOnClickListener(View.OnClickListener{
            val manualIntent = Intent(this, ManualActivity::class.java)
            startActivity(manualIntent)
            overridePendingTransition(0, 0)
        })

    }
    // ???????????? ???????????? ??? ????????? ?????? ??????
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //?????????
        if (resultCode == Activity.RESULT_OK &&
            requestCode == REQUEST_IMAGE_CAPTURE) {
            val cameraIntent = Intent(this, CameraActivity::class.java)
            showLoadingDialogAndMakeLabel(getCapturedImage(),cameraIntent)
        }
    }
    //??????????????? ??????, ????????? ??????, ?????? ??????
    private fun showLoadingDialogAndMakeLabel(bitmap: Bitmap,resultIntent: Intent) {
        val dialog = LoadingDialog(this@MainActivity)
        CoroutineScope(Dispatchers.Main).launch{
            dialog.show()
            delay(100)
            setViewAndDetectYolo(bitmap)
            setViewAndDetectResnet(bitmap)
            delay(1)
            dialog.dismiss()
            resultIntent.putExtra("res", maxResLabel)
            resultIntent.putExtra("yolo", maxYoloLabel)
            startActivity(resultIntent)
            overridePendingTransition(0, 0)
            maxYoloLabel.clear()
            maxYoloLabel.add("zero")
        }
    }
    //-------------------------------------------------------------------------------------
    //                                         ?????????
    //-------------------------------------------------------------------------------------
    // Yolo ??????
    private fun setViewAndDetectYolo(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.5f)
            .build()

        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "model.tflite", // Yolo ??????
            options
        )

        // ?????? ?????? ??? ????????? ??????
        val results = detector.detect(image)
        // ????????????
        val resultToDisplay = results.map {
            // ?????????(first)???????????? ???????????? ????????????
            val category = it.categories.first()
            val text = category.label
            // boundingBox??? ??????
            DetectionResult(it.boundingBox, text)
        }
        resultToDisplay.forEach {
            maxYoloLabel.add(it.text)
        }
    }
    // resnet ??????
    private fun setViewAndDetectResnet(bitmap: Bitmap){
        val model = Resnet.newInstance(this)
        // input ??????
        val image = TensorImage.fromBitmap(bitmap)
        // ?????? ?????? ??? ????????? ??????
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        // ?????? ?????? score??? ??? label
        maxResLabel = probability.maxByOrNull { it!!.score }?.label!!
        // ?????? ??????
        model.close()
    }
    //-------------------------------------------------------------------------------------
    //                                         ?????????
    //-------------------------------------------------------------------------------------
    // capture ?????? ?????? ??? ???????????? ?????????
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // ????????? ?????? ??? ??????(??????) ??????
            takePictureIntent.resolveActivity(packageManager)?.also {
                // ?????? ?????? ??????
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }
                //?????? ?????? ?????? ???
                photoFile?.also {
                    // path??? ?????? ?????? ??????
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "org.tensorflow.codelabs.objectdetection.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    //onActivityResult??? ??????(???????????? ????????? Main???????????? ?????????)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                }
            }
        }
    }
    // ????????? ?????? ?????????????????? ?????? -> ????????? ?????? ????????? ???????????? ??????
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // ????????????????????? ??????
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // ???????????? : ACTION_VIEW intents ?????? ?????? ??? ??????
            currentPhotoPath = absolutePath
        }
    }
    // ????????? ???????????? ?????? ????????? ?????? ??????
    private fun getCapturedImage(): Bitmap {
        // ???????????? ??????
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val targetW: Int = size.x
        val targetH: Int = size.y

        val bmOptions = BitmapFactory.Options().apply {
            // ???????????? ?????? ?????????
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // ????????? ?????? ?????? ??????
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            // ??????????????? ???????????? ???????????? ?????????????????? ?????????
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }
    // ????????? ????????? ?????? ??? ????????? ??????
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
    //-------------------------------------------------------------------------------------
    //                                         ??????
    //-------------------------------------------------------------------------------------
    // ?????? ?????? ??????
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this,"????????? ?????? ?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // ?????? ??????
    private fun cameraPermissionGranted(): Boolean {
        val preference = getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // ????????? ?????? ??? ???????????? ??????
                val snackBar = Snackbar.make(main_layout, "????????? ???????????????", Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction("????????????") {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(
                            Manifest.permission.CAMERA
                        ), REQ_PERMISSION_CAMERA)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // ?????? ???????????? ????????? ??????
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    // ????????????
                    ActivityCompat.requestPermissions(this,
                        arrayOf(
                            Manifest.permission.CAMERA
                        ), REQ_PERMISSION_CAMERA)
                } else {
                    // ???????????? ????????? ??????????????? ?????? ???????????? ????????? ????????? ??????
                    // requestPermission??? ???????????? ?????? ???????????? ?????? ????????? ??????????????? ??????
                    val snackBar = Snackbar.make(main_layout, "??????????????? ??????????????? ????????? ???????????? ???????????????", Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction("??????") {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }
            }
            return false
        } else {
            return true
        }
    }
}

// ????????? ????????? ???????????? class
data class DetectionResult(val boundingBox: RectF, val text: String)
